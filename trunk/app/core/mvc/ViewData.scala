package core.mvc

import core.utils.KeyUtils
import model.{Member, Link}
import play.api.Play
import play.api.mvc._
import scala.collection.mutable
import play.api.i18n.Messages.Implicits._
import play.api.i18n.{Messages, Lang}
import play.api.libs.json.Json
import core.auth.Identity
import play.api.libs.Crypto
import play.api.Play.current

/**
  * 视图数据
  */
class ViewData[A](val request: Request[A])(implicit val lang: Lang) extends WrappedRequest[A](request) {
    //参数
    val instance = mutable.LinkedHashMap.empty[String, Any]
    //是否登录
    var isAuth: Boolean = false
    //用户信息
    private var _identity: Option[Identity] = None
    //css样式引用
    val styles = mutable.LinkedHashMap.empty[String, Int]
    //js引用
    val scripts = mutable.LinkedHashMap.empty[String, Int]

    //头像
    val static = Play.current.configuration.getString("static").getOrElse("")

   //admin平台路径
   val adminstatic = Play.current.configuration.getString("adminstatic").getOrElse("")

   //友情链接
   val linkList = Link.linkList("HZYQ-YQLJ")

   //TOP-IDNEX
   var topIdex = 1

   //是否认证
   var isMauth = false

   //资料填写进度
   var progress = 0


    //获取cookie
    def apply() = {
        if (!_identity.isDefined && request.cookies.get(KeyUtils.USER_INFO).isDefined) {
            _identity = Json.fromJson[Identity](Json.parse(Crypto.decryptAES(request.cookies.get(KeyUtils.USER_INFO).get.value))).asOpt
            isAuth = _identity.isDefined
        }
        this
    }

    /**
      * 已登录的用户对象
      */
    def identity: Identity = {
        if (!isAuth || _identity == None)
            Identity(0, "","")
        else {
            play.api.cache.Cache.getOrElse[Identity]("identity_" + _identity.get.accountname, 60) {
                Identity.apply(_identity.get.id, _identity.get.accountname,_identity.get.tel)
            }
            //是否实名认证
            isMauth = Member.isAuth(_identity.get.id)
            //资料进度
            progress = Member.progress(_identity.get.id)
            _identity.get
        }
    }

    def style(style: String)(implicit priority: Int = 500) {
        styles.put(style, priority)
    }

    def script(script: String)(implicit priority: Int = 500) {
        scripts.put(script, priority)
    }

    def apply(message: String, objects: Any*) = Messages(message, objects: _*)

    /**
      * 获得一个值
      * @param key
      * @tparam T
      * @return
      */
    def get[T](key: String): T = this.instance.get(key).getOrElse(None).asInstanceOf[T]

    def put(key: String, value: Any): Option[Any] = this.instance.put(key, value)

    def remove(key: String): Option[Any] = this.instance.remove(key)

    def +=(kv: (String, Any)): mutable.LinkedHashMap[String, Any] = {
        this.instance.put(kv._1, kv._2); this.instance
    }

    def +=(elem1: (String, Any), elem2: (String, Any), elems: (String, Any)*): mutable.LinkedHashMap[String, Any] = this += elem1 += elem2 ++= elems

    def -=(key: String): mutable.LinkedHashMap[String, Any] = {
        this.instance.remove(key); this.instance
    }
}