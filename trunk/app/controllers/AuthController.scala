package controllers

import core.auth.Identity
import core.mvc.{Action, WAction}
import core.utils.{DeEnCode, KeyUtils, CookieUtil, RandomUtils}
import form.{ForgetForm, RegForm, LoginForm}
import model.RegLogin
import play.api.libs.Crypto
import play.api.libs.json.Json
import play.api.mvc.Controller

/**
  * 方法描述:注册/登录
  *
  * author 小刘
  * version v1.0
  * date 2015/11/28
  */
class AuthController extends Controller{

    //注册/登录
    def regLogin = WAction{
        implicit request =>
        Ok(views.html.login.login(RegForm.regForm,LoginForm.loginForm,ForgetForm.forgetForm))
        .discardingCookies(CookieUtil.toDiscardingCookie(KeyUtils.USER_INFO)).withHeaders(
            PRAGMA -> "No-cache",
            CACHE_CONTROL -> "no-store",
            EXPIRES -> "0"
        )
    }

    //手机号是否注册
    def isReg(tel:String) = Action{
        implicit request =>
        val isReg = RegLogin.isReg(tel)
        Ok(s"""{"code":"$isReg"}""").as("text/json")
    }

    //账号是否注册
    def isAccount(account:String) = Action{
        implicit request=>
        val isAccount = RegLogin.isAccount(account)
        Ok(s"""{"code":"$isAccount"}""").as("text/json")
    }

    //注册
    def reg = WAction{
        implicit dataView =>
            RegForm.regForm.bindFromRequest.fold(
                error => {
                    BadRequest(views.html.login.login(error,LoginForm.loginForm,ForgetForm.forgetForm))
                },
                data => {
                    //验证码
                    val code = dataView.request.session.get(KeyUtils.VCODE).getOrElse("-1")
                    //短信验证码
                    var phoneCode = ""
                    if(dataView.request.cookies.get("registerCode").isDefined){
                        phoneCode = Crypto.decryptAES(dataView.request.cookies.get("registerCode").get.value,"Rregister")
                    }
                    if(code.equals("-1") || !data.regCode.equals(code)){
                        BadRequest(views.html.login.login(RegForm.regForm.withError("regCodeError",""),LoginForm.loginForm,ForgetForm.forgetForm))
                    }else if(phoneCode.equals("") || !data.sortCode.equals(phoneCode)){
                        BadRequest(views.html.login.login(RegForm.regForm.withError("regPhoneCodeError",""),LoginForm.loginForm,ForgetForm.forgetForm))
                    }else{
                        //注册
                        val status = RegLogin.reg(data.account,data.tel,data.regPassword)
                        if(status == -1){
                            BadRequest(views.html.login.login(RegForm.regForm.withError("isAccountError",""),LoginForm.loginForm,ForgetForm.forgetForm))
                        }else if(status == -2){
                            BadRequest(views.html.login.login(RegForm.regForm.withError("isTelError",""),LoginForm.loginForm,ForgetForm.forgetForm))
                        }else{
                            //初始化用户对象
                            val idt = Identity(status,data.account,data.tel)
                            Redirect("/").withCookies(
                                CookieUtil.toRememberCookie(KeyUtils.USER_INFO, Crypto.encryptAES(Json.stringify(Json.toJson(idt))))
                            ).discardingCookies(CookieUtil.toDiscardingCookie("registerCode"))
                            .discardingCookies(CookieUtil.toDiscardingCookie("passCode")).withSession(dataView.request.session - ("vcode"))
                        }
                    }
                }
            )
    }

    //登录
    def login = WAction{
        implicit dataView =>
            LoginForm.loginForm.bindFromRequest.fold(
                error => {
                    BadRequest(views.html.login.login(RegForm.regForm,error,ForgetForm.forgetForm))
                },
                data => {
                    //登陆错误次数
                    val isError = RegLogin.isError(data.accountname)
                    //验证码
                    val code = dataView.request.session.get(KeyUtils.VCODE).getOrElse("-1")
                    //登录
                    val user = RegLogin.login(data.accountname,data.loginPassword)
                    if(user.id == 0){
                        BadRequest(views.html.login.login(RegForm.regForm,LoginForm.loginForm.withError("loginError",""),ForgetForm.forgetForm))
                    }else if(!user.delflag.equals("0")){
                        BadRequest(views.html.login.login(RegForm.regForm,LoginForm.loginForm.withError("loginAuthError",""),ForgetForm.forgetForm))
                    }else{
                        //初始化用户对象
                        val idt = Identity(user.id,user.accountname,user.tel)
                        Redirect("/").withCookies(
                            CookieUtil.toRememberCookie(KeyUtils.USER_INFO, Crypto.encryptAES(Json.stringify(Json.toJson(idt))))
                        )
                    }
                }
            )
    }

    //忘记密码
    def forget = WAction {
        implicit dataView =>
        ForgetForm.forgetForm.bindFromRequest.fold(
            error => {
                BadRequest(views.html.login.login(RegForm.regForm,LoginForm.loginForm,error))
            },
            data => {
                //验证码
                val code = dataView.request.session.get("vcode").getOrElse("-1")
                //短信验证码
                var phoneCode = ""
                if(dataView.request.cookies.get("forgotCode").isDefined){
                    phoneCode = Crypto.decryptAES(dataView.request.cookies.get("forgotCode").get.value,"Rforgot")
                }
                //手机号码是否存在
                val count = RegLogin.isReg(data.forgetPhone)
                if(count == 0){
                    BadRequest(views.html.login.login(RegForm.regForm,LoginForm.loginForm,ForgetForm.forgetForm.withError("forgetPhoneError","")))
                }else if(!data.forgetCode.equals(code)){
                    BadRequest(views.html.login.login(RegForm.regForm,LoginForm.loginForm,ForgetForm.forgetForm.withError("forgetCodeError","")))
                }else if(!phoneCode.equals("") && !data.forgetPhoneCode.equals(phoneCode)){
                    BadRequest(views.html.login.login(RegForm.regForm,LoginForm.loginForm,ForgetForm.forgetForm.withError("phoneCodeError","")))
                }else{
                    RegLogin.forget(data.forgetPhone,data.newPassword)
                    Redirect("/regLogin")
                }
            }
        )
    }

    //退出登录
    def logout = Action {
        implicit request =>
            Redirect("/")
                .discardingCookies(
                    CookieUtil.toDiscardingCookie(KeyUtils.USER_INFO)
                ).withHeaders(
                PRAGMA -> "No-cache",
                CACHE_CONTROL -> "no-store",
                EXPIRES -> "0"
            )
    }

    //Ajax登录
    def ajaxLogin = WAction{
        implicit dataView =>
        var json = ""
        val map = dataView.request.body.asFormUrlEncoded
        //用户名/手机号
        val username = map.get.get("username").getOrElse(Seq(""))(0)
        //密码
        val password = map.get.get("password").getOrElse(Seq(""))(0)
        //验证码
        val code = dataView.request.session.get("vcode").getOrElse("-1")
        val userCode = map.get.get("code").getOrElse(Seq(""))(0)
        //登陆错误次数
        val isError = RegLogin.isError(username)
        if(username == null || username.equals("")){
            json = """{"code" : 1, "msg" : "用户名/密码不正确"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else if(password == null || password.equals("")){
            json = """{"code" : 2, "msg" : "密码错误"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else if(isError >= 5 && !code.equals(userCode)){
            json = """{"code" : 5, "msg" : "图形验证码错误"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else {
            //登录
            val user = RegLogin.login(username,password)
            if(user.id == 0){
                //登陆错误次数记录
                RegLogin.loginError(username)
                json = """{"code" : 3, "msg" : "用户名/密码不正确"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            }else if(!user.delflag.equals("0")){
                json = """{"code" : 4, "msg" : "该账号已被锁定,请联系客服[021-20226053]"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            }else{
                //充值登陆错误次数
                RegLogin.loginErrorBack(username)
                //初始化用户对象
                val idt = Identity(user.id,user.accountname,user.tel)
                json = """{"code" : 0, "msg" : "登录成功"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json").withCookies(CookieUtil.toRememberCookie(KeyUtils.USER_INFO, Crypto.encryptAES(Json.stringify(Json.toJson(idt)))))
                .discardingCookies(CookieUtil.toDiscardingCookie("passCode")).withSession(dataView.request.session - ("vcode"))

            }
        }
    }

    //Ajax注册
    def ajaxReg = WAction{
        implicit dataView=>
        var json = ""
        //post参数
        val map = dataView.request.body.asFormUrlEncoded
        //用户名
        val account = map.get.get("account").getOrElse(Seq(""))(0)
        //手机号
        val phone = map.get.get("tel").getOrElse(Seq(""))(0)
        //密码
        val password = map.get.get("password").getOrElse(Seq(""))(0)
        //短信验证码
        val sortCode = map.get.get("sortCode").getOrElse(Seq(""))(0)
        //验证码
        val code = map.get.get("code").getOrElse(Seq("-1"))(0)
        //Cookie获取验证码
        val cookieCode = dataView.request.session.get(KeyUtils.VCODE).getOrElse("-1")
        //短信验证码
        var phoneCode = ""
        if(dataView.request.cookies.get("registerCode").isDefined){
            phoneCode = Crypto.decryptAES(dataView.request.cookies.get("registerCode").get.value,"Rregister")
        }
        if(account.equals("") || account.length < 2 || account.length > 18){
            json = """{"code" : 1, "msg" : "英文或数字2-18位字符"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else if(phone.equals("")){
            json = """{"code" : 2, "msg" : "输入的手机号码格式不正确"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else if(password.equals("") || password.length < 6 || password.length > 18){
            json = """{"code" : 3, "msg" : "输入的密码格式不正确"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else if(code.equals("-1") || !code.equals(cookieCode)){
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else if(sortCode.equals("") || !sortCode.equals(phoneCode)){
            json = """{"code" : 5, "msg" : "短信验证码错误"}"""
            Ok(Json.stringify(Json.parse(json))).as("text/json")
        }else{
            //注册
            val status = RegLogin.reg(account,phone,password)
            if(status == -1){
                json = """{"code" : 6, "msg" : "注册失败"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
            }else{
                //初始化用户对象
                val idt = Identity(status,account,phone)
                json = """{"code" : 0, "msg" : "注册成功"}"""
                Ok(Json.stringify(Json.parse(json))).as("text/json")
                .withCookies(CookieUtil.toRememberCookie(KeyUtils.USER_INFO, Crypto.encryptAES(Json.stringify(Json.toJson(idt)))))
                .discardingCookies(CookieUtil.toDiscardingCookie("registerCode"))
                .discardingCookies(CookieUtil.toDiscardingCookie("passCode")).withSession(dataView.request.session - ("vcode"))
            }
        }
    }
}
