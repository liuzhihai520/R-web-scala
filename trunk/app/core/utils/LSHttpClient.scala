package core.utils

import com.sun.jersey.api.client.Client
import com.sun.jersey.api.client.ClientResponse
import com.sun.jersey.api.client.filter.HTTPBasicAuthFilter
import com.sun.jersey.core.util.MultivaluedMapImpl
import javax.ws.rs.core.MediaType

import org.json.JSONObject
import play.api.Logger
;

/**
  * 方法描述:螺丝帽短信发送
  *
  * author 小刘
  * version v1.0
  * date 2015/12/3
  */
object LSHttpClient {
    //发送客户端
    def LSsend(tel:String,code:Int,msg:String) = {
        //实例化客户端
        val client = Client.create()
        client.addFilter(new HTTPBasicAuthFilter("api","76f55f38f725ac1ff1fd0099be6d2b89"))
        val webResource = client.resource("http://sms-api.luosimao.com/v1/send.json")
        val formData = new MultivaluedMapImpl()
        formData.add("mobile", tel)
        formData.add("message", msg)
        val response =  webResource.`type`( MediaType.APPLICATION_FORM_URLENCODED ).post(classOf[ClientResponse],formData)
        val textEntity = response.getEntity(classOf[String])
        //Http返回状态
        val status = response.getStatus()
        val httpResponse =  textEntity
        var error_code = -1
        var error_msg = ""
        try{
            val jsonObj = new JSONObject(httpResponse)
            error_code = jsonObj.getInt("error")
            error_msg = jsonObj.getString("msg")
        }catch {
            case e:Exception =>
            Logger.error("短信发送错误:"+e)
            error_code = -2
            error_msg = "发送短信异常"
        }
        (error_code,error_msg,status)
    }
}
