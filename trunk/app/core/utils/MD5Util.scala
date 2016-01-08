package core.utils

import play.api.libs.Codecs

/**
  * 方法描述:MD5加密
  *
  * author 小刘
  * version v1.0
  * date 2015/11/28
  */
object MD5Util {

  /**
    * MD5密码加密
    * @param password
    * @param salt
    * @return
      */
  def passMd5(password:String,salt:String) = {
    val md5 = Codecs.md5(password.getBytes)
    Codecs.md5(md5.getBytes)
  }

  /**
   * MD5加密
   *
   *
   * @param password
   * @return
   */
  def getMD5(password: String): String = {
    val md5 = Codecs.md5(password.getBytes).substring(8, 24)
    Codecs.md5(md5.getBytes())
  }

  /**
   * 写入CookieMD5加密
   *
   *
   * @param userId
   * @param password
   * @return userIdMD5前8位+密码md5取中间16位+userId后8位
   */
  def getCookieMD5(userId: String, password: String): String = {
    val md5 = Codecs.md5(password.getBytes).substring(8, 24)
    val userIdMd5 = Codecs.md5(userId.getBytes())
    userIdMd5.substring(0, 7) + md5 + userIdMd5.substring(25, 32)
  }

  //MD5
  def md5(str:String) = {
    Codecs.md5(str.getBytes)
  }
}
