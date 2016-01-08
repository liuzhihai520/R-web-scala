package controllers

import core.mvc.WAction
import core.utils.{LSHttpClient, HttpUtils, CookieUtil, RandomUtils}
import model._
import org.joda.time.DateTime
import play.api.libs.Crypto
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * 方法描述:首页
  *
  * author 小刘
  * version v1.0
  * date 2015/11/23
  */
class IndexController extends Controller {

    //登录首页
    def index = WAction{
        implicit request =>
        //设置TOP下标
        request.topIdex = 1
        //Banner
        val bannerList = Banner.bannerList(1,0,8)
        //活动
        val activityList = Activity.activityList(1,0,4)
        //资讯
        val newsList = News.newsList(0,1,0,4)
        //项目
        val itemList = ItemProject.itemList(1,0,6)
        var itemArray = List[CountDown]()
        for(i <- itemList.list){
            itemArray ::= CountDown(i.id,i.endtime.getOrElse(new DateTime()).toString("yyyy/MM/dd HH:mm:ss"))
        }
        //合作伙伴
        val linkList = Link.linkList("HZYQ-HZHB")
        Ok(views.html.index(bannerList,activityList,newsList,itemList,linkList,Json.toJson(itemArray)+""))
    }


    //处理发送短信
    def sendSms = WAction{
        implicit dataView =>
        var json = """{"code":"0","msg":"验证码发送成功"}"""
        val map = dataView.request.body.asFormUrlEncoded
        val telePhone = map.get.get("telePhone").getOrElse(Seq(""))(0)
        val codeType = map.get.get("codeType").getOrElse(Seq(""))(0)
        //查看短信发送是否频繁
        val smsCode = SmsCode.isBusy(telePhone)
        if(telePhone.equals("")){
            json = """{"code":"1","msg":"手机号格式不正确"}"""
            Ok(json).as("text/json")
        }else if(smsCode != None && smsCode.get.types == codeType && (System.currentTimeMillis() - smsCode.get.sendcodetime.toDate.getTime) < 60000){
            json = """{"code":"2","msg":"验证码请求过于频繁,请1分钟后再试"}"""
            Ok(json).as("text/json")
        }else{
            var code = RandomUtils.getRandomInt(1000,9999)
            if(smsCode != None && telePhone.equals(smsCode.get.tel.getOrElse("")) && (System.currentTimeMillis() - smsCode.get.sendcodetime.getMillis) <= 300000){
                code = smsCode.get.code.toInt
            }
            //短信分类
            codeType match {
                //注册
                case "0" =>{
                    val msg = "您的验证码是:【"+code.toString+"】请您于1分钟之内输入。【睿就投】"
                    //螺丝帽
                    val status = LSHttpClient.LSsend(telePhone,code,msg)
                    if(status._1 != 0){
                        json = s"""{"code":"${status._2}","msg":"短信发送失败"}"""
                        Ok(json).as("text/json")
                    }else{
                        SmsCode.isLog(telePhone,code+"",codeType.toInt)
                        Ok(json).withCookies(CookieUtil.toCodeCookie("registerCode",Crypto.encryptAES(code.toString,"Rregister"))).as("text/json")
                    }
                }
                //忘记密码/密码修改
                case "1" =>{
                    val msg = "您的验证码是:【"+code.toString+"】请您于1分钟之内输入。【睿就投】"
                    //螺丝帽
                    val status = LSHttpClient.LSsend(telePhone,code,msg)
                    if(status._1 != 0){
                        json = s"""{"code":"${status._2}","msg":"短信发送失败"}"""
                        Ok(json).as("text/json")
                    }else{
                        SmsCode.isLog(telePhone,code+"",codeType.toInt)
                        Ok(json).withCookies(CookieUtil.toCodeCookie("forgotCode",Crypto.encryptAES(code.toString,"Rforgot"))).as("text/json")
                    }
                }
                //领头/跟投
                case "2" =>{
                    val msg = "您的验证码是:【"+code.toString+"】请您于1分钟之内输入。【睿就投】"
                    //螺丝帽
                    val status = LSHttpClient.LSsend(telePhone,code,msg)
                    if(status._1 != 0){
                        json = s"""{"code":"${status._2}","msg":"短信发送失败"}"""
                        Ok(json).as("text/json")
                    }else{
                        SmsCode.isLog(telePhone,code+"",codeType.toInt)
                        Ok(json).withCookies(CookieUtil.toCodeCookie("investCode",Crypto.encryptAES(code.toString,"Rinvest"))).as("text/json")
                    }
                }
                //取消报名
                case "3" =>{
                    val msg = "您的验证码是:【"+code.toString+"】请您于1分钟之内输入。【睿就投】"
                    //螺丝帽
                    val status = LSHttpClient.LSsend(telePhone,code,msg)
                    if(status._1 != 0){
                        json = s"""{"code":"${status._2}","msg":"短信发送失败"}"""
                        Ok(json).as("text/json")
                    }else{
                        SmsCode.isLog(telePhone,code+"",codeType.toInt)
                        Ok(json).withCookies(CookieUtil.toCodeCookie("cancelInvestCode",Crypto.encryptAES(code.toString,"RCancelInvest"))).as("text/json")
                    }
                }
                //活动报名
                case "4" =>{
                    val msg = "您的验证码是:【"+code.toString+"】请您于1分钟之内输入。【睿就投】"
                    //螺丝帽
                    val status = LSHttpClient.LSsend(telePhone,code,msg)
                    if(status._1 != 0){
                        json = s"""{"code":"${status._2}","msg":"短信发送失败"}"""
                        Ok(json).as("text/json")
                    }else{
                        SmsCode.isLog(telePhone,code+"",codeType.toInt)
                        Ok(json).withCookies(CookieUtil.toCodeCookie("activityCode",Crypto.encryptAES(code.toString,"RActivityCode"))).as("text/json")
                    }
                }
                //取消活动报名
                case "5" =>{
                    val msg = "您的验证码是:【"+code.toString+"】请您于1分钟之内输入。【睿就投】"
                    //螺丝帽
                    val status = LSHttpClient.LSsend(telePhone,code,msg)
                    if(status._1 != 0){
                        json = s"""{"code":"${status._2}","msg":"短信发送失败"}"""
                        Ok(json).as("text/json")
                    }else{
                        SmsCode.isLog(telePhone,code+"",codeType.toInt)
                        Ok(json).withCookies(CookieUtil.toCodeCookie("cancelActivityCode",Crypto.encryptAES(code.toString,"RCancelActivityCode"))).as("text/json")
                    }
                }
            }
        }
    }

  def countBanner= WAction{
    implicit dataView =>
      val json = """{"code":"0","msg":"统计banner"}"""
      val map = dataView.request.body.asFormUrlEncoded
      val bid = map.get.get("bid").head(0)
      UV.addUV("3",dataView.request.remoteAddress,bid.toLong);
      Ok(json).as("text/json")
  }
}
