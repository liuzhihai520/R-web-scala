package model

import anorm._
import anorm.SqlParser._
import core.utils.Pages
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
 * Created by 24950 on 2015/12/10.
 */
case class MainInfo (id:Long,memberid:Long,yearlysalary:Double,stranger:String,worker:String,pccapital:Double,pctel:String,educationid:Long)
object MainInfo{
  var mainifo={
    get[Long]("id")~
    get[Long]("memberid")~
    get[Double]("yearlysalary")~
    get[String]("stranger")~
    get[String]("worker")~
    get[Double]("pccapital")~
    get[String]("pctel")~
    get[Long]("educationid") map{
      case id~memberid~yearlysalary~stranger~worker~pccapital~pctel~educationid =>
        MainInfo(id,memberid,yearlysalary,stranger,worker,pccapital,pctel,educationid)
    }
  }

  /**
   * 保存
   * @return
   */
  def saveMainInfo(memberid:Long,yearlysalary:Double,stranger:String,worker:String,pccapital:Double,pctel:String,educationid:Long) = DB.withConnection {
    implicit con =>
      val i= SQL(
        """
          |INSERT INTO r_maininfo(memberid,yearlysalary,stranger,worker,pccapital,pctel,educationid) VALUES({memberid},{yearlysalary},{stranger},{worker},{pccapital},{pctel},{educationid})
        """.stripMargin).on('memberid -> memberid,'yearlysalary -> (yearlysalary*10000),'stranger -> stranger,'worker -> worker,'pccapital->(pccapital*10000),'pctel -> pctel,'educationid->educationid).executeUpdate()
      i
  }

  /**
   * 根据用户id 查询领投信息
   * @param memberid
   * @return
   */
  def getMain(memberid:Long) = DB.withConnection(){
    implicit  con =>
      SQL(
        """
          |SELECT *,yearlysalary/10000 as yearlysalary,pccapital/10000 as pccapital FROM r_maininfo where memberid={memberid}
        """.stripMargin
      ).on('memberid -> memberid).as(mainifo.singleOpt).getOrElse(MainInfo(0, 0, 0.0, "", "",0.0,"",0))

  }

  /**
   * 根据用户id 查询领头信息
   * @param memberid
   * @return
   */
  def getMainInfo(memberid:Long)= DB.withConnection(){
    implicit  con =>
      val mainInfo=SQL(
        """
          |SELECT *,yearlysalary/10000 as yearlysalary,pccapital/10000 as pccapital FROM r_maininfo where memberid={memberid}
        """.stripMargin).on('memberid -> memberid).as(mainifo.singleOpt)
      mainInfo
  }

  /**
   * 更新领投信息
   * @return
   */
  def editMain(memberid:Long,yearlysalary:Double,stranger:String,worker:String,pccapital:Double,pctel:String,educationid:Long)=DB.withConnection{
    implicit  con =>
    println(pccapital+"!"+pctel+"!"+educationid+"!"+memberid)
      val i=SQL(
        """
          |UPDATE r_maininfo SET yearlysalary={yearlysalary},stranger={stranger},worker={worker},pccapital={pccapital},pctel={pctel},educationid={educationid}
          |WHERE memberid={id}
        """.stripMargin).on('yearlysalary -> (yearlysalary*10000),'stranger ->stranger,'worker -> worker,'pccapital->(pccapital*10000),'pctel->pctel,'educationid->educationid,'id ->memberid).executeUpdate()
      i
  }
}
