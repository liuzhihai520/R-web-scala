package model

import java.util.Date

import anorm._
import anorm.SqlParser._
import core.utils.DateTimeUtils
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:投资
  *
  * author 小刘
  * version v1.0
  * date 2015/11/29
  */

//投资
case class Invest (id:Long,projectid:Long,memberid:Long,investmentsum:Double,name:Option[String],sex:Option[String],
                   tel:Option[String],email:Option[String],pcname:Option[String],job:Option[Long],ismain:Option[String],
                   main:Option[String],delflag:Option[String],status:Option[String],paycode:Option[String],addtime:Option[DateTime]
)
object Invest {
    val invest = {
        get[Long]("id")~
        get[Long]("projectid")~
        get[Long]("memberid")~
        get[Double]("investmentsum")~
        get[Option[String]]("name")~
        get[Option[String]]("sex")~
        get[Option[String]]("tel")~
        get[Option[String]]("email")~
        get[Option[String]]("pcname")~
        get[Option[Long]]("job")~
        get[Option[String]]("ismain")~
        get[Option[String]]("main")~
        get[Option[String]]("delflag")~
        get[Option[String]]("status")~
        get[Option[String]]("paycode")~
        get[Option[DateTime]]("addtime") map{
            case id~projectid~memberid~investmentsum~name~sex~tel~email~pcname~job~ismain~main~delflag~status~paycode~addtime =>
            Invest(id,projectid,memberid,investmentsum,name,sex,tel,email,pcname,job,ismain,main,delflag,status,paycode,addtime)
        }
    }

    //投资列表
    def investList(id:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |select id,projectid,memberid,investmentsum/10000 as investmentsum,name,sex,tel,email,pcname,job,ismain,main,delflag,status,paycode,addtime,'' as jobName
              |from r_investmentuser where projectid = {id} and delflag='0'
            """.stripMargin
        ).on('id -> id).as(invest.*)
    }

  //投资信息
  def investInfo(id:Long,memberid:Long) = DB.withConnection {
    implicit con=>
      SQL(
        """
          |select  count(0) as getCount
          |from r_investmentuser where projectid = {id} and memberid={memberid} and delflag = '0' and main='1'
        """.stripMargin
      ).on('id -> id,'memberid->memberid).as(scalar[Int].single)
  }

    //是否报名
    def isInvest(id:Long,userId:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |select count(0) as getCount from r_investmentuser where delflag = '0' and projectid = {id} and memberid = {userId}
            """.stripMargin
        ).on('id -> id,'userId -> userId).as(scalar[Int].single)
    }

  //是否报名
  def isUserMain(id:Long,userId:Long) = DB.withConnection {
    implicit con=>
      SQL(
        """
          |select count(0) as getCount from r_investmentuser where delflag = '0' and projectid = {id} and memberid = {userId} and main='1'
        """.stripMargin
      ).on('id -> id,'userId -> userId).as(scalar[Int].single)
  }

    //是否超过报名期限
    def isOverTime(id:Long) = DB.withConnection {
        implicit con =>
        SQL(
            """
              |select count(0) as getCount from r_project where endtime > {now}
            """.stripMargin
        ).on('now -> new DateTime().toDate).as(scalar[Int].single)
    }

    /**
     * 是否存在领头人
     * @param projectId
     * @return
     */
    def isMain(projectId:Long) = DB.withConnection {
      implicit con=>
        SQL(
          """
            |SELECT COUNT(*) as getCount FROM r_investmentuser where projectid={projectid} and main='1'
          """.stripMargin).on('projectid -> projectId).as(scalar[Int].single)
    }

    //报名金额
    def investMoney(id:Long,userId:Long) = DB.withConnection {
        implicit con=>
        val money = SQL(
            """
              |select investmentsum/10000 as investmentsum  from r_investmentuser where projectid = {id} and memberid = {userId} order by id desc limit 0,1
            """.stripMargin
        ).on('id -> id,'userId -> userId).as(scalar[Double].singleOpt)
        money.getOrElse("0").toString.toDouble
    }

    //领头人
    def leader(id:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |SELECT id,projectid,memberid,investmentsum/10000 as investmentsum,name,sex,tel,email,pcname,job,ismain,main,delflag,status,paycode,addtime
              |FROM r_investmentuser where projectid = {projectid} and main = 1
            """.stripMargin
        ).on('projectid -> id).as(invest.singleOpt)
    }

    //报名
    def invest(projectid:Long,memberid:Long,name:String,sex:String,tel:String,email:String,pcname:String,job:Long,money:Double) = DB.withConnection {
        implicit con=>
        con.setAutoCommit(false)
        //写入投资表
        val id:Option[Long] = SQL(
            """
              |insert into r_investmentuser(projectid,memberid,investmentsum,name,sex,tel,email,pcname,job)
              |values({projectid},{memberid},{investmentsum},{name},{sex},{tel},{email},{pcname},{job})
            """.stripMargin
        ).on('projectid -> projectid,'memberid -> memberid,'investmentsum -> (money*10000),'name -> name,'sex -> sex,'tel->tel,'email -> email,'pcname->pcname,'job -> job ).executeInsert()
        //更改总金额
        SQL(
            """
              |update r_project set financingamount = financingamount + {getMoney},sign = sign+1 where id = {id}
            """.stripMargin
        ).on('getMoney -> (money*10000),'id -> projectid).executeUpdate()
        con.commit()
        id.getOrElse(-1)
    }

    //取消报名
    def cancelInvest(projectId:Long,userId:Long) = DB.withConnection {
        implicit con=>
        con.setAutoCommit(false)
        val money = SQL(
            """
              |select investmentsum from r_investmentuser where projectid = {itemId} and memberid = {userId} order by id desc limit 0,1
            """.stripMargin
        ).on('itemId -> projectId,'userId -> userId).as(scalar[Double].singleOpt).getOrElse(0.0)
        //取消报名
        SQL(
            """
              |update r_investmentuser set delflag = 1 where projectId = {id} and memberid = {memberid}
            """.stripMargin
        ).on('id -> projectId,'memberid -> userId).executeUpdate()
        //报名金额
        SQL(
            """
              |update r_project set financingamount = financingamount - {money},sign = sign-1  where id = {id}
            """.stripMargin
        ).on('money -> money,'id -> projectId).executeUpdate()
        con.commit()
    }

  //报名
  def investMain(projectid:Long,memberid:Long,name:String,sex:String,tel:String,email:String,pcname:String,job:Long,money:Double,isMain:String) = DB.withConnection {
    implicit con=>
      con.setAutoCommit(false)
      //写入投资表
      val id:Option[Long] = SQL(
        """
          |insert into r_investmentuser(projectid,memberid,investmentsum,name,sex,tel,email,pcname,job,ismain)
          |values({projectid},{memberid},{investmentsum},{name},{sex},{tel},{email},{pcname},{job},{ismain})
        """.stripMargin
      ).on('projectid -> projectid,'memberid -> memberid,'investmentsum -> (money*10000),'name -> name,'sex -> sex,'tel->tel,'email -> email,'pcname->pcname,'job -> job,'ismain-> isMain).executeInsert()
      //更改总金额
      SQL(
        """
          |update r_project set financingamount = financingamount + {getMoney},sign = sign+1 where id = {id}
        """.stripMargin
      ).on('getMoney -> (money*10000),'id -> projectid).executeUpdate()
      con.commit()
      id.getOrElse(-1)
  }
}
//我的投资
case class MyInvest(projectid:Long,investmentsum:Double,name:Option[String],status:Option[String],synopsis:Option[String],financingsum:Double,
financingamount:Double,sign:Int,tag:String,location:Option[String],imgurl:Option[String],endtime:Option[DateTime],var day:Long)
object MyInvest {
    val myInvest = {
        get[Long]("projectid")~
        get[Double]("investmentsum")~
        get[Option[String]]("name")~
        get[Option[String]]("status")~
        get[Option[String]]("synopsis")~
        get[Double]("financingsum")~
        get[Double]("financingamount")~
        get[Int]("sign")~
        get[String]("tag")~
        get[Option[String]]("location")~
        get[Option[String]]("imgurl")~
        get[Option[DateTime]]("endtime")~
        get[Int]("types") map {
            case projectid~investmentsum~name~status~synopsis~financingsum~financingamount~sign~tag~location~img~endtime~types =>
            MyInvest(projectid,investmentsum,name,status,synopsis,financingsum,financingamount,sign,tag,location,img,endtime,types)
        }
    }

    //我的投资
    def myInvest(id:Long):List[MyInvest] = DB.withConnection {
        implicit con =>
        val list = SQL(
            """
              |SELECT a.projectid,a.investmentsum,b.name,b.status,b.synopsis,b.financingsum/10000 as financingsum,b.financingamount/10000 as financingamount,
              |b.sign,b.tag,b.location,b.imgurl,b.endtime,0 as types
              |FROM r_investmentuser a LEFT JOIN r_project b ON a.projectid=b.id
              |where b.delflag = '0' and a.memberid = {memberid}
            """.stripMargin
        ).on('memberid -> id).as(myInvest.*)
        for(i <- list){
            i
            val endTime = i.endtime.getOrElse(new DateTime()).toDate
            val days = DateTimeUtils.difference(new Date,endTime)._1
            i.day = days
        }
        list
    }
}

