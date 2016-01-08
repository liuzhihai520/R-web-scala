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
    //报名倒计时
    signCountDown:60,
    //取消报名倒计时
    cancelCountDown:60,
    //是否注册
    isReg:0,
    //验证码
    flushCode: function () {
        $("#captcha").attr("src","/getCaptcha?v="+Math.random()+"");
    },
    logCaptcha:function(){
        $("#logCaptcha").attr("src","getCaptcha?v="+Math.random());
    },
    //登录
    login: function () {
        var username = $("#username").val();
        var loginPassword = $("#loginPassword").val();
        var code = $("#logCode").val();
        if (!isUsername(username)) {
            $("#username").focus();
            $("#usernameError").html("用户名长度为2-18位");
        } else if (!checkPassword(loginPassword)) {
            $("#loginPassword").focus();
            $("#loginPasswordError").html("密码格式为6-16位字符");
        } else {
            $.ajax({
                type: "post",
                url: "/ajaxLogin",
                data: {username:username,password:loginPassword,code:code},
                dataType: "json",
                success: function (result) {
                    if(parseInt(result.code) == 1 || parseInt(result.code) == 3){
                        $("#username").focus();
                        $("#usernameError").html("用户名/密码错误");
                    }else if(parseInt(result.code) == 2){
                        $("#loginPassword").focus();
                        $("#loginPasswordError").html("密码错误");
                    }else if(parseInt(result.code) == 4){
                        $("#username").focus();
                        $("#usernameError").html("该账号已被锁定,请联系客服[021-20226053]");
                    }else if(parseInt(result.code) == 5){
                        $("#username").focus();
                        $("#codebox").show();
                        $("#logCodeError").html("图形验证码错误");
                    }else{
                        window.location.href = window.location.href;
                    }
                }
            });
        }
    },
    //判断手机号是否注册
    phoneReg:function(phone){
        $.ajax({
            type:"get",
            url: "/isReg",
            data: {tel:phone},
            dataType: "json",
            success: function(result){
                if(parseInt(result.code) > 0){
                    //已注册
                    RegLogin.isReg = 1;
                    $("#telError").html("此号码已注册");
                }else{
                    //未注册
                    RegLogin.isReg = 0;
                    $("#telError").html("");
                }
            }
        });
    },
    //注册验证/提交
    reg:function(){
        var account = $("#account").val();
        var tel = $("#tel").val();
        var password = $("#password").val();
        var sortCode = $("#sortCode").val();
        var code = $("#code").val();
        if(!isUsername(account)){
            $("#account").focus();
            $("#accountError").html("英文或数字2-18位字符");
        }else if(!isPhone(tel)){
            $("#tel").focus();
            $("#telError").html("手机格式不正确");
        }else if(!checkPassword(password)){
            $("#password").focus();
            $("#passwordError").html("密码格式为6-16位字符");
        }else if(!isPhoneCode(sortCode)){
            $("#sortCode").focus();
            $("#sortCodeError").html("手机验证码为4位数字");
        }else if(code.length != 4){
            $("#code").focus();
            $("#codeError").html("验证码为4位字符");
        }else if($("#agree").is(':checked') == false){
            $("#agreeError").html("请阅读服务条款");
        }else{
            $.ajax({
                type:"post",
                url: "/ajaxReg",
                data: {account:account,tel:tel,password:password,sortCode:sortCode,code:code},
                dataType: "json",
                success: function(result){
                    if(parseInt(result.code) == 1){
                        $("#account").focus();
                        $("#accountError").html("用户名长度为2-18位");
                    }else if(parseInt(result.code) == 2){
                        $("#tel").focus();
                        $("#telError").html("手机号码格式不正确");
                    }else if(parseInt(result.code) == 3){
                        $("#password").focus();
                        $("#passwordError").html("密码格式为6-16位字符");
                    }else if(parseInt(result.code) == 4){
                        $("#code").focus();
                        $("#codeError").html("验证码错误");
                    }else if(parseInt(result.code) == 5){
                        $("#sortCode").focus();
                        $("#sortCodeError").html("验证码错误");
                    }else if(parseInt(result.code) == 6){
                        $("#agreeError").html("注册失败");
                    }else{
                        window.location.href = window.location.href;
                    }
                }
            });
        }
    },
    //报名
    signUp:function(){
        var name = $("#name").val();
        var sex = $("#sex").val();
        var email = $("#email").val();
        var pcname = $("#pcname").val();
        var job = $("#job").val();
        var signCode = $("#signCode").val();
        var money = $("#money").val();
        var projectId = $("#projectId").val();

        if(name.length == 0){
            $("#name").focus();
            $("#nameError").html("请输入姓名");
        }else if(!checkEmail(email)){
            $("#email").focus();
            $("#emailError").html("请输入正确的邮箱格式");
        }else if(pcname.length == 0){
            $("#pcname").focus();
            $("#pcnameError").html("请输入公司名称");
        }else if(!isPhoneCode(signCode)){
            $("#pcnsignCodeame").focus();
            $("#signCodeError").html("手机验证码为4位数字");
        }else if(!isNumeric(money) || parseInt(money) <= 0){
            $("#money").focus();
            $("#moneyError").html("请输入整数投资金额");
        }else if(parseInt(projectId) <= 0){
            $("#money").focus();
            $("#moneyError").html("没有发现可报名的项目");
        }else {
            $.ajax({
                type:"post",
                url: "/invest",
                data: {name:name,sex:sex,email:email,job:job,signCode:signCode,money:money,pcname:pcname,projectId:projectId},
                dataType: "json",
                success: function(result){
                    if(parseInt(result.code) == 1){
                        $("#name").focus();
                        $("#nameError").html("请输入姓名");
                    }else if(parseInt(result.code) == 2){
                        $("#sex").focus();
                        $("#sexError").html("请选择性别");
                    }else if(parseInt(result.code) == 3){
                        $("#email").focus();
                        $("#emailError").html("请填写邮箱");
                    }else if(parseInt(result.code) == 4){
                        $("#pcname").focus();
                        $("#pcnameError").html("请填写公司名称");
                    }else if(parseInt(result.code) == 5){
                        $("#job").focus();
                        $("#jobError").html("请选择职位");
                    }else if(parseInt(result.code) == 6){
                        $("#signCode").focus();
                        $("#signCodeError").html("验证码错误");
                    }else if(parseInt(result.code) == 7){
                        $("#money").focus();
                        $("#moneyError").html("请输入整数报名金额");
                    }else if(parseInt(result.code) == 8){
                        $("#money").focus();
                        $("#moneyError").html("没有发现可报名的项目");
                    }else if(parseInt(result.code) == 9){
                        $("#money").focus();
                        $("#moneyError").html("投资金额小于起投金额");
                    }else if(parseInt(result.code) == 10){
                        $("#money").focus();
                        $("#moneyError").html("报名失败");
                    }else if(parseInt(result.code) == 11){
                        $("#money").focus();
                        $("#moneyError").html("报名时间已结束");
                    }else if(parseInt(result.code) == 12){
                        $("#money").focus();
                        $("#moneyError").html("您已经参加过报名,不可重复报名");
                    }else{
                        window.location.href = window.location.href;
                    }
                }
            });
        }
    },
    cancelSignUp:function(){
        var cacelCode = $("#cancelCode").val();
        var projectId = $("#projectId").val();
        if(!isPhoneCode(cacelCode)){
            $("#cancelCode").focus();
            $("#cancelCodeError").html("手机验证码为4位数字");
        }else if(parseInt(projectId) <= 0){
            $("#cancelCodeError").html("没有发现可取消的项目");
        }else{
            $.ajax({
                type:"post",
                url: "/cancelSignUp",
                data: {cacelCode:cacelCode,projectId:projectId},
                dataType: "json",
                success: function(result){
                    if(parseInt(result.code) == 1){
                        $("#cancelCodeError").html("没有发现可取消的项目");
                    }else if(parseInt(result.code) == 2){
                        $("#cancelCodeError").html("短信验证码错误");
                    }else if(parseInt(result.code) == 3){
                        $("#cancelCodeError").html("领投人不能取消报名");
                    }else{
                        window.location.href = window.location.href;
                    }
                }
            });
        }
    }
}

$(function(){
    $('#investBtn').zclip({
        path: "/assets/javascripts/js/zclip/ZeroClipboard.swf",
        copy: function(){
            return $('#webURL').val();
        },
        afterCopy:function(){
            $('.layer.invitation').show();
        }
    });

    //刷新验证码
    $("#changeCode").click(function(){
        RegLogin.flushCode();
    });

    $("#logReflush").click(function(){
        RegLogin.logCaptcha();
    });

    //登录
    $("#LoginSub").click(function(){
        RegLogin.login();
    });

    $("#leaderAvatar").error(function() {
        $(this).attr("src", "/assets/images/head_default.jpg");
    });

    //焦点控制
    $("#username").bind("focus blur",function(){
        $("#usernameError").html("");
    });
    $("#loginPassword").bind("focus blur",function(){
        $("#loginPasswordError").html("");
    });
    $("#code").bind("focus blur",function(){
        $("#codeError").html("");
    });
    $("#name").bind("focus blur",function(){
        $("#nameError").html("");
    });
    $("#email").bind("focus blur",function(){
        $("#emailError").html("");
    });
    $("#money").bind("focus blur",function(){
        $("#moneyError").html("");
    });
    $("#signCode").bind("focus blur",function(){
        $("#signCodeError").html("");
    });
    $("#pcname").bind("focus blur",function(){
        $("#pcnameError").html("");
    });
    $("#password").bind("focus blur",function(){
        $("#passwordError").html("");
    });
    $("#sortCode").bind("focus blur",function(){
        $("#sortCodeError").html("");
    });



    /******************注册*********************/
        //失去焦点提示手机是否注册
    $("#tel").blur(function(){
        var phone = $("#tel").val();
        if(isPhone(phone)){
            RegLogin.phoneReg(phone);
        }else{
            $("#tel").focus();
            $("#telError").html("手机格式不正确");
        }
    });

    //注册
    $("#regBtn").click(function(){
        RegLogin.reg();
    });

    //绑定报名/取消报名时间
    $("#gaveUp").click(function(){
        var ismain=$("#ismain").val();
        if(0<Number(ismain)){
            $('.layer.invitation2').show();
        }else{
             $("#gaveUpBox").show();
        }
    });

    //报名
    $("#investSub").click(function(){
        RegLogin.signUp();
    });

    //取消报名
    $("#cancelSub").click(function(){
        RegLogin.cancelSignUp();
    });

    //是否认证
    $("#signUp").click(function(){
        $.ajax({
            type:"post",
            url: "/isRealAuth",
            data: {},
            dataType: "json",
            success: function(result){
                if(parseInt(result.status) == 2){
                    window.location.href="/isMainInfo/"+$("#projectId").val();
                }else{
                    $(".realNameExamine").show();
                }
            }
        });
    });

    //不能下载商业计划书
    $("#downLoad").click(function(){
        $("#planBox").show();
    });
    $("#planSure").click(function(){
        $("#planBox").hide();
    });


    $('#password').hideShowPassword({
        innerToggle: true,
        touchSupport: Modernizr.touch
    });

    $("#jobs").change(function(){
        $("#job").val($("#jobs").val());
    });
});

//注册倒计时
function sendCode(){
    //手机号
    var tel = $("#tel").val();
    //验证码
    var code = $("#code").val();
    //服务器验证码
    var passCode = $.cookie("passCode");
    //判断验证码
    if(!isPhone(tel)){
        $("#telError").html("手机格式不正确");
    }else if(RegLogin.isReg > 0){
        $("#telError").html("此号码已注册");
    }else if(code != passCode){
        $("#codeError").html("图形验证码错误");
    }else{
        $.ajax({
            type:"post",
            url: "/sendSms",
            data: {telePhone:tel},
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
    if (RegLogin.countDown == 0) {
        $("#codeButton").removeAttr("disabled");
        $("#codeButton").html("免费获取验证码");
        RegLogin.countDown = 60;
        return;
    } else {
        $("#codeButton").attr("disabled", "true");
        $("#codeButton").html("重新发送(" + RegLogin.countDown + ")");
        RegLogin.countDown--;
    }
    setTimeout(function() {
        setTime();
    },1000)
}


//报名倒计时
function signSendCode(){
    //手机号
    var tel = $("#phoneNum").val();
    $.ajax({
        type:"post",
        url: "/sendSms",
        data: {telePhone:tel},
        dataType: "json",
        success: function(result){
            if(parseInt(result.code) == 0){
                setSignTime();
            }else{
                $("#signCodeError").html(result.msg);
            }
        }
    });
}

//报名倒计时
function setSignTime(){
    if (RegLogin.signCountDown == 0) {
        $("#sendCodeBtn").removeAttr("disabled");
        $("#sendCodeBtn").html("免费获取验证码");
        RegLogin.signCountDown = 60;
        return;
    } else {
        $("#sendCodeBtn").attr("disabled", "true");
        $("#sendCodeBtn").html("重新发送(" + RegLogin.signCountDown + ")");
        RegLogin.signCountDown--;
    }
    setTimeout(function() {
        setSignTime();
    },1000)
}

//取消报名倒计时
function cacelSendCode(){
    //手机号
    var tel = $("#phoneNum").val();
    $.ajax({
        type:"post",
        url: "/sendSms",
        data: {telePhone:tel,codeType:3},
        dataType: "json",
        success: function(result){
            if(parseInt(result.code) == 0){
                setCancelTime();
            }else{
                $("#cancelCodeError").html(result.msg);
            }
        }
    });
}

//找回密码倒计时
function setCancelTime(){
    if (RegLogin.cancelCountDown == 0) {
        $("#cancelCodeBtn").removeAttr("disabled");
        $("#cancelCodeBtn").html("免费获取验证码");
        RegLogin.cancelCountDown = 60;
        return;
    } else {
        $("#cancelCodeBtn").attr("disabled", "true");
        $("#cancelCodeBtn").html("重新发送(" + RegLogin.cancelCountDown + ")");
        RegLogin.cancelCountDown--;
    }
    setTimeout(function() {
        setCancelTime();
    },1000)
}
