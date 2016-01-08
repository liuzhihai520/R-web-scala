package model

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Json

/**
  * 方法描述:TODO
  *
  * author 小刘
  * version v1.0
  * date 2015/12/14
  */
case class Key(id:Long,txt:String)
object Key{
    implicit val fmt = Json.format[Key]
    val key = {
        get[Long]("id")~
        get[String]("txt") map{
            case id~txt =>
            Key(id,txt)
        }
    }

    //关键词过滤
    def keyList = DB.withConnection {
        implicit con=>
        SQL(
            """
              |select * from r_key limit 0,1
            """.stripMargin
        ).as(key.single)
    }

}
