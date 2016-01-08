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
  * date 2015/12/4
  */
case class Link(id:Long,name:String,typeid:Long,logo:Option[String],url:String,orderlist:Int)
object Link {
    val link = {
        get[Long]("id")~
        get[String]("name")~
        get[Long]("typeid")~
        get[Option[String]]("logo")~
        get[String]("url")~
        get[Int]("orderlist") map{
            case id~name~typeid~logo~url~orderlist =>
            Link(id,name,typeid,logo,url,orderlist)
        }
    }

    //合作伙伴
    def linkList(code:String) = DB.withConnection {
        implicit con =>
        SQL(
            """
              |SELECT b.id,b.name,b.typeid,b.logo,b.url,b.orderlist
              |FROM r_type a LEFT JOIN r_partners b ON a.id = b.typeid WHERE a.typecode = {typecode} AND b.delflag = '0' AND b.status = '1'
            """.stripMargin
        ).on('typecode -> code).as(link.*)
    }
}
