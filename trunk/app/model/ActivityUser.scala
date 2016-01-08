package model

import anorm._
import anorm.SqlParser._
import core.utils.Pages
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current
/**
 * Created by 24950 on 2015/11/30.
 */
case class ActivityUser(id:Long,activityid:Long,memberid:Long,name:String,tel:String,email:String,pcname:String,job:Long,delflag:String,status:String,addtime:DateTime)

object ActivityUser {
  val activityUser = {
    get[Long]("id")~
    get[Long]("activityid")~
    get[Long]("memberid")~
    get[String]("name")~
    get[String]("tel")~
    get[String]("email")~
    get[String]("pcname")~
    get[Long]("job")~
    get[String]("delflag")~
    get[String]("status")~
    get[DateTime]("addtime") map{
      case id~activityid~memberid~name~tel~email~pcname~job~delflag~status~addtime=>
        ActivityUser(id,activityid,memberid,name,tel,email,pcname,job,delflag,status,addtime)
    }
  }

  /**
   * 根据活动id查询报名人员分页数据
   * @param id
   * @param limit
   * @return
   */
  def getActivityUser(id:Long,pageNumber:Int,index:Int,limit:Int) = DB.withConnection{
    implicit con =>
      //总数
      val count = SQL(
        """
          |select count(0) as getCount from r_activityuser where activityid={activityid} and delflag='0' and status='0'
        """.stripMargin
      ).on('activityid->id).as(scalar[Int].single)
      val list=SQL(
        """
          |select * from r_activityuser where activityid={activityid} and delflag='0' and status='0' order by addtime desc limit {index},{limit}
        """.stripMargin).on('activityid->id,'index -> index,'limit -> limit).as(activityUser.*)
      Pages[ActivityUser](list,count,5,pageNumber,limit)
  }

  def isRegistration(memberid:Long,activityid:Long) = DB.withConnection{
    implicit  con =>
      val list=SQL(
        """
          |select * from r_activityuser where activityid={activityid} and memberid={memberid}
        """.stripMargin).on('activityid->activityid,'memberid->memberid).as(activityUser.*)
      list
  }

  //保存编辑会员信息
  def saveActivityUser(user:ActivityUser) = DB.withConnection {
    implicit con =>
      val i = SQL(
        """
          |insert into r_activityuser(activityid,memberid,name,tel,email,pcname,job) values({activityid},{memberid},{name},{tel},{email},{pcname},{job})
        """.stripMargin
      ).on('activityid -> user.activityid,'memberid -> user.memberid,'name -> user.name,'tel -> user.tel,'email -> user.email,'pcname -> user.pcname, 'job -> user.job).executeUpdate()
      i
      val saveUser=SQL(
        """
          |UPDATE r_activity set signup=signup+1 where id={id}
        """.stripMargin).on('id->user.activityid).executeUpdate();
  }

  //取消报名
  def outActivityUser(userId:Long,ActivityId:Long) = DB.withConnection {
    implicit con =>
      val i = SQL(
        """
          |update r_activityuser set status='1' where activityid={activityid} and memberid={memberid}
        """.stripMargin
      ).on('activityid -> ActivityId,'memberid ->userId).executeUpdate()
      i
      val outUser=SQL(
        """
          |UPDATE r_activity set signup=signup-1 where id={id}
        """.stripMargin).on('id->ActivityId).executeUpdate();
  }

  /**
   * 判断用户是否报名
   * @param userId
   * @param activityId
   */
  def queryUserForActivity(userId:Long,activityId:Long) = DB.withConnection {
    implicit con =>
      val list= SQL(
        """
          |select * from r_activityuser where activityid={activityid} and memberid={memberid} and status='0'
        """.stripMargin).on('activityid -> activityId,'memberid -> userId).as(activityUser.*)
      list
  }

  //活动名称和地点
  def titleAdd(id:Long) = DB.withConnection {
      implicit con=>
      SQL(
          """
            |SELECT title,address FROM r_activity WHERE id = {id}
          """.stripMargin
      ).on('id -> id).as(
        (get[String]("title") ~ get[String]("address") map {
          case title ~ address => (title, address)
        }).singleOpt).getOrElse(("", "")
      )
  }
}
