package model

import org.joda.time.DateTime
import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:短信发送
  *
  * author 小刘
  * version v1.0
  * date 2015/12/3
  */
case class SmsCode (id:Long,tel:Option[String],types:String,code:String,sendcodetime:DateTime)
object SmsCode {
    val smsCode = {
        get[Long]("id")~
        get[Option[String]]("tel")~
        get[String]("types")~
        get[String]("code")~
        get[DateTime]("sendcodetime") map{
            case id~tel~types~code~sendcodetime =>
            SmsCode(id,tel,types,code,sendcodetime)
        }
    }

    //是否频繁请求短信接口
    def isBusy(tel:String) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |select id,tel,type as types,code,sendcodetime from r_smscode where tel = {tel} order by id desc limit 0,1
            """.stripMargin
        ).on('tel -> tel).as(smsCode.singleOpt)
    }

    //短信发送日志
    def isLog(tel:String,code:String,codeTpe:Int) = DB.withConnection {
        implicit con =>
        SQL(
            """
              |insert into r_smscode (tel,type,code) values ({tel},{type},{code})
            """.stripMargin
        ).on('tel -> tel,'type -> codeTpe,'code -> code).executeInsert()
    }
}
