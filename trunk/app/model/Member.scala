package model

import anorm._
import anorm.SqlParser._
import core.utils.{MD5Util, RandomUtils}
import form.{UserAuthForm, MemberForm}
import org.joda.time.DateTime
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:描述
  *
  * author 小刘
  * version v1.0
  * date 2015/11/29
  */
case class Member(id:Long,accountname:String,name:Option[String],sex:Option[String],card:Option[String],cardfront:Option[String],
    cardcon:Option[String],userandCard:Option[String],head:Option[String],birthday:Option[DateTime],tel:Option[String],email:Option[String],
    pcname:Option[String],job:Option[Long],description:Option[String],delflag:String,passdescription:Option[String],status:Option[String],
    addtime:Option[DateTime],authenticationtime:Option[DateTime],errorcount:Int,audituserid:Long,investmentideas:Option[String],city:Option[String]
)

object Member {
    val member = {
        get[Long]("id")~
            get[String]("accountname")~
            get[Option[String]]("name")~
            get[Option[String]]("sex")~
            get[Option[String]]("card")~
            get[Option[String]]("cardfront")~
            get[Option[String]]("cardcon")~
            get[Option[String]]("userandCard")~
            get[Option[String]]("head")~
            get[Option[DateTime]]("birthday")~
            get[Option[String]]("tel")~
            get[Option[String]]("email")~
            get[Option[String]]("pcname")~
            get[Option[Long]]("job")~
            get[Option[String]]("description")~
            get[String]("delflag")~
            get[Option[String]]("passdescription")~
            get[Option[String]]("status")~
            get[Option[DateTime]]("addtime")~
            get[Option[DateTime]]("authenticationtime")~
            get[Int]("errorcount")~
            get[Long]("audituserid")~
            get[Option[String]]("investmentideas")~
            get[Option[String]]("city") map {
            case id~accountname~name~sex~card~cardfront~cardcon~userandCard~head~birthday~tel~email~pcname~job~description~delflag~passdescription~status~addtime~authenticationtime~errorcount~audituserid~investmentideas~city =>
            Member(id,accountname,name,sex,card,cardfront,cardcon,userandCard,head,birthday,tel,email,pcname,job,description,delflag,passdescription,status,addtime,authenticationtime,errorcount,audituserid,investmentideas,city)
        }
    }

    //会员信息
    def getMember(id:Long) = DB.withConnection {
        implicit con =>
        val user = SQL(
            """
              |select id,accountname,name,sex,card,cardfront,cardcon,userandCard,head,birthday,
              |tel,email,pcname,job,description,delflag,passdescription,status,addtime,
              |authenticationtime,errorcount,audituserid,investmentideas,city
              |from r_member where id = {id}
            """.stripMargin
        ).on('id -> id).as(member.singleOpt)
        user
    }

    //保存编辑会员信息
    def saveMember(data:MemberForm,id:Long,oldName:String) = DB.withConnection {
        implicit con =>
        val city = data.province+"-"+data.city
        val count = SQL(
            """
              |select count(0) as getCount from r_member where accountname = {accountname} and accountname != {oldName}
            """.stripMargin
        ).on('accountname -> data.accountname,'oldName -> oldName).as(scalar[Int].single)
        if(count > 0){
            -1
        }else{
            //更新信息
            val i = SQL(
                """
                  |update r_member set accountname = {accountname},head = {head}, name = {name},sex = {sex},birthday = {birthday},tel = {tel},email = {email},pcname = {pcname},
                  |investmentideas = {investmentideas},description = {description},job = {job},city = {city} where id = {id}
                """.stripMargin
            ).on('accountname -> data.accountname,'head -> data.head,'name -> data.name,'sex -> data.sex,'birthday -> data.birthday,'tel -> data.tel,'email -> data.email,'pcname -> data.pcname,
                'investmentideas -> data.investmentideas,'description -> data.description,'job -> data.job,'city -> city,'id -> id).executeUpdate()
            i
        }
    }

    //用户实名认证
    def memberAuth(data:UserAuthForm,userId:Long) = DB.withConnection {
        implicit con =>
        val id = SQL(
            """
              |update r_member set name = {name},card = {card},cardfront = {cardfront},cardcon = {cardcon},userandcard = {userandcard},creditImg = {creditImg},status = 3
              |where id = {id}
            """.stripMargin
        ).on('name -> data.name,'card -> data.card,'cardfront -> data.front,'cardcon -> data.back,'userandcard -> data.hand,'creditImg -> data.credit,'id -> userId).executeUpdate()
        id
    }

    //密码更改
    def updatePassword(id:Long,password:String) = DB.withConnection {
        implicit con =>
        //生成随机字符串
        val salt = RandomUtils.getRandomStr(4)
        val hash = MD5Util.passMd5(password,salt)
        SQL(
            """
              |update r_member set password = {password},salt = {salt} where id = {id}
            """.stripMargin
        ).on('password -> hash,'salt -> salt,'id -> id).executeUpdate()
    }

    //获取密码
    def getUserPass(id:Long) = DB.withConnection {
        implicit con =>
        val map = SQL(
            """
              |select password,salt from r_member where id = {id}
            """.stripMargin
        ).on('id -> id).as(
            (get[String]("password") ~ get[String]("salt") map {
                case password ~ salt => (password, salt)
            }).singleOpt).getOrElse(("", "")
        )
        map
    }

    //是否实名认证
    def isAuth(id:Long) = DB.withConnection {
        implicit con=>
        val auth = SQL(
            """
              |select count(0) as getCount from r_member where id = {id} and status = '2'
            """.stripMargin
        ).on('id -> id).as(scalar[Int].single)
        if(auth > 0){
            true
        }else{
            false
        }
    }

    //进度
    def progress(id:Long) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |SELECT SUM(num1+num2+num3+num4+num5+num6+num7+num8+num9+num10) AS num FROM (
              |    SELECT
              |        CASE accountname WHEN '' THEN 0 ELSE 1 END AS num1,
              |        CASE tel WHEN '' THEN 0 ELSE 1 END AS num2,
              |        CASE `name` WHEN '' THEN 0 ELSE 1 END AS num3,
              |        CASE city WHEN '' THEN 0 ELSE 1 END AS num4,
              |        CASE sex WHEN '' THEN 0 ELSE 1 END AS num5,
              |        CASE head WHEN '' THEN 0 ELSE 1 END AS num6,
              |        CASE birthday WHEN '' THEN 0 ELSE 1 END AS num7,
              |        CASE email WHEN '' THEN 0 ELSE 1 END AS num8,
              |        CASE pcname WHEN '' THEN 0 ELSE 1 END AS num9,
              |        CASE job WHEN 0 THEN 0 ELSE 1 END AS num10
              |    FROM (
              |        SELECT
              |            IFNULL(accountname,'') AS accountname,IFNULL(tel,'') AS tel,IFNULL(`name`,'') AS `name`,
              |            IFNULL(city,'') AS city,IFNULL(sex,'') AS sex,
              |            IFNULL(head,'') AS head,IFNULL(birthday,'') AS birthday,
              |            IFNULL(email,'') AS email,IFNULL(pcname,'') AS pcname,
              |            IFNULL(job,0) AS job
              |            FROM r_member WHERE id = {userId}
              |        ) a
              |)t
            """.stripMargin
        ).on('userId -> id).as(scalar[Int].single)
    }
}

//项目报名个人信息
case class ItemMember (id:Long,name:Option[String],sex:Option[String],email:Option[String],pcname:Option[String],job:Option[Long],birthday:Option[DateTime])
object ItemMember {
    val itemMember = {
        get[Long]("id")~
        get[Option[String]]("name")~
        get[Option[String]]("sex")~
        get[Option[String]]("email")~
        get[Option[String]]("pcname")~
        get[Option[Long]]("job")~
        get[Option[DateTime]]("birthday") map{
            case id~name~sex~email~pcname~job~birthday =>
            ItemMember(id,name,sex,email,pcname,job,birthday)
        }
    }

    def sigunMember(id:Long) = DB.withConnection {
        implicit con =>
        SQL(
            """
              |select id,name,sex,email,pcname,job,birthday from r_member where id = {id}
            """.stripMargin
        ).on('id -> id).as(itemMember.singleOpt).getOrElse(ItemMember(0,Option[String](""),Option[String](""),Option[String](""),Option[String](""),Option[Long](0),Option[DateTime](new DateTime())))
    }
}
