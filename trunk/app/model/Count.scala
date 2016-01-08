package model

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

/**
 * Created by 24950 on 2015/12/3.
 * HH
 */
case class Count (id:Long,types:String,relevanceid:Long,uv:Long,pv:Long)
object count {
  var count = {
    get[Long]("id") ~
      get[String]("type") ~
      get[Long]("relevanceid") ~
      get[Long]("uv") ~
      get[Long]("pv") map { case
      id ~ types ~ relevanceid ~ uv ~ pv =>
      Count(id, types, relevanceid, uv, pv)
    }
  }

  /**
   * 保存PV和UV信息
   * @param types
   * @param relevanceid
   * @return
   */
  def addCount(types:String,relevanceid:Long)= DB.withConnection{
    implicit con=>
      val list=SQL(
        """
          |SELECT * FROM R_COUNT WHERE type={types} AND relevanceid={relevanceid}
        """.stripMargin).on('types -> types,'relevanceid -> relevanceid).as(count.*)
      list
      if(list.isEmpty){
        val id:Option[Long]=SQL("""
            |insert into r_count(type,relevanceid,uv,pv) values({types},{relevanceid},0,0)
            |""".stripMargin
        ).on('types -> types,'relevanceid -> relevanceid).executeInsert()
          id.getOrElse(-1)
      }
  }

  /**
   * 更新pv
   * @param types
   * @param relevanceid
   * @return
   */
  def editCount(types:String,relevanceid:Long)= DB.withConnection{
    implicit con=>
      val id:Option[Long]=SQL("""
                                |update r_count set pv=pv+1 where type={types} and relevanceid={relevanceid}
                                |""".stripMargin
      ).on('types -> types,'relevanceid -> relevanceid).executeInsert()
      id.getOrElse(-1)
  }

  /**
   * 查询
   * @param types
   * @param relevanceid
   * @return
   */
  def updateCount(types:String,relevanceid:Long)=DB.withConnection{
    implicit  con =>
    val list=SQL(
      """
        |select id,type as types,relevanceid,uv,pv from r_count where type={type} and relevanceid={relevanceid}
      """.stripMargin).on('type->types,'relevanceid->relevanceid).as(count.*)
    list
  }
}
