package controllers

import core.auth.Identity
import core.mvc.{WAction, Authorize}
import core.utils.{KeyUtils, MD5Util, CookieUtil}
import form.{UserAuthForm, MemberForm}
import model._
import play.api.libs.Crypto
import play.api.libs.json.Json
import play.api.mvc.{Controller}

/**
  * 方法描述:个人中心
  *
  * author 小刘
  * version v1.0
  * date 2015/11/29
  */
class CenterController extends Controller{

    //个人中心数据
    def center = Authorize{
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        //会员信息
        val member = Member.getMember(dataView.identity.id)
        //我的融资
        val proList = MyProject.myProject(dataView.identity.id)
        //我的投资
        val investList = MyInvest.myInvest(dataView.identity.id)
        //我的活动
        val activityList = Activity.myActivity(dataView.identity.id)
        Ok(views.html.centrer.center(member,proList,investList,activityList))
    }

    //用户名是否注册
    def isRegUsername = WAction{
        implicit dataView =>
        val map = dataView.request.body.asFormUrlEncoded
        //用户名
        val username = map.get.get("username").getOrElse(Seq(""))(0)
        val isReg = RegLogin.hasAccount(username,dataView.identity.accountname)
        if(isReg > 0){
            Ok("""{"code" : 1, "msg" : "用户名已存在"}""").as("text/json")
        }else{
            Ok("""{"code" : 0, "msg" : ""}""").as("text/json")
        }
    }

    //展示个人资料
    def userView = Authorize {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        //职位
        val jobList = TypeClass.typeList("GSZW")
        //会员信息
        val member = Member.getMember(dataView.identity.id)
        Ok(views.html.centrer.user(member,jobList))
    }

    //编辑个人资料
    def editorView = Authorize {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        //职位
        val jobList = TypeClass.typeList("GSZW")
        val member = Member.getMember(dataView.identity.id)
        Ok(views.html.centrer.userViews(member,jobList,MemberForm.memberForm))
    }

    //保存编辑资料
    def saveMemberView = Authorize {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        //职位
        val jobList = TypeClass.typeList("GSZW")
        val member = Member.getMember(dataView.identity.id)
        MemberForm.memberForm.bindFromRequest.fold(
            error => {
                BadRequest(views.html.centrer.userViews(member,jobList,error))
            },
            data => {
                val i = Member.saveMember(data,dataView.identity.id,dataView.identity.accountname)
                if(i == -1){
                    BadRequest(views.html.centrer.userViews(member,jobList,MemberForm.memberForm.withError("isRegUserName","")))
                }else if(i == 0){
                    BadRequest(views.html.centrer.userViews(member,jobList,MemberForm.memberForm.withError("updateError","")))
                }else{
                    val idt = Identity(dataView.identity.id,data.accountname,data.tel)
                    Redirect("/userView").withCookies(CookieUtil.toRememberCookie(KeyUtils.USER_INFO, Crypto.encryptAES(Json.stringify(Json.toJson(idt)))))
                }
            }
        )
    }

    //是否实名认证
    def isRealAuth = WAction {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        val member = Member.getMember(dataView.identity.id)
        Ok(s"""{"status":"${member.get.status.getOrElse(0)}"}""").as("text/json")
    }

    //实名认证
    def authView = Authorize {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        val member = Member.getMember(dataView.identity.id)
        val status = member.get.status.getOrElse("0").toInt
        if(status == 1){
            Ok(views.html.centrer.authFaild(member,UserAuthForm.userAuthForm)).withHeaders(
                PRAGMA -> "No-cache",
                CACHE_CONTROL -> "no-store",
                EXPIRES -> "0"
            )
        }else if(status == 2){
            Ok(views.html.centrer.authSucc(member,UserAuthForm.userAuthForm)).withHeaders(
                PRAGMA -> "No-cache",
                CACHE_CONTROL -> "no-store",
                EXPIRES -> "0"
            )
        }else if(status == 3){
            Ok(views.html.centrer.authExamine(member,UserAuthForm.userAuthForm)).withHeaders(
                PRAGMA -> "No-cache",
                CACHE_CONTROL -> "no-store",
                EXPIRES -> "0"
            )
        }else{
            Ok(views.html.centrer.authView(member,UserAuthForm.userAuthForm)).withHeaders(
                PRAGMA -> "No-cache",
                CACHE_CONTROL -> "no-store",
                EXPIRES -> "0"
            )
        }
    }

    //实名认证为通过/进入修改
    def authFailView = Authorize {
        implicit dataView=>
        //设置TOP下标
        dataView.topIdex = 3
        val member = Member.getMember(dataView.identity.id)
        val status = member.get.status.getOrElse("0").toInt
        if(status == 2){
            Ok(views.html.centrer.authSucc(member,UserAuthForm.userAuthForm))
        }else{
            Ok(views.html.centrer.authView(member,UserAuthForm.userAuthForm))
        }
    }

    //实名认证资料
    def userAuth = Authorize {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        val member = Member.getMember(dataView.identity.id)
        UserAuthForm.userAuthForm.bindFromRequest().fold(
            error => {
                Ok(views.html.centrer.authView(member,error))
            },
            data => {
                //完成实名认证->等待审核
                Member.memberAuth(data,dataView.identity.id)
                Ok(views.html.centrer.authExamine(member,UserAuthForm.userAuthForm))
            }
        )
    }

    //密码
    def password = Authorize {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 3
        val member = Member.getMember(dataView.identity.id)
        Ok(views.html.centrer.passwordView(member,0))
    }

    //更改密码
    def updatePassword = Authorize {
        implicit dataView =>
        //post参数
        val map = dataView.request.body.asFormUrlEncoded
        //手机号
        val phone = map.get.get("tel").head(0)
        //短信验证码
        val sortCode = map.get.get("sortCode").head(0)
        //验证码
        val code = map.get.get("code").head(0)
        //旧密码
        val oldPass = map.get.get("oldPass").getOrElse(Seq(""))(0)
        //密码
        val password = map.get.get("password").head(0)
        //获取旧密码
        val userPass = Member.getUserPass(dataView.identity.id)
        val hash = MD5Util.passMd5(oldPass,userPass._2)

        //短信验证码
        var phoneCode = ""
        if(dataView.request.cookies.get("forgotCode").isDefined){
            phoneCode = Crypto.decryptAES(dataView.request.cookies.get("forgotCode").get.value,"Rforgot")
        }
        //正确的验证码
        val vcode = dataView.request.session.get("vcode").getOrElse("-1")
        //判断手机号是否注册
        val isReg = RegLogin.isReg(phone)
        if(isReg == 0){
            //手机未注册
            val member = Member.getMember(dataView.identity.id)
            Ok(views.html.centrer.passwordView(member,1))
        }else if(!code.equals(vcode)){
            //验证码错误
            val member = Member.getMember(dataView.identity.id)
            Ok(views.html.centrer.passwordView(member,2))
        }else if(!hash.equals(userPass._1)){
            //旧密码不匹配
            val member = Member.getMember(dataView.identity.id)
            Ok(views.html.centrer.passwordView(member,3))
        }else if(!sortCode.equals(phoneCode)){
            //短信验证码错误
            val member = Member.getMember(dataView.identity.id)
            Ok(views.html.centrer.passwordView(member,4))
        }else if(password.length < 6 || password.length > 15){
            //密码格式不正确
            val member = Member.getMember(dataView.identity.id)
            Ok(views.html.centrer.passwordView(member,5))
        }else{
            //修改密码成功
            Member.updatePassword(dataView.identity.id,password)
            Redirect("/regLogin").discardingCookies(CookieUtil.toDiscardingCookie("forgotCode"))
        }
    }
}
