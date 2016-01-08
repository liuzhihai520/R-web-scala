package model

import anorm.SqlParser._
import anorm.~
import org.joda.time.DateTime

/**
 * Created by 24950 on 2015/12/2.
 */
case class MemberInfo(var id:Long,var accountname:String,var name:String,var sex:String,var tel:String,var email:String,
                      var pcname:String,var job:Long,var status:String)

  object MemberInfo {
    val memberInfo = {
      get[Long]("id")~
      get[String]("accountname")~
      get[String]("name")~
      get[String]("sex")~
      get[String]("tel")~
      get[String]("email")~
      get[String]("pcname")~
      get[Long]("job")~
      get[String]("status") map {
      case id~accountname~name~sex~tel~email~pcname~job~status =>
        MemberInfo(id,accountname,name,sex,tel,email,pcname,job,status)
      }
    }
}
