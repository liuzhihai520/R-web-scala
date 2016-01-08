package core.utils

import java.util.UUID

/**
 * Created by Sili Jiang on 14-5-8.
 */
object UUIDUtil {

  /**
   * 去掉-号的uuid
   *
   * @return
   */
  def uuid = {
    val s = UUID.randomUUID().toString
    s.substring(0, 8) + s.substring(9, 13) + s.substring(14, 18) + s.substring(19, 23) + s.substring(24)
  }
}
