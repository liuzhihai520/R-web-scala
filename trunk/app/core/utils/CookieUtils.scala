package core.utils

import play.api.mvc.{DiscardingCookie, Cookie}

/**
  * 方法描述:TODO
  * author 小刘
  * version v1.0
  * date 2015/11/23
  */
object CookieUtil {

    //记住短信验证码Cookie
    def toCodeCookie(key:String,value:String):Cookie = {
        Cookie(
            key,
            value,
            Some(60),
            "/",
            domain = None,
            secure = false,
            httpOnly = false
        )
    }

    /**
      * Set cookie
      *
      * @param key the cookie name
      * @param value the cookie value
      * @return
      */
    def toCookie(key: String, value: String): Cookie = {
        Cookie(
            key,
            value,
            None,
            "/",
            domain = None,
            secure = false,
            httpOnly = false
        )
    }

    /**
      * Set cookie remember
      *
      * @param key the cookie name
      * @param value the cookie value
      * @return
      */
    def toRememberCookie(key: String, value: String): Cookie = {
        Cookie(
            key,
            value,
            Some(60 * 120),
            "/",
            domain = None,
            //Option(".*"),
            secure = false,
            httpOnly = false
        )
    }

    /**
      * Set discarding cookie
      *
      * @param key the cookie name
      * @return
      */
    def toDiscardingCookie(key: String): DiscardingCookie = {
        DiscardingCookie(
            key, "/",
            domain = None
            //Option(".*")
        )
    }
}
