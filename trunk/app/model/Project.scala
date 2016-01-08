package model

import anorm._
import anorm.SqlParser._
import core.utils.{DateUtils, DateTimeUtils, Pages}
import form.{CompanyForm, ProjectForm}
import org.joda.time.DateTime
import java.util.Date
import play.api.db.DB
import play.api.Play.current
import play.api.libs.json.Json

/**
  * 方法描述:项目
  *
  * author 小刘
  * version v1.0
  * date 2015/11/27
  */
case class ItemProject(id:Long,name:String,synopsis:Option[String],imgurl:Option[String],financingamount:Double,sign:Int,status:String,
    schedules:Double,endtime:Option[DateTime],var day:Long,tag:String,location:Option[String],financingsum:Double,description:Option[String],
    valueation:Option[Double],equity:Double,plan:String,use:Option[String],pcname:Option[String],listingcode:Option[String],web:Option[String],licensenum:Option[String],
    linkman:Option[String],linkmantel:Option[String],financingterm:Option[Int],startnum:Option[Double],shares:Option[Double],regcapital:Option[Double],islisting:Option[String]
)

object ItemProject {
    val itemProject = {
        get[Long]("id")~
        get[String]("name")~
        get[Option[String]]("synopsis")~
        get[Option[String]]("imgurl")~
        get[Double]("financingamount")~
        get[Int]("sign")~
        get[String]("status")~
        get[Double]("schedules")~
        get[Option[DateTime]]("endtime")~
        get[Long]("day")~
        get[String]("tag")~
        get[Option[String]]("location")~
        get[Double]("financingsum")~
        get[Option[String]]("description")~
        get[Option[Double]]("valueation")~
        get[Double]("equity")~
        get[String]("plan")~
        get[Option[String]]("use")~
        get[Option[String]]("pcname")~
        get[Option[String]]("listingcode")~
        get[Option[String]]("web")~
        get[Option[String]]("licensenum")~
        get[Option[String]]("linkman")~
        get[Option[String]]("linkmantel")~
        get[Option[Int]]("financingterm")~
        get[Option[Double]]("startnum")~
        get[Option[Double]]("shares")~
        get[Option[Double]]("regcapital")~
        get[Option[String]]("islisting") map{
            case id~name~synopsis~imgurl~financingamount~sign~status~schedules~endtime~day~tag~location~
            financingsum~description~valueation~equity~plan~use~pcname~listingcode~web~licensenum~linkman~linkmantel~financingterm~startnum~shares~regcapital~islisting =>
            ItemProject(id,name,synopsis,imgurl,financingamount,sign,status,schedules,endtime,day,tag,location,
            financingsum,description,valueation,equity,plan,use,pcname,listingcode,web,licensenum,linkman,linkmantel,financingterm,startnum,shares,regcapital,islisting)
        }
    }

    //首页项目列表
    def itemList(pageNumber:Int,index:Int,limit:Int) = DB.withConnection {
        implicit conn =>
        //总数
        val count = SQL(
            """
              |select count(0) as getCount from r_project where (status = '4' or status = '5') and isonline='1' and delflag = '0'
            """.stripMargin
        ).as(scalar[Int].single)
        //集合
        val list = SQL(
            """
              |select id,name,synopsis,imgurl,financingamount/10000 as financingamount,sign,status,description,valueation/10000 as valueation,equity,plan,
              |round(((financingamount/financingsum)*100),2) AS schedules,0 as day,endtime,tag,location,financingsum/10000 as financingsum,`use`,
              |pcname,listingcode,web,licensenum,linkman,linkmantel,financingterm,startnum/10000 as startnum,shares,regcapital/10000 as regcapital,islisting
              |from r_project  where (status = '4' or status = '5') and isonline='1'  and delflag = '0'  ORDER BY istop desc,status asc,addtime DESC
              |limit {index},{limit}
            """.stripMargin
        ).on('index -> index,'limit -> limit).as(itemProject.*)
        for(i <- list){
            i
            val endTime = i.endtime.getOrElse(new DateTime()).toDate
            var days = DateTimeUtils.difference(new Date,endTime)._1
            if(days < 0){
                days = 0
            }
            i.day = days
            i.schedules
        }
        Pages[ItemProject](list,count,5,pageNumber,limit)
    }

    //项目详情
    def itemInfo(id:Long) = DB.withConnection {
        implicit con =>
        val obj = SQL(
            """
              |select id,name,synopsis,imgurl,financingamount/10000 as financingamount,sign,status,description,valueation/10000 as valueation,equity,plan,
              |round(((financingamount/financingsum)*100),2) AS schedules,0 as day,endtime,tag,location,financingsum/10000 as financingsum,`use`,
              |pcname,listingcode,web,licensenum,linkman,linkmantel,financingterm,startnum/10000 as startnum,shares,regcapital/10000 as regcapital,islisting
              |from r_project where id = {id} and delflag='0'
            """.stripMargin
        ).on('id -> id).as(itemProject.singleOpt)
        val endTime = obj.get.endtime.getOrElse(new DateTime()).toDate
        val days = DateTimeUtils.difference(new Date,endTime)._1
        obj.get.day = days
        obj
    }

    //起投金额
    def startMoney(id:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |SELECT startnum/10000 as startnum FROM  r_project WHERE id = {id}
            """.stripMargin
        ).on('id -> id).as(scalar[Double].singleOpt).getOrElse(0.0)
    }
}

//我的融资
case class MyProject(id:Long,name:Option[String],status:Option[String],synopsis:Option[String],
financingsum:Double,financingamount:Double,sign:Int,tag:String,location:Option[String],imgurl:Option[String],passdescription:Option[String],endtime:Option[DateTime])
object MyProject {
    val myProject = {
        get[Long]("id")~
        get[Option[String]]("name")~
        get[Option[String]]("status")~
        get[Option[String]]("synopsis")~
        get[Double]("financingsum")~
        get[Double]("financingamount")~
        get[Int]("sign")~
        get[String]("tag")~
        get[Option[String]]("location")~
        get[Option[String]]("imgurl")~
        get[Option[String]]("passdescription")~
        get[Option[DateTime]]("endtime") map {
        case id~name~status~synopsis~financingsum~financingamount~sign~tag~location~imgurl~passdescription~endtime =>
            MyProject(id,name,status,synopsis,financingsum,financingamount,sign,tag,location,imgurl,passdescription,endtime)
        }
    }

    //我的融资
    def myProject(id:Long):List[MyProject] = DB.withConnection {
        implicit con =>
        val list = SQL(
            """
              |select id,name,status,ifnull(synopsis,'') as synopsis,financingsum/10000 as financingsum,financingamount/10000 as financingamount,sign,tag,location,imgurl,passdescription,endtime
              |from r_project where memberid = {memberid} and delflag='0' order by id desc
            """.stripMargin
        ).on('memberid -> id).as(myProject.*)
        list
    }
}

//项目发布
case class ProItem(id:Long,name:String,tag:String,typeid:Long,tradedescription:String,projectstage:String,financingsum:Double,
                   use:String,equity:Double,imgurl:String,plan:String,synopsis:String,description:String,status:String,
                   financingterm:Int,startnum:Double,shares:Double,regcapital:Double)
object ProItem {
    implicit val fmt = Json.format[ProItem]
    val proItem = {
        get[Long]("id")~
        get[String]("name")~
        get[String]("tag")~
        get[Long]("typeid")~
        get[String]("tradedescription")~
        get[String]("projectstage")~
        get[Double]("financingsum")~
        get[String]("use")~
        get[Double]("equity")~
        get[String]("imgurl")~
        get[String]("plan")~
        get[String]("synopsis")~
        get[String]("description")~
        get[String]("status")~
        get[Int]("financingterm")~
        get[Double]("startnum")~
        get[Double]("shares")~
        get[Double]("regcapital") map{
            case id~name~tag~typeid~tradedescription~projectstage~financingsum~use~equity~imgurl~plan~synopsis~description~status~financingterm~startnum~shares~regcapital =>
            ProItem(id,name,tag,typeid,tradedescription,projectstage,financingsum,use,equity,imgurl,plan,synopsis,description,status,financingterm,startnum,shares,regcapital)
        }
    }

    //查询项目信息
    def projectInfo(id:Long) = DB.withConnection {
        implicit con =>
        val obj = SQL(
            """
              |select id,name,tag,typeid,tradedescription,projectstage,financingsum/10000 as financingsum,
              |`use`,equity,imgurl,plan,synopsis,description,status,financingterm,startnum/10000 as startnum,shares,regcapital/10000 as regcapital
              |from r_project where id = {id} and delflag='0'
            """.stripMargin
        ).on('id -> id).as(proItem.singleOpt)
        obj
    }

    //保存项目信息
    def saveProItem(data:ProjectForm,userId:Long):Long = DB.withConnection {
        implicit con =>
        con.setAutoCommit(false)
        val date = DateUtils.addDay(new Date(),data.financingterm)
        //简介
        var synopsis = data.synopsis.replaceAll("""\n""", """<br/>""")
        synopsis = data.synopsis.replaceAll("""\r""", """<br/>""")
        //用途
        var use = data.use.replaceAll("""\n""", """<br/>""")
        use = data.use.replaceAll("""\r""", """<br/>""")
        //插入项目表
        val id: Option[Long] = SQL(
            """
              |insert into r_project(name,tag,typeid,tradedescription,projectstage,financingsum,`use`,equity,
              |imgurl,plan,synopsis,description,memberid,status,financingterm,endtime,shares,startnum,regcapital)
              |values({name},{tag},{typeid},{tradedescription},{projectstage},{financingsum},{use},{equity},
              |{imgurl},{plan},{synopsis},{description},{memberid},1,{financingterm},{endtime},{shares},{startnum},{regcapital})
            """.stripMargin
        ).on('name -> data.name,'tag -> data.tag,'typeid -> data.typeid,'tradedescription -> data.tradedescription,'projectstage -> data.projectstage,
            'financingsum -> (data.financingsum*10000),'use -> use,'equity -> data.equity,'imgurl -> data.imgurl,'plan -> data.plan,
            'synopsis -> synopsis,'description -> data.description,
            'memberid -> userId,'financingterm -> data.financingterm,
            'endtime -> date,'shares -> data.shares,'startnum -> (data.startnum*10000),'regcapital -> (data.regcapital*10000)
        ).executeInsert()

        //插入统计表
        SQL(
            """
              |insert into r_count(type,relevanceid) values(4,{id})
            """.stripMargin
        ).on('id -> id.getOrElse("0").toString.toLong).executeInsert()
        con.commit()
        id.getOrElse(-1)
    }

    //修改项目信息
    def updateProItem(data:ProjectForm,userId:Long) = DB.withConnection {
        implicit con =>
        //简介
        var synopsis = data.synopsis.replaceAll("""\n""", """<br/>""")
        synopsis = data.synopsis.replaceAll("""\r""", """<br/>""")
        //用途
        var use = data.use.replaceAll("""\n""", """<br/>""")
        use = data.use.replaceAll("""\r""", """<br/>""")
        SQL(
            """
              |update r_project set name = {name},tag={tag},typeid={typeid},tradedescription={tradedescription},
              |projectstage={projectstage},financingsum={financingsum},`use`={use},equity={equity},imgurl={imgurl},plan={plan},
              |synopsis={synopsis},description={description},status = '1',startnum = {startnum},shares = {shares},regcapital = {regcapital} where id = {id}
            """.stripMargin
        ).on('name -> data.name,'tag -> data.tag,'typeid -> data.typeid,'tradedescription ->data.tradedescription,'projectstage -> data.projectstage,
            'financingsum -> (data.financingsum*10000),'use -> use,'equity -> data.equity,'imgurl -> data.imgurl,'plan -> data.plan,
            'synopsis -> synopsis,'description -> data.description,'startnum -> (data.startnum*10000),'shares -> data.shares,'regcapital -> data.regcapital,'id -> data.id
        ).executeUpdate()
        data.id
    }

    //保存公司信息
    def saveCompany(data:CompanyForm) = DB.withConnection {
        implicit con =>
        val location = data.province+"-"+data.city
        var foundtime = ""
        if(data.foundtime == null || data.foundtime.equals("")){
            foundtime = "0000-00-00 00:00:00"
        }else{
            foundtime = data.foundtime+"-01 00:00:00"
        }
        val status = SQL(
            """
              |update r_project set pcname = {pcname},web = {web},location = {location},licensenum = {licensenum},
              |islisting={islisting},listingcode={listingcode},foundtime={foundtime},agencynum={agencynum},valueation={valueation},
              |referee={referee},refereetel={refereetel},linkman={linkman},linkmantel={linkmantel},linkmanjob={linkmanjob}
              |where id = {id}
            """.stripMargin
        ).on('pcname -> data.pcname,"web"  -> data.web,'location -> location,'licensenum -> data.licensenum,'islisting -> data.islisting,'listingcode -> data.listingcode,
            'foundtime -> foundtime,'agencynum -> data.agencynum,'valueation -> (data.valueation*10000),'referee -> data.referee,'refereetel -> data.refereetel,'linkman -> data.linkman,
            'linkmantel -> data.linkmantel,'linkmanjob -> data.linkmanjob,'id -> data.id
        ).executeUpdate()
        status
    }
}

//公司
case class Company(id:Long,pcname:Option[String],web:Option[String],location:Option[String],licensenum:Option[String],
islisting:Option[String],listingcode:Option[String],foundtime:Option[DateTime],agencynum:Option[Int],valueation:Option[Double],
referee:Option[String],refereetel:Option[String],linkman:Option[String],linkmantel:Option[String],linkmanjob:Option[Long],regcapital:Option[Double])

object Company {
    implicit val fmt = Json.format[Company]
    val company = {
        get[Long]("id")~
        get[Option[String]]("pcname")~
        get[Option[String]]("web")~
        get[Option[String]]("location")~
        get[Option[String]]("licensenum")~
        get[Option[String]]("islisting")~
        get[Option[String]]("listingcode")~
        get[Option[DateTime]]("foundtime")~
        get[Option[Int]]("agencynum")~
        get[Option[Double]]("valueation")~
        get[Option[String]]("referee")~
        get[Option[String]]("refereetel")~
        get[Option[String]]("linkman")~
        get[Option[String]]("linkmantel")~
        get[Option[Long]]("linkmanjob")~
        get[Option[Double]]("regcapital") map {
            case id~pcname~web~location~licensenum~islisting~listingcode~foundtime~agencynum~valueation~referee~refereetel~linkman~linkmantel~linkmanjob~regcapital =>
            Company(id,pcname,web,location,licensenum,islisting,listingcode,foundtime,agencynum,valueation,referee,referee,linkman,linkmantel,linkmanjob,regcapital)
        }
    }

    //公司信息
    def companyInfo(id:Long) = DB.withConnection {
        implicit con =>
        SQL(
            """
              |select id,pcname,web,location,licensenum,islisting,listingcode,foundtime,agencynum,
              |valueation/10000 as valueation,referee,refereetel,linkman,linkmantel,linkmanjob,regcapital/10000 as regcapital
              |from r_project where id = {id} and delflag='0'
            """.stripMargin
        ).on('id -> id).as(company.singleOpt)
    }

    //保存公司信息
    def saveCompany(data:CompanyForm) = DB.withConnection {
        implicit con=>
        val location = data.province+"-"+data.city
        var foundtime = ""
        if(foundtime == null || foundtime.equals("")){
            foundtime = "0000-00-00 00:00:00"
        }else{
            foundtime = data.foundtime+"-01 00:00:00"
        }
        val id:Option[Long] = SQL(
            """
              |insert into r_project (pcname,web,location,licensenum,islisting,listingcode,foundtime,agencynum,valueation,referee,refereetel,linkman,linkmantel,linkmanjob,regcapital)
              |values({pcname},{web},{location},{licensenum},{islisting},{listingcode},{foundtime},{agencynum},{valueation},{referee},{refereetel},{linkman},{linkmantel},{linkmanjob})
            """.stripMargin
        ).on('pcname -> data.pcname,"web"  -> data.web,'location -> location,'licensenum -> data.licensenum,'islisting -> data.islisting,'listingcode -> data.listingcode,
            'foundtime -> foundtime,'agencynum -> data.agencynum,'valueation -> (data.valueation*10000),'referee -> data.referee,'refereetel -> data.refereetel,'linkman -> data.linkman,
            'linkmantel -> data.linkmantel,'linkmanjob -> data.linkmanjob
        ).executeInsert()
        id.getOrElse(-1)
    }
}

case class CountDown(id:Long,time:String)
object CountDown {
    implicit val fmt = Json.format[CountDown]
}
