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
    //领头/跟投
    isInvest:0,
    //验证码
    flushCode: function () {
        $("#captcha").attr("src","/getCaptcha?v="+Math.random()+"");
    },
    //报名
    signUp:function(){
        cleanAlert();
        var itemfinancingsum=Number($("#itemfinancingsum").val())/10;
        var name = $("#name").val();
        var projectName = $("#projectName").val();
        var sex = $("#sex").val();
        var email = $("#email").val();
        var pcname = $("#pcname").val();
        var educationid = $("#educationid").val();
        var pccapital = $("#pccapital").val();
        var pctel = $("#pctel").val();
        var job = $("#jobs").val();
        var yearlysalary = $("#yearlysalary").val();
        var stranger = $("#stranger").val();
        var worker = $("#worker").val();
        var signCode = $("#signCode").val();
        var money = $("#money").val();
        var projectId = $("#projectId").val();
        if(parseInt(educationid) == 0){
            $("#educationid").focus();
            $("#educationidError").html("请选择学历");
        }else if(!checkEmail(email)){
            $("#email").focus();
            $("#emailError").html("请输入正确的邮箱格式");
        }else if(pccapital.length == 0){
            $("#pccapital").focus();
            $("#pccapitalError").html("请输入公司资产");
        }else if(Number(pccapital) == 0){
            $("#pccapital").focus();
            $("#pccapitalError").html("公司资产必须大于0");
        }else if(!isPhone(pctel)){
            $("#pctel").focus();
            $("#pctelError").html("请输入有效的公司电话");
        }else if(pcname.length == 0){
            $("#pcname").focus();
            $("#pcnameError").html("请输入公司名称");
        }else if(yearlysalary.length == 0){
            $("#yearlysalary").focus();
            $("#yearlysalaryError").html("请输入年收入");
        }else if(isNaN(yearlysalary)){
            $("#yearlysalary").focus();
            $("#yearlysalaryError").html("年收入只能输入数字");
        }else if(parseInt(job) == 0){
            $("#jobs").focus();
            $("#jobsError").html("请选择职位");
        }else if(stranger.length == 0){
            $("#stranger").focus();
            $("#strangerError").html("请输入过往投资过的项目/资金");
        }else if(worker.length == 0){
            $("#worker").focus();
            $("#workerError").html("请输入工作经验");
        }else if(money.length == 0){
            $("#money").focus();
            $("#moneyError").html("请输入投资金额");
        }else if(isNaN(money)){
            $("#money").focus();
            $("#moneyError").html("投资金额只能输入数字");
        }else if(itemfinancingsum>Number(money)){
            $("#money").focus();
            $("#moneyError").html("领投人投资金额必须大于计划融资的10%");
        }else if(!isPhoneCode(signCode)){
            $("#pcnsignCodeame").focus();
            $("#signCodeError").html("手机验证码为4位数字");
        }else if(parseInt(projectId) <= 0){
            $("#money").focus();
            $("#moneyError").html("没有发现可报名的项目");
        }else {
            $.ajax({
                type:"post",
                url: "/investMain1",
                data: {educationid:educationid,pccapital:pccapital,pctel:pctel,name:name,sex:sex,email:email,
                       job:job,signCode:signCode,money:money,pcname:pcname,projectId:projectId,yearlysalary:yearlysalary,stranger:stranger,worker:worker,projectName:projectName
                },
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
                        $("#moneyError").html("领投人投资金额必须大于计划融资的10%");
                    }else if(parseInt(result.code) == 10){
                        $("#yearlysalary").focus();
                        $("#yearlysalaryError").html("请输入数字年薪金额");
                    }else if(parseInt(result.code) == 11){
                        $("#stranger").focus();
                        $("#strangerError").html("请输入过往投资过的项目/资金");
                    }else if(parseInt(result.code) == 12){
                        $("#worker").focus();
                        $("#workerError").html("请输入工作经验");
                    }else if(parseInt(result.code) == 13){
                        $("#money").focus();
                        $("#moneyError").html("报名失败");
                    }else if(parseInt(result.code) == 14){
                        $("#money").focus();
                        $("#moneyError").html("报名时间已结束");
                    }else if(parseInt(result.code) == 15){
                        $("#money").focus();
                        $("#moneyError").html("您已经参加过报名,不可重复报名");
                    }else if(parseInt(result.code) == 17){
                        $("#pccapital").focus();
                        $("#pccapitalError").html("公司资产必须大于0");
                    }else if(parseInt(result.code) == 17){
                        $("#pctel").focus();
                        $("#pctelError").html("请输入公司电话");
                    }else if(parseInt(result.code) == 16){
                        alert("请登录后再报名");
                        //window.location.href ="http://www.ruijiutou.com/regLogin";
                        window.location.href ="/regLogin";
                    }else{
                       // window.location.href ="http://www.ruijiutou.com/item/"+projectId;
                        window.location.href ="/item/"+projectId;
                    }
                }
            });
        }
    },
    //报名
    signUp2:function(){
        var projectName = $("#projectName").val();
        var startnum = $("#startnum").val();
        var name = $("#name2").val();
        var sex = $("#sex2").val();
        var email = $("#email2").val();
        var pcname = $("#pcname2").val();
        var job = $("#jobs2").val();
        var money = $("#money2").val();
        var projectId = $("#projectId").val();
        var signCode2=$("#signCode2").val();
        if(!checkEmail(email)){
            $("#email2").focus();
            $("#email2Error").html("请输入正确的邮箱格式");
        }else if(pcname.length == 0){
            $("#pcname2").focus();
            $("#pcname2Error").html("请输入公司名称");
        }else if(money.length == 0){
            $("#money2").focus();
            $("#money2Error").html("请输入投资金额");
        }else if(isNaN(money)){
            $("#money2").focus();
            $("#money2Error").html("投资金额只能输入数字");
        }else if(startnum>Number(money)){
            $("#money2").focus();
            $("#money2Error").html("投资金额必须大于起投金额");
        }else if(!isPhoneCode(signCode2)){
            $("#signCode2").focus();
            $("#signCode2Error").html("手机验证码为4位数字");
        }else if(parseInt(projectId) <= 0){
            $("#money2").focus();
            $("#money2Error").html("没有发现可报名的项目");
        }else {
            $.ajax({
                type:"post",
                url: "/invest",
                data: {name:name,sex:sex,email:email,job:job,signCode:signCode2,money:money,pcname:pcname,projectId:projectId,projectName:projectName},
                dataType: "json",
                success: function(result){
                    if(parseInt(result.code) == 1){
                        $("#name").focus();
                        $("#name2Error").html("请输入姓名");
                    }else if(parseInt(result.code) == 2){
                        $("#sex").focus();
                        $("#sex2Error").html("请选择性别");
                    }else if(parseInt(result.code) == 3){
                        $("#email").focus();
                        $("#email2Error").html("请填写邮箱");
                    }else if(parseInt(result.code) == 4){
                        $("#pcname").focus();
                        $("#pcname2Error").html("请填写公司名称");
                    }else if(parseInt(result.code) == 5){
                        $("#job").focus();
                        $("#job2Error").html("请选择职位");
                    }else if(parseInt(result.code) == 6){
                        $("#signCode").focus();
                        $("#signCode2Error").html("验证码错误");
                    }else if(parseInt(result.code) == 7){
                        $("#money").focus();
                        $("#money2Error").html("请输入整数报名金额");
                    }else if(parseInt(result.code) == 8){
                        $("#money").focus();
                        $("#money2Error").html("没有发现可报名的项目");
                    }else if(parseInt(result.code) == 9){
                        $("#money").focus();
                        $("#money2Error").html("投资金额不能大于起投金额");
                    }else if(parseInt(result.code) == 10){
                        $("#money").focus();
                        $("#money2Error").html("报名失败");
                    }else if(parseInt(result.code) == 11){
                        $("#money").focus();
                        $("#money2Error").html("报名时间已结束");
                    }else if(parseInt(result.code) == 12){
                        $("#money").focus();
                        $("#money2Error").html("您已经参加过报名,不可重复报名");
                    }else if(parseInt(result.code) == 13){
                        alert("请登录后再报名");
                        //window.location.href ="http://www.ruijiutou.com/regLogin";
                        window.location.href ="/regLogin";
                    }else{
                        alert("报名成功");
                       // window.location.href ="http://www.ruijiutou.com/item/"+projectId;
                        window.location.href ="/item/"+projectId;
                    }
                }
            });
        }
    }
}

$(function(){

    $("#user_nav .nav_item").click(function () {
        var index = $("#user_nav .nav_item").index(this);
        $(this).addClass("active").siblings().removeClass("active");
        $(".step_main").eq(index).show().siblings(".step_main").hide();
    });

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

    //焦点控制
    $("#loginPhone").bind("focus blur",function(){
        $("#loginPhoneError").html("");
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


    //绑定报名/取消报名时间
    $("#gaveUp").click(function(){
        $("#gaveUpBox").show();
    });

    //报名
    $("#investSub").click(function(){
        //领投
        RegLogin.isInvest = 0;
        var money = $("#money").val();
        var itemfinancingsum = $("#itemfinancingsum").val();
        var minMoney = Number($("#itemfinancingsum").val())/10;
        if(parseFloat(money) > 0 && parseFloat(itemfinancingsum) > 500 && minMoney < money){
            $("#investTips").show();
        }else{
            RegLogin.signUp();
        }
    });

    //报名
    $("#investSub2").click(function(){
        //跟投
        RegLogin.isInvest = 1;
        var money = $("#money2").val();
        var itemfinancingsum = $("#itemfinancingsum").val();
        var minMoney = Number($("#itemfinancingsum").val())/10;
        if(parseFloat(money) > 0 && parseFloat(itemfinancingsum) > 500 && minMoney < money){
            $("#investTips").show();
        }else{
            RegLogin.signUp2();
        }
    });

    //不正当竞争提示
    $("#investSure").click(function(){
        $("#investTips").hide();
        if(RegLogin.isInvest == 0){
            RegLogin.signUp();
        }else{
            RegLogin.signUp2();
        }
    });

    $('#password').hideShowPassword({
        innerToggle: true,
        touchSupport: Modernizr.touch
    });

    $("#jobs").change(function(){
        $("#job").val($("#jobs").val());
    });

    $("#jobs2").change(function(){
        $("#job2").val($("#jobs2").val());
    });
});

//清除提示
function cleanAlert(){
    $("#educationidError").html("");
    $("#jobError").html("");
    $("#pcnameError").html("");
    $("#emailError").html("");
    $("#yearlysalaryError").html("");
    $("#strangerError").html("");
    $("#workerError").html("");
    $("#moneyError").html("");
    $("#signCodeError").html("");
    //------------------------------------------------
    $("#pcname2Error").html("");
    $("#email2Error").html("");
    $("#money2Error").html("");
    $("#signCode2Error").html("");
}
//报名倒计时
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
        data: {telePhone:tel,codeType:2},
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
        $("#sendCodeBtn2").removeAttr("disabled");
        $("#sendCodeBtn2").html("免费获取验证码");
        RegLogin.signCountDown = 60;
        return;
    } else {
        $("#sendCodeBtn").attr("disabled", "true");
        $("#sendCodeBtn").html("重新发送(" + RegLogin.signCountDown + ")");
        $("#sendCodeBtn2").attr("disabled", "true");
        $("#sendCodeBtn2").html("重新发送(" + RegLogin.signCountDown + ")");
        RegLogin.signCountDown--;
    }
    setTimeout(function() {
        setSignTime();
    },1000)
}

//跟投倒计时
function cacelSendCode(){
    //手机号
    var tel = $("#phoneNum").val();
    $.ajax({
        type:"post",
        url: "/sendSms",
        data: {telePhone:tel},
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
