package model

import anorm._
import anorm.SqlParser._
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:路演
  *
  * author 小刘
  * version v1.0
  * date 2015/12/2
  */
case class Demo (id:Long,projectid:Long,activitytime:DateTime,content:String,address:String,addtime:DateTime)
object Demo {
    val demo = {
        get[Long]("id")~
        get[Long]("projectid")~
        get[DateTime]("activitytime")~
        get[String]("content")~
        get[String]("address")~
        get[DateTime]("addtime") map {
            case id~projectid~activitytime~content~address~addtime =>
            Demo(id,projectid,activitytime,content,address,addtime)
        }
    }

    //路演
    def itemDemo(id:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |select * from r_demo where projectid = {id} order by id desc limit 0,1
            """.stripMargin
        ).on('id -> id).as(demo.singleOpt)
    }

}
