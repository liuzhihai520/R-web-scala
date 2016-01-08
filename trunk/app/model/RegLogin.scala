package model

import anorm._
import anorm.SqlParser._
import core.utils.{MD5Util, RandomUtils}
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:描述
  *
  * author 小刘
  * version v1.0
  * date 2015/11/28
  */
case class RegLogin (id:Long,accountname:String,password:String,salt:String,delflag:String,tel:String)
object RegLogin {
    val regLogin = {
        get[Long]("id")~
        get[String]("accountname")~
        get[String]("password")~
        get[String]("salt")~
        get[String]("delflag")~
        get[String]("tel") map {
            case id~accountname~password~salt~delflag~tel =>
            RegLogin(id,accountname,password,salt,delflag,tel)
        }
    }

    //手机号是否注册
    def isReg(tel:String) = DB.withConnection {
        implicit con =>
        val count  = SQL(
            """
              |select count(0) as getCount from r_member where tel = {accountname}
            """.stripMargin
        ).on('accountname -> tel).as(scalar[Int].single)
        count
    }

    //账号是否注册
    def isAccount(account:String) = DB.withConnection {
        implicit con=>
        val count = SQL(
            """
              |select count(0) as getCount from r_member where accountname = {accountname}
            """.stripMargin
        ).on('accountname -> account).as(scalar[Int].single)
        count
    }

    //账号是否注册
    def hasAccount(account:String,oldName:String) = DB.withConnection {
        implicit con=>
        val count = SQL(
            """
              |select count(0) as getCount from r_member where accountname = {accountname} and accountname != {old}
            """.stripMargin
        ).on('accountname -> account,'old -> oldName).as(scalar[Int].single)
        count
    }

    //注册
    def reg(accountname:String,tel:String,password:String) = DB.withConnection {
        implicit con =>
        var status:Long = 0
        //用户是否注册
        val isAccount = SQL(
            """
              |select count(*) as getcount from r_member where accountname = {accountname}
            """.stripMargin
        ).on('accountname -> accountname).as(scalar[Int].single)
        //手机号是否注册
        val isTel = SQL(
            """
              |select count(*) as getcount from r_member where tel = {tel}
            """.stripMargin
        ).on('tel -> tel).as(scalar[Int].single)
        if(isAccount > 0){
            //已注册的账号
            status = -1
        }else if(isTel > 0){
            //已注册的手机号
            status = -2
        }else{
            //生成随机字符串
            val salt = RandomUtils.getRandomStr(4)
            val hash = MD5Util.passMd5(password,salt)
            val id: Option[Long] = SQL(
                """
                  |insert into r_member (accountname,password,salt,tel) values({accountname},{password},{salt},{tel})
                """.stripMargin
            ).on('accountname -> accountname,'password -> hash,'salt -> salt,'tel -> tel).executeInsert()
            status = id.getOrElse(-1)
        }
        status
    }

    //登录
    def login(account:String,password:String) = DB.withConnection {
        implicit conn =>
        //查询登录用户数据
        val tempUser = SQL(
            """
              |select id,accountname,password,salt,delflag,tel from r_member where accountname = {accountname} or tel = {tel}
            """.stripMargin
        ).on('accountname -> account,'tel -> account).as(regLogin.singleOpt).getOrElse(RegLogin(0,"","","","0",""))
        //格式化密码
        val hash = MD5Util.passMd5(password,tempUser.salt)
        val user = SQL(
            """
              |select id,accountname,password,salt,delflag,tel from r_member
              |where (accountname = {accountname} or tel = {tel}) and password = {password}
            """.stripMargin
        ).on('accountname -> account,'tel -> account,'password -> hash).as(regLogin.singleOpt).getOrElse(RegLogin(0,"","","","0",""))
        user
    }

    //忘记密码/修改
    def forget(tel:String,password:String) = DB.withConnection {
        implicit conn =>
        //生成随机字符串
        val salt = RandomUtils.getRandomStr(4)
        val hash = MD5Util.passMd5(password,salt)
        SQL(
            """
              |update r_member set password = {password},salt = {salt} where accountname = {accountname}
            """.stripMargin
        ).on('password -> hash,'salt -> salt,'accountname -> tel).executeUpdate()
    }

    //查询登陆错误次数
    def isError(accountname:String) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |select errorcount from r_member where accountname = {accountname}
            """.stripMargin
        ).on('accountname -> accountname).as(scalar[Int].singleOpt).getOrElse(0)
    }

    //登陆错误次数
    def loginError(accountname:String) = DB.withConnection {
        implicit con=>
        SQL(
            """
              |update r_member set errorcount = errorcount+1 where accountname = {accountname} or tel = {tel}
            """.stripMargin
        ).on('accountname -> accountname,'tel -> accountname).executeUpdate()
    }

    //回执登陆次数
    def loginErrorBack(accountname:String) = DB.withConnection {
        implicit con=>
            SQL(
                """
                  |update r_member set errorcount = 0 where accountname = {accountname} or tel = {tel}
                """.stripMargin
            ).on('accountname -> accountname,'tel -> accountname).executeUpdate()
    }
}
