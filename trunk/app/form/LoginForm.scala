package form

import play.api.data._
import play.api.data.Forms._

//注册
case class RegForm(account:String,tel:String,regPassword:String,sortCode:String,regCode:String)
object RegForm {
    val regForm = Form(
        mapping(
            "account" -> nonEmptyText(minLength = 2,maxLength = 18),
            "tel" -> nonEmptyText(minLength = 11,maxLength = 15),
            "regPassword" -> nonEmptyText(minLength = 6,maxLength = 16),
            "sortCode" -> nonEmptyText(minLength =4,maxLength = 4),
            "regCode" -> nonEmptyText(minLength = 4,maxLength = 4)
        )(RegForm.apply)(RegForm.unapply)
    )
}

//登录
case class LoginForm(accountname:String,loginPassword:String)
object LoginForm {
    val loginForm = Form(
        mapping(
            "accountname" -> nonEmptyText(minLength = 5,maxLength = 18),
            "loginPassword" -> nonEmptyText(minLength = 6,maxLength = 16)
        )(LoginForm.apply)(LoginForm.unapply)
    )
}

//忘记密码
case class ForgetForm(forgetPhone:String,newPassword:String,forgetPhoneCode:String,forgetCode:String)
object ForgetForm {
    val forgetForm = Form(
        mapping(
            "forgetPhone" -> nonEmptyText(minLength = 11,maxLength = 15),
            "newPassword" -> nonEmptyText(minLength = 6,maxLength = 16),
            "forgetPhoneCode" -> nonEmptyText(minLength = 4,maxLength = 4),
            "forgetCode" -> nonEmptyText(minLength = 4,maxLength = 4)
        )(ForgetForm.apply)(ForgetForm.unapply)
    )
}

//实名认证
case class UserAuthForm(name:String,card:String,front:String,back:String,hand:String,credit:String)
object UserAuthForm {
    val userAuthForm = Form(
        mapping(
            "name" -> nonEmptyText(minLength = 2,maxLength = 10),
            "card" -> nonEmptyText(minLength = 18,maxLength = 18),
            "front" -> nonEmptyText,
            "back" -> nonEmptyText,
            "hand" -> nonEmptyText,
            "credit" -> text
        )(UserAuthForm.apply)(UserAuthForm.unapply)
    )
}
