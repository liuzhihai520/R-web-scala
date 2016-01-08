package model

import anorm._
import anorm.SqlParser._
import core.utils.Pages
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:活动
  *
  * author 小刘
  * version v1.0
  * date 2015/11/27
  */
case class Activity (id:Long,title:String,typeid:Long,tag:String,imgurl:String,content:String,url:String,signup:Int,address:String,qrcode:Option[String],delflag:Int,status:Int,
iscream:Int,istop:Int,isauth:Int,toptime:DateTime,publishtime:DateTime,endtime:DateTime,activitytime:DateTime,addtime:DateTime,userid:Long,lat:String,lng:String,var types:Int,review:Option[String])

object Activity {
    val activity = {
        get[Long]("id")~
        get[String]("title")~
        get[Long]("typeid")~
        get[String]("tag")~
        get[String]("imgurl")~
        get[String]("content")~
        get[String]("url")~
        get[Int]("signup")~
        get[String]("address")~
        get[Option[String]]("qrcode")~
        get[Int]("delflag")~
        get[Int]("status")~
        get[Int]("iscream")~
        get[Int]("istop")~
        get[Int]("isauth")~
        get[DateTime]("toptime")~
        get[DateTime]("publishtime")~
        get[DateTime]("endtime")~
        get[DateTime]("activitytime")~
        get[DateTime]("addtime")~
        get[Long]("userid")~
        get[String]("lat")~
        get[String]("lng")~
        get[Int]("types")~
        get[Option[String]]("review") map {
            case id~title~typeid~tag~imgurl~content~url~signup~address~qrcode~delflag~status~iscream~istop~isauth~toptime~publishtime~endtime~activitytime~addtime~userid~lat~lng~types~review =>
            Activity(id,title,typeid,tag,imgurl,content,url,signup,address,qrcode,delflag,status,iscream,istop,isauth,toptime,publishtime,endtime,activitytime,addtime,userid,lat,lng,types,review)
        }
    }

    //活动列表
    def activityList(pageNumber:Int,index:Int,limit:Int) = DB.withConnection{
        implicit con =>
        //总数
        val count = SQL(
            """
              |select count(0) as getCount from r_activity where delflag='0' and status='1'
            """.stripMargin
        ).as(scalar[Int].single)
        //集合
        val list = SQL(
            """
              |select *,0 as types from r_activity where delflag='0' and status='1' ORDER BY istop desc,toptime desc,id DESC  limit {index},{limit}
            """.stripMargin
        ).on('index -> index,'limit -> limit).as(activity.*)
        for(i <- list){
            i
            if(i.endtime.getMillis > new DateTime().getMillis){
                i.types = 1
            }else{
                i.types = 2
            }
        }
        Pages[Activity](list,count,5,pageNumber,limit)
    }

    //我的活动
    def myActivity(id:Long):List[Activity] = DB.withConnection {
        implicit con =>
        val list = SQL(
            """
              |SELECT b.*, 0 as types FROM r_activityuser a JOIN r_activity b ON a.activityid=b.id where a.memberid = {memberid} order by a.id desc
            """.stripMargin
        ).on('memberid -> id).as(activity.*)
        for(i <- list){
            val now = new DateTime().getMillis
            val endtime = i.endtime.getMillis
            val activitytime = i.activitytime.getMillis
            if(now >= activitytime && now < endtime){
                //进行中
                i.types = 1
            }else if(now > endtime){
                //已结束
                i.types = 2
            }else {
                //未开始
                i.types = 3
            }
        }
        list
    }

  //获取活动详情
  def activityInfo(id:Long):Activity=DB.withConnection{
      implicit con =>
      val activityInfo = SQL(
        """
          |SELECT *,0 as types FROM r_activity where id={id} and delflag='0' and status='1'
        """.stripMargin
      ).on('id -> id).as(activity.single)
        activityInfo
  }

}
