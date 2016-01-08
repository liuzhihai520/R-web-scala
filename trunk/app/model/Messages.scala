package model

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:描述
  *
  * author 小刘
  * version v1.0
  * date 2015/12/26
  */
case class Messages(id:Long,name:Option[String],tel:Option[String],email:Option[String],context:Option[String],addtime:Option[DateTime],delflag:Option[String],
status:Option[String],backcontext:Option[String],backtime:Option[DateTime],userId:Option[Long],ip:Option[String])

object Messages {
    val messages = {
        get[Long]("id")~
        get[Option[String]]("name")~
        get[Option[String]]("tel")~
        get[Option[String]]("email")~
        get[Option[String]]("context")~
        get[Option[DateTime]]("addtime")~
        get[Option[String]]("delflag")~
        get[Option[String]]("status")~
        get[Option[String]]("backcontext")~
        get[Option[DateTime]]("backtime")~
        get[Option[Long]]("userId")~
        get[Option[String]]("ip") map {
            case id~name~tel~email~context~addtime~delflag~status~backcontext~backtime~userId~ip =>
            Messages(id,name,tel,email,context,addtime,delflag,status,backcontext,backtime,userId,ip)
        }
    }

    //上次留言时间
    def preMsg(ip:String) = DB.withConnection {
        implicit con =>
        SQL(
            """
              |select * from r_message where ip = {ip} order by id desc limit 0,1
            """.stripMargin
        ).on('ip -> ip).as(messages.singleOpt)
    }

    //留言
    def saveMsg(name:String,tel:String,email:String,context:String,ip:String,userId:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |insert into r_message(name,tel,email,context,userId,ip) values ({name},{tel},{email},{context},{userId},{ip})
            """.stripMargin
        ).on('name -> name,'tel -> tel,'email -> email,'context -> context,'userId -> userId,'ip -> ip).executeInsert()
    }
}
