/**
 * 方法描述:登录/注册
 *
 * author 小刘
 * version v1.0
 * date 2015/11/28
 */

//全局设置
var RegLogin = {
    //验证码
    captcha:function(){
        $("#captcha").attr("src","getCaptcha?v="+Math.random());
    },
    fcaptcha:function(){
        $("#forgetCaptcha").attr("src","getCaptcha?v="+Math.random());
    },
    logCaptcha:function(){
        $("#logCaptcha").attr("src","getCaptcha?v="+Math.random());
    },
    //注册短信倒计时
    regCountDown:60,
    //找回密码短信倒计时
    findCountDown:60,
    //是否注册
    isReg:0,
    //验证账号是否注册
    isAccount:0,
    //注册验证手机号是否注册
    phoneReg:function(){
        var phone = $("#tel").val();
        $.ajax({
            type:"get",
            url: "/isReg",
            data: {tel:phone},
            dataType: "json",
            success: function(result){
                if(parseInt(result.code) > 0){
                    //已注册
                    RegLogin.isReg = 1;
                    $("#regPhoneError").html("此号码已注册");
                }else{
                    //未注册
                    RegLogin.isReg = 0;
                    $("#regPhoneError").html("");
                }
            }
        });
    },
    //找回密码验证手机号是否注册
    findPhoneReg:function(){
        var phone = $("#forgetPhone").val();
        $.ajax({
            type:"get",
            url: "/isReg",
            data: {tel:phone},
            dataType: "json",
            success: function(result){
                if(parseInt(result.code) == 0){
                    //未注册
                    RegLogin.isReg = 0;
                    $("#forgetPhoneError").html("此号码未注册");
                }else{
                    //已注册
                    RegLogin.isReg = 1;
                    $("#forgetPhoneError").html("");
                }
            }
        });
    },
    //验证账号是否注册
    accountReg:function(){
        //用户名
        var account = $("#account").val();
        $.ajax({
            type:"get",
            url: "/isAccount",
            data: {account:account},
            dataType: "json",
            success: function(result){
                if(parseInt(result.code) == 0){
                    //账号未注册
                    RegLogin.isAccount = 0;
                    $("#accountError").html("");
                }else{
                    //账号已注册
                    RegLogin.isAccount = 1;
                    $("#accountError").html("账号已注册");
                }
            }
        });
    },
    //注册验证/提交
    reg:function(){
        var account = $("#account").val();
        var phone = $("#tel").val();
        var password = $("#regPassword").val();
        var sortCode = $("#sortCode").val();
        var code = $("#regCode").val();
        if(!isUsername(account)){
            $("#account").focus();
            $("#accountError").html("英文或数字2-18位字符");
        }else if(!isPhone(phone)){
            $("#tel").focus();
            $("#regPhoneError").html("手机格式不正确");
        }else if(!checkPassword(password)){
            $("#regPassword").focus();
            $("#regPwdError").html("密码格式为6-16位字符");
        }else if(code.length != 4){
            $("#regCode").focus();
            $("#regCodeError").html("验证码为4位字符");
        }else if(!isPhoneCode(sortCode)){
            $("#sortCode").focus();
            $("#regPhoneCodeError").html("手机验证码为4位数字");
        }else if($("#agree").is(':checked') == false){
            $("#agreeError").html("请阅读服务条款");
        }else{
            $("#regForm").submit();
        }
    },
    //登录
    login:function(){
        //用户名
        var username = $("#username").val();
        var password = $("#password").val();
        var code = $("#code").val();
        if(!isUsername(username)){
            $("#username").focus();
            $("#usernameError").html("用户名长度为2-18位");
        }else if(!checkPassword(password)){
            $("#password").focus();
            $("#passwordError").html("密码格式为6-16位字符");
        }else{
            $.ajax({
                type:"post",
                url: "/ajaxLogin",
                data: {username:username,password:password,code:code},
                dataType: "json",
                success: function(result){
                    if(parseInt(result.code) == 1 || parseInt(result.code) == 3){
                        $("#username").focus();
                        $("#usernameError").html("用户名/密码错误");
                    }else if(parseInt(result.code) == 2){
                        $("#password").focus();
                        $("#passwordError").html("密码错误");
                    }else if(parseInt(result.code) == 4){
                        $("#username").focus();
                        $("#usernameError").html("该账号已被锁定,请联系客服[021-20226053]");
                    }else if(parseInt(result.code) == 5){
                        $("#username").focus();
                        $("#codebox").show();
                        $("#codeError").html("图形验证码错误");
                    }else{
                        window.location.href = "/";
                    }
                }
            });
        }
    },
    //忘记密码
    forget:function(){
        var forgetPhone = $("#forgetPhone").val();
        var newPassword = $("#newPassword").val();
        var forgetPhoneCode = $("#forgetPhoneCode").val();
        var forgetCode = $("#forgetCode").val();
        if(!isPhone(forgetPhone)){
            $("#forgetPhone").focus();
            $("#forgetPhoneError").html("手机格式不正确");
        }else if(!checkPassword(newPassword)){
            $("#newPassword").focus();
            $("#newPasswordError").html("密码格式为6-16位字符");
        }else if(!isPhoneCode(forgetPhoneCode)){
            $("#forgetPhoneCode").focus();
            $("#forgetPhoneCodeError").html("手机验证码为4位数字");
        }else if(forgetCode.length != 4){
            $("#forgetCode").focus();
            $("#forgetCodeError").html("验证码为4位字符");
        }else{
            $("#forgetForm").submit();
        }
    }
}

$(function(){

    //切换
    $('.box_control').find('a').click(function(){
        if($(this).hasClass('show')){
            $('.regist_box').show();
            $(this).toggleClass('show').html('忘记密码？')
        }else{
            $('.regist_box').hide();
            $(this).toggleClass('show').html('返回注册')
        }
    });

    //刷新验证码
    $("#reflush").click(function(){
        RegLogin.captcha();
    });

    $("#reflushCode").click(function(){
        RegLogin.fcaptcha();
    });

    $("#logReflush").click(function(){
        RegLogin.logCaptcha();
    });

    //账号是否注册
    $("#account").blur(function(){
        RegLogin.isAccount = 0;
        RegLogin.accountReg();
    });

    //验证手机号是否注册
    $("#tel").blur(function(){
        RegLogin.isReg = 0;
        RegLogin.phoneReg();
    });

    //验证手机号是否注册
    $("#forgetPhone").blur(function(){
        RegLogin.isReg = 0;
        RegLogin.findPhoneReg();
    });

    //注册
    $("#regSub").click(function(){
        RegLogin.reg();
    });

    //登录
    $("#login").click(function(){
        RegLogin.login();
    });

    //忘记密码
    $("#forgetSub").click(function(){
        RegLogin.forget();
    });

    //获取焦点
    $("#username").bind("focus blur",function(){
        $("#usernameError").html("");
    });
    $("#password").bind("focus blur",function(){
        $("#passwordError").html("");
    });
    $("#code").bind("focus blur",function(){
        $("#codeError").html("");
    });
    $("#regPhoneError").focus(function(){
        $("#regPwdError").html("");
    });
    $("#sortCode").focus(function(){
        $("#regPhoneCodeError").html("");
    });
    $("#regCode").focus(function(){
        $("#regCodeError").html("");
    });
    $("#newPassword").focus(function(){
        $("#newPasswordError").html("");
    });
    $("#forgetPhoneCode").focus(function(){
        $("#forgetPhoneCodeError").html("");
    });
    $("#forgetCode").focus(function(){
        $("#forgetCodeError").html("");
    });

    $('#regPassword').hideShowPassword({
        innerToggle: true,
        touchSupport: Modernizr.touch
    });

    $('#newPassword').hideShowPassword({
        innerToggle: true,
        touchSupport: Modernizr.touch
    });
});

//注册验证码倒计时
function getCode(){
    //手机号
    var phone = $("#tel").val();
    //验证码
    var regCode = $("#regCode").val();
    //服务器验证码
    var passCode = $.cookie("passCode");
    //判断验证码
    if(regCode != passCode){
        $("#regCodeError").html("验证码错误");
    }else if(!isPhone(phone)){
        $("#regPhoneError").html("手机格式不正确");
    }else if(RegLogin.isReg > 0){
        $("#regPhoneError").html("此号码已注册");
    }else{
        $.ajax({
            type:"post",
            url: "/sendSms",
            data: {telePhone:phone,codeType:0},
            dataType: "json",
            success: function(result){
                if(parseInt(result.code) == 0){
                    setTime();
                }else{
                    $("#regCodeError").html(result.msg);
                }
            }
        });
    }
}

//注册倒计时
function setTime(){
    if (RegLogin.regCountDown == 0) {
        $("#codeButton").removeAttr("disabled");
        $("#codeButton").html("免费获取验证码");
        RegLogin.regCountDown = 60;
        return;
    } else {
        $("#codeButton").attr("disabled", "true");
        $("#codeButton").html("重新发送(" + RegLogin.regCountDown + ")");
        RegLogin.regCountDown--;
    }
    setTimeout(function() {
        setTime();
    },1000)
}

//忘记密码-验证码倒计时
function forgetCodes(){
    var phone = $("#forgetPhone").val();
    //验证码
    var regCode = $("#forgetCode").val();
    //服务器验证码
    var passCode = $.cookie("passCode");
    if(!isPhone(phone)){
        $("#forgetPhoneError").html("手机格式不正确");
    }else if(RegLogin.isReg == 0){
        $("#forgetPhoneError").html("此号码未注册");
    }else if(regCode != passCode){
        $("#forgetCodeError").html("验证码错误");
    }else{
        //发送验证码
        $.ajax({
            type:"post",
            url: "/sendSms",
            data: {telePhone:phone,codeType:1},
            dataType: "json",
            success: function(result){
                if(parseInt(result.code) == 0){
                    setTimes();
                }else{
                    $("#forgetPhoneCodeError").html(result.msg);
                }
            }
        });
    }
}

//倒计时
function setTimes(){
    if (RegLogin.findCountDown == 0) {
        $("#forgetCodeButton").removeAttr("disabled");
        $("#forgetCodeButton").html("获取验证码");
        RegLogin.findCountDown = 60;
        return;
    } else {
        $("#forgetCodeButton").attr("disabled", "true");
        $("#forgetCodeButton").html("重新发送(" + RegLogin.findCountDown + ")");
        RegLogin.findCountDown--;
    }
    setTimeout(function() {
        setTimes();
    },1000)
}

