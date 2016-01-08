package controllers

import core.mvc.WAction
import core.utils.IPUtil
import model.Messages
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}

/**
  * 方法描述:静态
  *
  * author 小刘
  * version v1.0
  * date 2015/12/4
  */
class HtmlController extends Controller{

    //平台答疑
    def FAQ(id:Int = 1,trun:Int) = WAction {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 5
        Ok(views.html.vhtml.FAQ(id,trun))
    }

    //关于我们
    def aboutUs(trun:Int = 0) = WAction {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 6
        Ok(views.html.vhtml.aboutUs(trun))
    }

    //APP
    def APP = WAction {
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 4
        Ok(views.html.vhtml.App())
    }

    //留言
    def msg = WAction {
        implicit dataView =>
        var json = """{"code" : 0, "msg" : "留言成功"}"""
        val map = dataView.request.body.asFormUrlEncoded
        //ip
        val ip = IPUtil.ipAddress(dataView.request)
        //姓名
        val name = map.get.get("name").getOrElse(Seq(""))(0)
        //邮箱
        val email = map.get.get("email").getOrElse(Seq(""))(0)
        //电话
        val tel = map.get.get("tel").getOrElse(Seq(""))(0)
        //留言
        val msg = map.get.get("msg").getOrElse(Seq(""))(0)
        //上次留言时间
        val preMsg = Messages.preMsg(ip)
        if(name.length == 0){
            json = """{"code" : 1, "msg" : "请输入姓名"}"""
        }else if(email.length == 0){
            json = """{"code" : 2, "msg" : "请输入邮箱"}"""
        }else if(tel.length == 0){
            json = """{"code" : 3, "msg" : "请输入电话号码"}"""
        }else if(msg.length < 5){
            json = """{"code" : 4, "msg" : "留言字数应在5字以上"}"""
        }else if(preMsg != None && (System.currentTimeMillis() - preMsg.get.addtime.getOrElse(new DateTime()).getMillis) <= 300000){
            json = """{"code" : 5, "msg" : "留言过于频繁,请5分钟后再次留言"}"""
        }else {
            Messages.saveMsg(name,tel,email,msg,ip,dataView.identity.id)
        }
        Ok(json).as("text/json")
    }
}
