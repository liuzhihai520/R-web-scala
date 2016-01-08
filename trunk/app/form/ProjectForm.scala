package form

import play.api.data.Form
import play.api.data.Forms._
import play.api.data.format.Formats._

/**
  * 方法描述:项目
  *
  * author 小刘
  * version v1.0
  * date 2015/11/29
  */
case class ProjectForm (id:Long,name:String,tag:String,typeid:Long,tradedescription:String = "",financingsum:Double,
use:String,equity:Double,imgurl:String,plan:String,synopsis:String,description:String,projectstage:String,financingterm:Int,startnum:Double,shares:Double,regcapital:Double)
object ProjectForm {
    val projectForm = Form[ProjectForm](
        mapping(
            "id" -> longNumber,
            "name" -> nonEmptyText(minLength = 3,maxLength = 50),
            "tag" -> nonEmptyText(minLength = 2,maxLength = 20),
            "typeid" -> longNumber,
            "tradedescription" -> text,
            "financingsum" -> of[Double],
            "use" -> nonEmptyText(minLength = 4,maxLength = 200),
            "equity" -> of[Double],
            "imgurl" -> nonEmptyText,
            "plan" -> nonEmptyText,
            "synopsis" -> nonEmptyText,
            "description" -> nonEmptyText,
            "projectstage" -> nonEmptyText,
            "financingterm" -> number(min = 30,max = 120),
            "startnum" -> of[Double],
            "shares" -> of[Double],
            "regcapital" -> of[Double]
        )(ProjectForm.apply)(ProjectForm.unapply)
    )
}

//公司信息
case class CompanyForm (id:Long,pcname:String,web:String,province:String,city:String,licensenum:String,islisting:String = "0",
                    listingcode:String,foundtime:String,agencynum:Int = 0,valueation:Double,referee:String,
                    refereetel:String,linkman:String,linkmantel:String,linkmanjob:Long
)
object CompanyForm {
    val companyForm = Form[CompanyForm](
        mapping(
            "id" -> longNumber,
            "pcname" -> nonEmptyText,
            "web" -> text,
            "province" -> nonEmptyText,
            "city" -> nonEmptyText,
            "licensenum" -> nonEmptyText,
            "islisting" -> text,
            "listingcode" -> text,
            "foundtime" -> text,
            "agencynum" -> number,
            "valueation" -> of[Double],
            "referee" -> text,
            "refereetel" -> text,
            "linkman" -> nonEmptyText,
            "linkmantel" -> nonEmptyText,
            "linkmanjob" -> longNumber
        )(CompanyForm.apply)(CompanyForm.unapply)
    )
}
