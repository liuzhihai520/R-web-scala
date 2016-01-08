package model

import java.sql.Timestamp

import anorm._
import anorm.SqlParser._
import core.utils.Pages
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:描述
  *
  * author 小刘
  * version v1.0
  * date 2015/11/27
  */
case class Banner(id:Long, title:String, imgurl1:String, imgurl2:String, types:String, content:String, url:String, delflag:String, status:String, addtime:DateTime, orderlist:Int, clicknum:Int, userid:Long)

object Banner {
    val banner = {
        get[Long]("id")~
        get[String]("title")~
        get[String]("imgurl1")~
        get[String]("imgurl2")~
        get[String]("types")~
        get[String]("content")~
        get[String]("url")~
        get[String]("delflag")~
        get[String]("status")~
        get[DateTime]("addtime")~
        get[Int]("orderlist")~
        get[Int]("clicknum")~
        get[Long]("userid") map {
            case id~title~imgurl1~imgurl2~types~content~url~delflag~status~addtime~orderlist~clicknum~userid =>
            Banner(id,title,imgurl1,imgurl2,types,content,url,delflag,status,addtime,orderlist,clicknum,userid)
        }
    }

    def bannerList(pageNumber:Int,index:Int,limit:Int) = DB.withConnection{
        implicit con =>
        //总数
        val count = SQL(
            """
              |select count(0) as getCount from r_banner where status = 1
            """.stripMargin
        ).as(scalar[Int].single)
        //集合
        val list = SQL(
            """
              |select id,title,imgurl1,imgurl2,type as types,content,url,delflag,status,addtime,orderlist,clicknum,userid
              |from r_banner where status = 1  ORDER BY id DESC  limit {index},{limit}
            """.stripMargin
        ).on('index -> index,'limit -> limit).as(banner.*)
        Pages[Banner](list,count,5,pageNumber,limit)
    }

    def banner(id:Long):Banner=DB.withConnection{
      implicit con=>
        val bInfo=SQL(
          """
            |SELECT id,title,imgurl1,imgurl2,type as types,content,url,delflag,status,addtime,orderlist,clicknum,userid,content FROM R_BANNER WHERE ID={id} AND TYPE='5'
          """.stripMargin).on('id ->id).as(banner.single)
        bInfo
    }
}
