package core.utils

import play.api.mvc.{RequestHeader, Request}
import play.api.Logger

/**
  * IP地址
  */
object IPUtil {

    val IP_Pattern = "((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))"

    /**
      * get ip address
      *
      * @param request
      */
    def ipAddress(request: RequestHeader) = {
        try {
            var ip = request.headers.get("x-forwarded-for").getOrElse("")
            if (ip.equals("") || "unknown".equalsIgnoreCase(ip) && (!isIp(ip))) {
                ip = request.headers.get("Proxy-Client-IP").getOrElse("")
            }
            if (ip.equals("") || "unknown".equalsIgnoreCase(ip) && (!isIp(ip))) {
                ip = request.headers.get("WL-Proxy-Client-IP").getOrElse("")
            }
            if (ip.equals("") || "unknown".equalsIgnoreCase(ip) && (!isIp(ip))) {
                ip = request.remoteAddress
            }
            ip.split(",")(0)
        } catch {
            case e: Exception => Logger.error("ipAddress header is really exite?" + e.toString)
                ""
        }
    }

    /**
      * IP转成数字类型
      *
      * @param strIP
      * @return
      */
    def ipToLong(strIP: String) = {
        val ip = new Array[Long](4)
        val position1 = strIP.indexOf(".")
        val position2 = strIP.indexOf(".", position1 + 1)
        val position3 = strIP.indexOf(".", position2 + 1)
        ip(0) = strIP.substring(0, position1).toLong
        ip(1) = strIP.substring(position1 + 1, position2).toLong
        ip(2) = strIP.substring(position2 + 1, position3).toLong
        ip(3) = strIP.substring(position3 + 1).toLong
        // ip1*256*256*256+ip2*256*256+ip3*256+ip4
        (ip(0) << 24) + (ip(1) << 16) + (ip(2) << 8) + ip(3)
    }


    /**
      *
      * @param longIp
      */
    def longToIP(longIp: Long) = {
        val sb = new StringBuffer
        //直接右移24位
        sb.append(String.valueOf((longIp >>> 24)))
        sb.append(".")
        //将高8位置0，然后右移16位
        sb.append(String.valueOf((longIp & 0x00FFFFFF) >>> 16))
        sb.append(".")
        //将高16位置0，然后右移8位
        sb.append(String.valueOf((longIp & 0x0000FFFF) >>> 8))
        sb.append(".")
        //将高24位置0
        sb.append(String.valueOf((longIp & 0x000000FF)))
        sb.toString()
    }

    private def isIp(ip: String): Boolean = {
        ip.matches(IP_Pattern)
    }
}
