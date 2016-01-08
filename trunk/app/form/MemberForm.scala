package form

import play.api.data.Form
import play.api.data.Forms._

/**
  * 方法描述:TODO
  *
  * author 小刘
  * version v1.0
  * date 2015/11/30
  */
case class MemberForm (accountname:String,name:String,head:String,sex:String,birthday:String,tel:String,email:String,pcname:String,job:Long,investmentideas:String,description:String,province:String,city:String)
object MemberForm {
    val memberForm = Form(
        mapping(
            "accountname" -> nonEmptyText(minLength = 2,maxLength = 18),
            "name" -> nonEmptyText,
            "head" -> nonEmptyText,
            "sex" -> nonEmptyText,
            "birthday" -> nonEmptyText,
            "tel" -> nonEmptyText,
            "email" -> nonEmptyText,
            "pcname" -> nonEmptyText,
            "job" -> longNumber,
            "investmentideas" -> text,
            "description" -> text,
            "province" -> nonEmptyText,
            "city" -> nonEmptyText
        )(MemberForm.apply)(MemberForm.unapply)
    )
}
