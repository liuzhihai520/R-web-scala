package model

import org.joda.time.DateTime
import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

/**
  * 方法描述:类型
  *
  * author 小刘
  * version v1.0
  * date 2015/11/29
  */
case class TypeClass (id:Long,name:String,logo:Option[String],typecode:String,orderlist:Int,description:Option[String],delflag:String,addtime:DateTime,userid:Long)
object TypeClass {
    val typeClass = {
        get[Long]("id")~
        get[String]("name")~
        get[Option[String]]("logo")~
        get[String]("typecode")~
        get[Int]("orderlist")~
        get[Option[String]]("description")~
        get[String]("delflag")~
        get[DateTime]("addtime")~
        get[Long]("userid") map{
            case id~name~logo~typecode~orderlist~description~delflag~addtime~userid =>
            TypeClass(id,name,logo,typecode,orderlist,description,delflag,addtime,userid)
        }
    }

    //类型集合
    def typeList(code:String):List[TypeClass] = DB.withConnection {
        implicit con =>
        val list = SQL(
            """
              |select * from r_type where delflag='0' AND code={code}
            """.stripMargin
        ).on('code -> code).as(typeClass.*)
        list
    }
}
