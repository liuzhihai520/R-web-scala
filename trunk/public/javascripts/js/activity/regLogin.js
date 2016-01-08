/**
 * 方法描述:项目中的登录注册
 *
 * author 小刘
 * version v1.0
 * date 2015/12/2
 */
var RegLogin = {
    //倒计时
    countDown:60,
    //是否注册
    isReg:0,
    //验证码
    flushCode: function () {
        $("#captcha").attr("src", "getCaptcha?v=" + Math.random());
    },
    //登录
    login: function () {

    }
}

$(function(){
    //登录
    $("#LoginSub").click(function(){
        RegLogin.login();
    });

    $("#loginPhone").bind("focus blur",function(){
        $("#loginPhoneError").html("");
    });
    $("#loginPassword").bind("focus blur",function(){
        $("#loginPasswordError").html("");
    });

});

