package core.auth

import play.api.libs.json.Json

/**
  * 用户登录标示类
  */
case class Identity(id: Long = 0,accountname: String = "",tel:String = "")

object Identity {
  implicit val fmt = Json.format[Identity]
}
