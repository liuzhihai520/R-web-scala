package model

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import org.joda.time.DateTime
import play.api.Play.current

/**
 * Created by 24950 on 2015/12/4.
 * HH
 */
case class UV (id:Long,types:String,relevanceid:Long,userip:String,addtime:DateTime)
object UV{
  var uv={
      get[Long]("id")~
      get[String]("type")~
      get[Long]("relevanceid")~
      get[String]("userip")~
      get[DateTime]("addtime") map { case
      id ~ types ~ relevanceid ~ userip ~ addtime =>
        UV(id, types, relevanceid, userip, addtime)
    }
  }

  //增加信息的浏览量和阅读量
  def addUV(types:String,uip:String,relevanceid:Long)=DB.withConnection{
    implicit con =>
//      val id:Option[Long]=SQL("""
//                                |insert into r_uv(type,relevanceid,userip) values({type},{relevanceid},{userIp})
//                                |""".stripMargin
//      ).on('type -> types,'relevanceid -> relevanceid,'userIp->uip).executeInsert()
//      id.getOrElse(-1)

      val count=SQL(
        """
          |select count(*) as getCount from r_count where type={type} and relevanceid={relevanceid}
        """.stripMargin).on('type->types,'relevanceid->relevanceid).as(scalar[Int].single)
      count
      if(0<count){
        val id2:Option[Long]=SQL("""
                                  |update r_count set pv=pv+1 where type={type} and relevanceid={relevanceid}
                                  |""".stripMargin
        ).on('type -> types,'relevanceid -> relevanceid).executeInsert()
        id2.getOrElse(-1)
        var stime=(new DateTime()).toString("YYYY-MM-dd")+" 00:00:00"
        var etime=(new DateTime()).toString("YYYY-MM-dd")+" 23:59:59"
        val count2=SQL(
          """
            |select count(*) as getCount from r_uv where userip={userip} and  type={type} and relevanceid={relevanceid} and (addtime>{stime} and addtime<{etime})
          """.stripMargin).on('userip->uip,'type -> types,'relevanceid -> relevanceid,'stime->stime,'etime->etime).as(scalar[Int].single)
        count2
        print("count2:"+count2)
        System.out.print("count2:"+count2)
        //此访客今天没有访问过则增加UV信息并UV+1；
        if (0==count2) {
          // UV+1
          val id6:Option[Long]=SQL("""
                                     |update r_count set uv=uv+1 where type={type} and relevanceid={relevanceid}
                                     |""".stripMargin
          ).on('type -> types,'relevanceid -> relevanceid).executeInsert()
          id6.getOrElse(-1)

          val id5:Option[Long]=SQL("""
                                     |insert into r_uv(type,relevanceid,userip) values({type},{relevanceid},{userIp})
                                     |""".stripMargin
          ).on('type -> types,'relevanceid -> relevanceid,'userIp->uip).executeInsert()
          id5.getOrElse(-1)
        }
      }else{
        // 为空则增加统计数据和用户UV数据
        val id3:Option[Long]=SQL("""
                                   |insert into r_count(type,relevanceid,uv,pv) values({type},{relevanceid},1,1)
                                   |""".stripMargin
        ).on('type -> types,'relevanceid -> relevanceid).executeInsert()
        id3.getOrElse(-1)

        val id4:Option[Long]=SQL("""
                                  |insert into r_uv(type,relevanceid,userip) values({type},{relevanceid},{userIp})
                                  |""".stripMargin
        ).on('type -> types,'relevanceid -> relevanceid,'userIp->uip).executeInsert()
        id4.getOrElse(-1)
      }
  }
}
