package controllers

import core.mvc.{Authorize, WAction}
import form.{CompanyForm, ProjectForm}
import model._
import org.joda.time.DateTime
import play.api.libs.json._
import play.api.mvc.Controller

/**
  * 方法描述:项目
  *
  * author 小刘
  * version v1.0
  * date 2015/12/1
  */
class ProjectController extends Controller {

    //项目列表
    def itemList = WAction{
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 2
        val list = ItemProject.itemList(1,0,3)
        var itemArray = List[CountDown]()
        for(i <- list.list){
            itemArray ::= CountDown(i.id,i.endtime.getOrElse(new DateTime()).toString("yyyy/MM/dd HH:mm:ss"))
        }
        Ok(views.html.project.itemListView(list,Json.toJson(itemArray)+""))
    }

    //更多项目
    def itemListV(pageNumber:Int) = WAction {
        implicit dataView =>
        val index = (pageNumber - 1) * 3
        val list = ItemProject.itemList(pageNumber,index,3)
        var itemArray = List[CountDown]()
        for(i <- list.list){
            itemArray ::= CountDown(i.id,i.endtime.getOrElse(new DateTime()).toString("yyyy/MM/dd HH:mm:ss"))
        }
        Ok(views.html.project.itemListChild(list,Json.toJson(itemArray)+"")).as("text/html")
    }

    //初始化项目
    def publish = Authorize {
        implicit dataView =>
        //类型
        val typeList = TypeClass.typeList("HYLY")
        //关键词
        val keyList = Json.toJson(Key.keyList)
        Ok(views.html.project.publishView(typeList,ProjectForm.projectForm,keyList))
    }

    //发布项目
    def submitPublish = Authorize {
        implicit dataView =>
            //类型
            val typeList = TypeClass.typeList("HYLY")
            //关键词
            val keyList = Json.toJson(Key.keyList)
            ProjectForm.projectForm.bindFromRequest.fold(
                error => {
                    BadRequest(views.html.project.publishView(typeList,error,keyList))
                },
                data => {
                    val id = ProItem.saveProItem(data,dataView.identity.id)
                    if(id > 0){
                        //发布成功
                        Redirect("/companyViews/"+id)
                    }else{
                        //发布失败
                        Ok(views.html.project.publishView(typeList,ProjectForm.projectForm,keyList))
                    }
                }
            )
    }

    //项目信息
    def projectInfo(id:Long) = Authorize {
        implicit dataView =>
        val typeList = TypeClass.typeList("HYLY")
        //获取项目信息
        val pro = ProItem.projectInfo(id)
        //关键词
        val keyList = Json.toJson(Key.keyList)
        if(pro.get.status.toInt == 1 || pro.get.status.toInt == 3){
            UV.addUV("4",dataView.request.remoteAddress,id)
            val form = ProjectForm.projectForm.bind(Json.toJson(pro.get))
            Ok(views.html.project.projectView(typeList,form,keyList))
        }else{
            Redirect("/")
        }
    }

    //跳转到投资页面
    //项目详情
    def isMainInfo(id: Long) = WAction {
        implicit dataView =>
        //项目信息
        val item = ItemProject.itemInfo(id)
        if (id == 0 || item.get.status.toInt < 4) {
            Redirect("/")
        } else {
            //投资列表
            val list = Invest.investList(id)
            //是否投资/投资金额
            var isInvest = 0
            var investMoney = 0.0
            //该项目是否存在领头人
            var isMain = 0

            //个人资料
            val itemMember = ItemMember.sigunMember(dataView.identity.id)
            val itemMain = MainInfo.getMain(dataView.identity.id)
            if (dataView.isAuth) {
                isInvest = Invest.isInvest(id, dataView.identity.id)
                investMoney = Invest.investMoney(id, dataView.identity.id)
                isMain = Invest.isMain(item.get.id)
            }
            //路演信息
            val demo = Demo.itemDemo(id)
            //职位信息
            val jobList = TypeClass.typeList("GSZW")
            //职位信息
            val classList = TypeClass.typeList("XL")
            Ok(views.html.project.projectInvestor(itemMember,classList, itemMain, isMain, item, list, isInvest, demo, jobList, investMoney))
        }
    }

    //修改项目
    def updateProject = WAction {
        implicit dataView =>
        val typeList = TypeClass.typeList("HYLY")
        //关键词
        val keyList = Json.toJson(Key.keyList)
        ProjectForm.projectForm.bindFromRequest.fold(
            error => {
                BadRequest(views.html.project.projectView(typeList,error,keyList))
            },
            data => {
                val id = ProItem.updateProItem(data,dataView.identity.id)
                if(id > 0){
                    //发布成功
                    Redirect("/companyViews/"+id)
                }else{
                    //发布失败
                    Ok(views.html.project.projectView(typeList,ProjectForm.projectForm,keyList))
                }
            }
        )
    }

    //公司信息
    def companyViews(id:Long) = WAction{
        implicit dataView =>
        val typeList = TypeClass.typeList("GSZW")
        //获取项目信息
        val company = Company.companyInfo(id)
        val form = CompanyForm.companyForm.bind(Json.toJson(company.get))
        Ok(views.html.project.companyView(typeList,form,id))
    }

    //保存公司信息
    def saveCompany = WAction {
        implicit dataView =>
        val typeList = TypeClass.typeList("GSZW")
        CompanyForm.companyForm.bindFromRequest.fold(
            error =>{
                Ok(views.html.project.companyView(typeList,error,0))
            },
            data =>{
                ProItem.saveCompany(data)
                Redirect("/completePublish")
            }
        )
    }

    //完成提交信息
    def completePublish = WAction {
        implicit con=>
        Ok(views.html.project.complete())
    }

    //项目详情
    def item(id:Long) = WAction{
        implicit dataView =>
        //设置TOP下标
        dataView.topIdex = 2
        UV.addUV("4", dataView.request.remoteAddress, id)
        //项目信息
        val item = ItemProject.itemInfo(id)
        if(id == 0 || item.get.status.toInt < 4){
            Redirect("/")
        }else{
            //投资列表
            val list = Invest.investList(id)
            //是否投资/投资金额
            var isInvest = 0
            var investMoney = 0.0
            var ismain = 0
            //个人资料
            val itemMember = ItemMember.sigunMember(dataView.identity.id)
            if(dataView.isAuth){
                isInvest = Invest.isInvest(id,dataView.identity.id)
                investMoney = Invest.investMoney(id,dataView.identity.id)
                ismain = Invest.isUserMain(id,dataView.identity.id)
            }
            //路演信息
            val demo = Demo.itemDemo(id)
            //职位信息
            val jobList = TypeClass.typeList("GSZW")
            //领头人
            val leader = Invest.leader(id)
            Ok(views.html.project.itemView(itemMember,item,list,ismain,isInvest,demo,jobList,investMoney,leader))
        }
    }

}
