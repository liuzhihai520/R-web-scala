package core.utils

import java.util.Date

/**
  * 方法描述:TODO
  *
  * author 小刘
  * version v1.0
  * date 2015/12/2
  */
object DateTimeUtils {

    //时间差
    def difference(d1:Date,d2:Date) = {
        // 毫秒ms
        val diff = d2.getTime() - d1.getTime()

        //天
        val diffDays = diff / (24 * 60 * 60 * 1000)
        //小时
        val diffHours = diff / (60 * 60 * 1000) % 24
        //分钟
        val diffMinutes = diff / (60 * 1000) % 60
        //秒
        val diffSeconds = diff / 1000 % 60

        (diffDays,diffHours,diffMinutes,diffSeconds)
    }


}
