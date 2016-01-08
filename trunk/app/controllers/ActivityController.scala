package controllers

import core.mvc.WAction
import form.{ForgetForm, LoginForm, RegForm}
import model._
import org.joda.time.DateTime
import play.api.libs.Crypto
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import core.utils._

import scala.actors.Actor._

/**
  * Created by 24950 on 2015/11/30.
  * HH
  * 项目管理
  */
class ActivityController extends Controller {

    //查询活动分页
    def activityList(pageNumber:Int) = WAction {
        implicit request =>
        //活动
        val index = (pageNumber - 1) * 10
        val activityList = Activity.activityList(pageNumber, index, 6)
        Ok(views.html.activity.activityListView(activityList))
    }

    /**
      * 获取项目详情
      * @param activityid
      * @return
      */
    def activityinfo(activityid: Long) = WAction {
        implicit request =>
            //活动信息
            val activityinfo = Activity.activityInfo(activityid)
            var isShowBtn = true
            if (System.currentTimeMillis() > activityinfo.endtime.getMillis) {
                isShowBtn = false
            }
            if (null != activityinfo) {
                UV.addUV("2", request.remoteAddress, activityid)
                val memberUser = Member.getMember(request.identity.id)
                val m = MemberInfo(0L, "", "", "", "", "", "", 0L, "")
                //是否已报名
                var isreg = true;
                if (None != memberUser) {
                    m.id = memberUser.get.id
                    m.accountname = memberUser.get.accountname
                    m.name = memberUser.get.name.getOrElse("")
                    m.sex = memberUser.get.sex.getOrElse("男")
                    m.tel = memberUser.get.tel.getOrElse("")
                    m.email = memberUser.get.email.getOrElse("")
                    m.pcname = memberUser.get.pcname.getOrElse("")
                    m.job = memberUser.get.job.getOrElse(0L)
                    m.status = memberUser.get.status.getOrElse("0")
                    //如果是登录用户查询到已报名则将已报名的判断跳转到页面
                    val userlist = ActivityUser.queryUserForActivity(m.id, activityid)
                    if (!userlist.isEmpty) {
                        isreg = false
                    }
                }
                //活动集合
                val activityList = Activity.activityList(1, 0, 3)
                //职位集合
                val jobList = TypeClass.typeList("GSZW")
                //报名用户
                val activityUseList = ActivityUser.getActivityUser(activityinfo.id, 1, 0, 3);
                Ok(views.html.activity.activity(activityinfo, isreg, m, isShowBtn, activityList, activityUseList, jobList, RegForm.regForm, LoginForm.loginForm, ForgetForm.forgetForm))
            } else {
                Redirect("/")
            }
    }

    /**
      * 报名
      * @return
      */
    def registration = WAction {
        implicit dataView =>
            var json = ""
            //post参数
            val map = dataView.request.body.asFormUrlEncoded
            val userid = map.get.get("userid").head(0)
            val activityId = map.get.get("activityId").head(0)
            val name = map.get.get("name").head(0)
            val email = map.get.get("email").head(0)
            val pcname = map.get.get("pcname").head(0)
            val job = map.get.get("job").head(0)
            val sex = map.get.get("sex").head(0)
            val code = map.get.get("code").head(0)

            //短信验证码
            var phoneCode = ""
            if (dataView.request.cookies.get("activityCode").isDefined) {
                phoneCode = Crypto.decryptAES(dataView.request.cookies.get("activityCode").get.value, "RActivityCode")
            }

            if (null == name || "".equals(name)) {
                json = """{"code" : 1, "msg" : "请输入姓名"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            } else if (null == email || "".equals(email)) {
                json = """{"code" : 2, "msg" : "请输入电子邮箱"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            } else if (null == pcname || "".equals(pcname)) {
                json = """{"code" :3, "msg" : "请输入公司名称"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            } else if (!phoneCode.equals("") && !code.equals(phoneCode)) {
                json = """{"code" : 4, "msg" : "验证码不正确"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            }else {
                val memberUser = Member.getMember(userid.toLong)
                if (null != memberUser) {
                    val list = ActivityUser.isRegistration(userid.toLong, activityId.toLong)
                    if (0 <= list.size) {
                        val a = ActivityUser(0L, activityId.toLong, userid.toLong, name, memberUser.get.tel.getOrElse(""), email, pcname, job.toLong, "0", "0", new DateTime())
                        ActivityUser.saveActivityUser(a)
                        val act = ActivityUser.titleAdd(activityId.toLong)
                        actor {
                            val msg = s"""您已成功报名[${act._1}]活动，活动将于活动时间在[${act._2}]举行，请准时参加！【睿就投】"""
                            LSHttpClient.LSsend(dataView.identity.tel,0,msg)
                        }
                        json = """{"code" : 0, "msg" : "报名成功"}"""
                        Ok(Json.stringify(Json.parse(json))).as("text/json")
                    } else {
                        json = """{"code" : 6, "msg" : "不能重复表明"}"""
                        Ok(Json.stringify(Json.parse(json))).as("text/json")
                    }
                } else {
                    json = """{"code" : 5, "msg" : "没有此账号信息"}"""
                    Ok(Json.stringify(Json.parse(json))).as("text/json")
                }
            }
    }

    /**
      * 取消报名
      * @return
      */
    def registrationOut = WAction {
        implicit dataView =>
            var json = ""
            //post参数
            val map = dataView.request.body.asFormUrlEncoded
            val userid = map.get.get("userid").head(0)
            val activityId = map.get.get("activityId").head(0)
            val usertel = map.get.get("usertel").head(0)
            val outcode = map.get.get("outcode").head(0)

            //短信验证码
            var phoneCode = ""
            if (dataView.request.cookies.get("cancelActivityCode").isDefined) {
                phoneCode = Crypto.decryptAES(dataView.request.cookies.get("cancelActivityCode").get.value, "RCancelActivityCode")
            }
            if (null == usertel || "".equals(usertel)) {
                json = """{"code" : 1, "msg" : "请输入正确的手机号"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            } else if (!phoneCode.equals("") && !outcode.equals(phoneCode)) {
                json = """{"code" : 3, "msg" : "验证码不正确"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            }else {
                val memberUser = Member.getMember(userid.toLong)
                if (null != memberUser) {
                    ActivityUser.outActivityUser(userid.toLong, activityId.toLong)
                    val act = ActivityUser.titleAdd(activityId.toLong)
//                    actor {
//                        val msg = s"""您已取消[${act._1}]活动！【睿就投】"""
//                        LSHttpClient.LSsend(dataView.identity.tel,0,msg)
//                    }
                    json = """{"code" : 0, "msg" : "取消成功"}"""
                    Ok(Json.stringify(Json.parse(json))).as("text/json")
                } else {
                    json = """{"code" : 2, "msg" : "没有此账号信息"}"""
                    Ok(Json.stringify(Json.parse(json))).as("text/json")
                }
            }
    }

    //路演
    def roadShow(itemId:Long) = WAction{
        implicit dataView=>
        //项目信息
        val item = ItemProject.itemInfo(itemId)
        //路演信息
        val demo = Demo.itemDemo(itemId)
        Ok(views.html.project.roadShow(item,demo))
    }

}
