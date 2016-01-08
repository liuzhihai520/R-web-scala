package core.utils

import scala.util.Random

/**
  * 方法描述:随机数
  *
  * author 小刘
  * version v1.0
  * date 2015/11/28
  */
object RandomUtils {

    // 没有添加 I、O 的原因是避免和数字 1、0 混淆
    val ALPHA_NUMERIC = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"

    //产生一个固定范围（min-max 之间）的随机正整数。
    def getRandomInt(min:Int,max:Int) ={
        val random = (Math.random()*(max-min+1)+min)
        random.toInt
    }

    //产生固定长度的随机数字串。
    def getRandomNum(length:Int) = {
        val random = Math.random().toString.substring(2,(2+length))
        random
    }

    //生成随机字符串
    def getRandomStr(length:Int) = {
        Random.alphanumeric.take(length).mkString
    }
}
