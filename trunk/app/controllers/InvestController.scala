package controllers

import core.mvc.WAction
import core.utils.LSHttpClient
import model.{Invest, ItemProject, MainInfo}
import play.api.libs.Crypto
import play.api.libs.json.Json
import play.api.mvc.Controller

import scala.actors.Actor._

/**
  * 方法描述:描述
  *
  * author 小刘
  * version v1.0
  * date 2015/12/3
  */
class InvestController extends Controller {
    //报名
    def invest = WAction {
        implicit dataView =>
            var json = ""
            //post参数
            val map = dataView.request.body.asFormUrlEncoded
            val name = map.get.get("name").getOrElse(Seq(""))(0)
            val sex = map.get.get("sex").getOrElse(Seq(""))(0)
            val email = map.get.get("email").getOrElse(Seq(""))(0)
            val pcname = map.get.get("pcname").getOrElse(Seq(""))(0)
            val job = map.get.get("job").getOrElse(Seq(0))(0).toString.toLong
            val signCode = map.get.get("signCode").getOrElse(Seq(""))(0)
            val money = map.get.get("money").getOrElse(Seq(0))(0).toString.toLong
            val projectId = map.get.get("projectId").getOrElse(Seq(0))(0).toString.toLong
            val itemName = map.get.get("projectName").getOrElse(Seq(""))(0).toString
            //获取项目起投金额
            val startMoney = ItemProject.startMoney(projectId)
            //短信验证码
            var phoneCode = ""
            if (dataView.request.cookies.get("investCode").isDefined) {
                phoneCode = Crypto.decryptAES(dataView.request.cookies.get("investCode").get.value, "Rinvest")
            }
            if (name.equals("")) {
                json = """{"code" : 1, "msg" : "请输入姓名"}"""
            } else if (sex.equals("")) {
                json = """{"code" : 2, "msg" : "请选择性别"}"""
            } else if (email.equals("")) {
                json = """{"code" : 3, "msg" : "请填写邮箱"}"""
            } else if (pcname.equals("")) {
                json = """{"code" : 4, "msg" : "请填写公司名称"}"""
            } else if (job == 0) {
                json = """{"code" : 5, "msg" : "请选择职位"}"""
            }
            else if (!signCode.equals(phoneCode)) {
                json = """{"code" : 6, "msg" : "短信验证码错误"}"""
            }
            else if (money <= 0) {
                json = """{"code" : 7, "msg" : "请输入整数报名金额"}"""
            } else if (projectId <= 0) {
                json = """{"code" : 8, "msg" : "没有发现可报名的项目"}"""
            } else if (money < startMoney) {
                json = """{"code" : 9, "msg" : "投资金额小于起投金额"}"""
            } else {
                if (0 != dataView.identity.id) {
                    //判断是否报名
                    val isInvest = Invest.isInvest(projectId, dataView.identity.id)
                    //是否过期
                    val isOver = Invest.isOverTime(projectId)
                    if (isInvest == 0) {
                        val status = Invest.invest(projectId, dataView.identity.id, name, sex, dataView.identity.tel, email, pcname, job, money)
                        if (status != -1) {
                            actor {
                                val msg = s"""您已成功报名$itemName，认购金额为${money}万元，敬请留意项目相关信息。【睿就投】"""
                                LSHttpClient.LSsend(dataView.identity.tel,0,msg)
                            }
                            json = """{"code" : 0, "msg" : "报名成功"}"""
                        } else {
                            json = """{"code" : 10, "msg" : "报名失败"}"""
                        }
                    } else if (isOver == 0) {
                        json = """{"code" : 11, "msg" : "报名时间已结束"}"""
                    } else {
                        json = """{"code" : 12, "msg" : "您已经参加过报名,不可重复报名"}"""
                    }
                } else {
                    json = """{"code" : 13, "msg" : "请登录后在报名"}"""
                }
            }
            Ok(Json.stringify(Json.parse(json))).as("text/json")
    }

    //取消报名
    def cancelSignUp = WAction {
        implicit dataView =>
            var json = ""
            val map = dataView.request.body.asFormUrlEncoded
            val projectId = map.get.get("projectId").getOrElse(Seq(0))(0).toString.toLong
            val code = map.get.get("cacelCode").getOrElse(Seq(0))(0)
            //短信验证码
            var phoneCode = ""
            if (dataView.request.cookies.get("cancelInvestCode").isDefined) {
                phoneCode = Crypto.decryptAES(dataView.request.cookies.get("cancelInvestCode").get.value, "RCancelInvest")
            }
            if (projectId <= 0) {
                json = """{"code" : 1, "msg" : "没有发现可取消的项目"}"""
            }
            else if (!phoneCode.equals(code)) {
                json = """{"code" : 2, "msg" : "短信验证码错误"}"""
            }
            else {
                val isMain = Invest.investInfo(projectId, dataView.identity.id)
                if (0 < isMain) {
                    json = """{"code" : 3, "msg" : "领投人不能取消报名"}"""
                } else {
                    Invest.cancelInvest(projectId, dataView.identity.id)
//                    actor {
//                        val msg = "您报名的该项目已经取消。【睿就投】"
//                        LSHttpClient.LSsend(dataView.identity.tel,0,msg)
//                    }
                    json = """{"code" : 0, "msg" : "取消成功"}"""
                }
            }
            Ok(Json.stringify(Json.parse(json))).as("text/json")
    }

    //报名
    def investMain1 = WAction {
        implicit dataView =>
            var json = ""
            //post参数
            val map = dataView.request.body.asFormUrlEncoded
            val name = map.get.get("name").getOrElse(Seq(""))(0)
            val sex = map.get.get("sex").getOrElse(Seq(""))(0)
            val email = map.get.get("email").getOrElse(Seq(""))(0)
            val pcname = map.get.get("pcname").getOrElse(Seq(""))(0)
            val job = map.get.get("job").getOrElse(Seq(0))(0).toString.toLong
            val signCode = map.get.get("signCode").getOrElse(Seq(""))(0)
            val money = map.get.get("money").getOrElse(Seq(0))(0).toString.toLong
            val projectId = map.get.get("projectId").getOrElse(Seq(0))(0).toString.toLong
            val yearlysalary = map.get.get("yearlysalary").getOrElse(Seq(0))(0).toString.toDouble
            val stranger = map.get.get("stranger").getOrElse(Seq(""))(0)
            val worker = map.get.get("worker").getOrElse(Seq(""))(0)
            val pccapital =  map.get.get("pccapital").getOrElse(Seq(0))(0).toString.toDouble
            val pctel = map.get.get("pctel").getOrElse(Seq(""))(0)
            val educationid = map.get.get("educationid").getOrElse(Seq(0))(0).toString.toLong
            val itemfinancingsum = (map.get.get("itemfinancingsum").getOrElse(Seq(0))(0).toString.toDouble) / 10
            val itemName = map.get.get("projectName").getOrElse(Seq(""))(0).toString
            //获取项目起投金额
            val startMoney = ItemProject.startMoney(projectId)
            //短信验证码
            var phoneCode = ""
            if (dataView.request.cookies.get("investCode").isDefined) {
                phoneCode = Crypto.decryptAES(dataView.request.cookies.get("investCode").get.value, "Rinvest")
            }
            println("name:"+name)
            if (name.equals("")) {
                json = """{"code" : 1, "msg" : "请输入姓名"}"""
            } else if (sex.equals("")) {
                json = """{"code" : 2, "msg" : "请选择性别"}"""
            } else if (email.equals("")) {
                json = """{"code" : 3, "msg" : "请填写邮箱"}"""
            } else if (pcname.equals("")) {
                json = """{"code" : 4, "msg" : "请填写公司名称"}"""
            } else if (job == 0) {
                json = """{"code" : 5, "msg" : "请选择职位"}"""
            }
            else if (!signCode.equals(phoneCode)) {
                json = """{"code" : 6, "msg" : "短信验证码错误"}"""
            }
            else if (money <= 0) {
                json = """{"code" : 7, "msg" : "请输入整数报名金额"}"""
            } else if (projectId <= 0) {
                json = """{"code" : 8, "msg" : "没有发现可报名的项目"}"""
            } else if (itemfinancingsum > money) {
                json = """{"code" : 9, "msg" : "领投人投资金额必须大于计划融资的10%"}"""
            } else if (yearlysalary <= 0) {
                json = """{"code" : 10, "msg" : "请输入数字年薪金额"}"""
            } else if ("".equals(stranger)) {
                json = """{"code" : 11, "msg" : "请输入过往投资过的项目/资金"}"""
            } else if ("".equals(worker)) {
                json = """{"code" : 12, "msg" : "请输入工作经验"}"""
            } else if (pccapital<=0) {
              json = """{"code" : 17, "msg" : "公司资产必须大于0"}"""
            } else if (pctel.equals("")) {
              json = """{"code" : 18, "msg" : "请输入公司电话"}"""
            }  else {
                if (0 != dataView.identity.id) {
                    //判断是否报名
                    val isInvest = Invest.isInvest(projectId, dataView.identity.id)
                    //是否过期
                    val isOver = Invest.isOverTime(projectId)
                    if (isInvest == 0) {
                        val status = Invest.investMain(projectId, dataView.identity.id, name, sex, dataView.identity.tel, email, pcname, job, money, "1")
                        if (status != -1) {
                            val mainList = MainInfo.getMainInfo(dataView.identity.id)
                            if (mainList==None) {
                                MainInfo.saveMainInfo(dataView.identity.id, yearlysalary, stranger, worker,pccapital,pctel,educationid)
                            } else {
                                MainInfo.editMain(dataView.identity.id, yearlysalary, stranger, worker,pccapital,pctel,educationid)
                            }
                            actor {
                                val msg = s"""您已成功报名$itemName，认购金额为${money}万元，敬请留意项目相关信息。【睿就投】"""
                                LSHttpClient.LSsend(dataView.identity.tel,0,msg)
                            }
                            json = """{"code" : 0, "msg" : "报名成功"}"""
                        } else {
                            json = """{"code" : 13, "msg" : "报名失败"}"""
                        }
                    } else if (isOver == 0) {
                        json = """{"code" : 14, "msg" : "报名时间已结束"}"""
                    } else {
                        json = """{"code" : 15, "msg" : "您已经参加过报名,不可重复报名"}"""
                    }
                } else {
                    json = """{"code" : 16, "msg" : "请登录后在报名"}"""
                }
            }
            Ok(Json.stringify(Json.parse(json))).as("text/json")
    }
}
