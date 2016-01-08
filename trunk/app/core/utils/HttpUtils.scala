package core.utils

import java.util
import com.google.common.base.Charsets
import com.google.common.io.BaseEncoding
import com.google.gson.Gson
import com.squareup.okhttp._
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.json.Json

/**
  * 方法描述:描述
  *
  * author 小刘
  * version v1.0
  * date 2015/12/2
  */
object HttpUtils {

    //手机/模板/模板参数1/模板参数2[模版参数实例:您的验证码是{1}，请于{2}分钟内正确输入]
    def send(tel:String,template:String,data1:String,data2:String) = {
        //实例化OkHttp
        val client = new OkHttpClient()
        //声明URL
        val url = "https://app.cloopen.com:8883"
        //配置SID
        val sid = "aaf98f8950e15fc10150eb1365b416cd"
        //配置TOKEN
        val token = "8017d077dca748e3a4a75338b9bb775a"
        //设置时间戳格式
        val time = new DateTime().toString("yyyyMMddHHmmss")
        //加密SIG参数
        val sig = MD5Util.md5(sid+token+time)
        //声明路由
        val param = s"/2013-12-26/Accounts/$sid/SMS/TemplateSMS?sig=$sig"
        //设置JSON请求头
        val JSON = MediaType.parse("application/json; charset=utf-8")
        //实例化Gson
        val gson = new Gson()
        //封装模板参数
        val map = new util.HashMap[String,Object]()
        map.put("to",tel)
        map.put("appId","8a48b55150e162370150eb18d9984228")
        map.put("templateId",template)
        map.put("datas",Array[String](data1,data2))
        //设置POST的BODY
        val body = RequestBody.create(JSON,gson.toJson(map))

        //设置HTTP-HEADER
        val request = new Request.Builder()
        .header("Accept", "application/json")
        .addHeader("Content-Type", "application/json;charset=utf-8")
        .addHeader("Content-Length","256")
        .addHeader("Authorization", BaseEncoding.base64().encode(s"$sid:$time".getBytes(Charsets.UTF_8)))
        .url(url+param)
        .post(body)
        .build()
        //发送post请求
        val response = client.newCall(request).execute()
        val g:Gson = new Gson()
        //处理返回参数
        if (response.isSuccessful()) {
           println("返回的字符串是:"+response.body().string())
           val result = ResultUtil.fromJSON(response.body().string())
           0
        } else {
            1
        }
    }
}
