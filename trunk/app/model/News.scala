package model

import anorm._
import anorm.SqlParser._
import core.utils.Pages
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:咨询
  *
  * author 小刘
  * version v1.0
  * date 2015/11/27
  */
case class News (id:Long,title:String,typeid:Long,tag:String,imgurl:String,introduction:String,content:String,source:String,url:Option[String],
    qrcode:Option[String],delflag:String,status:String,iscream:String,istop:String,toptime:DateTime,publishtime:DateTime,addtime:DateTime,userid:Long
)

object News {
    val news = {
        get[Long]("id")~
        get[String]("title")~
        get[Long]("typeid")~
        get[String]("tag")~
        get[String]("imgurl")~
        get[String]("introduction")~
        get[String]("content")~
        get[String]("source")~
        get[Option[String]]("url")~
        get[Option[String]]("qrcode")~
        get[String]("delflag")~
        get[String]("status")~
        get[String]("iscream")~
        get[String]("istop")~
        get[DateTime]("toptime")~
        get[DateTime]("publishtime")~
        get[DateTime]("addtime")~
        get[Long]("userid") map{
            case id~title~typeid~tag~imgurl~introduction~content~source~url~qrcode~delflag~status~iscream~istop~toptime~publishtime~addtime~userid =>
                News(id,title,typeid,tag,imgurl,introduction,content,source,url,qrcode,delflag,status,iscream,istop,toptime,publishtime,addtime,userid)
        }
    }

    //资讯集合
    def newsList(iscream:Int,pageNumber:Int,index:Int,limit:Int) = DB.withConnection {
        implicit con =>
        var cream = ""
        if(iscream != 0){
            cream = " and iscream = '1'"
        }
        //总数
        val count = SQL(
            s"""
              |select count(0) as getCount from r_news where delflag='0' and status='1' $cream
            """.stripMargin
        ).on('iscream -> iscream).as(scalar[Int].single)
        //集合
        val list = SQL(
            s"""
              |select * from r_news where delflag='0' and status='1' $cream order by istop desc,toptime desc  limit {index},{limit}
            """.stripMargin
        ).on('index -> index,'limit -> limit).as(news.*)
        Pages[News](list,count,5,pageNumber,limit)
    }

  //获取资讯详情
  def newsInfo(newsId:Long) = DB.withConnection{
    implicit con =>
      val newsInfo=SQL(
        """
          |select * from r_news where id={id} and delflag='0' and status='1'
        """.stripMargin).on('id ->newsId).as(news.single)
      newsInfo
  }
}
