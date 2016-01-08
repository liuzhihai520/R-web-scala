/**
 * 方法描述:修改密码
 *
 * author 小刘
 * version v1.0
 * date 2015/12/1
 */
 var obj = {
       isReg:0,
       countdown:60,
       //验证码
       captcha:function(){
           $("#captcha").attr("src","/getCaptcha?v="+Math.random());
       },
       sendSms:function(){
           var phone = $("#tel").val();
           $.ajax({
               type:"post",
               url: "/sendSms",
               data: {telePhone:phone,codeType:1},
               dataType: "json",
               success: function(result){
                   if(parseInt(result.code) == 0){
                       setTime();
                   }else{
                       $("#sortCodeError").html(result.msg);
                   }
               }
           });
       }
 };

 $(function(){

    //刷新验证码
    $("#reflush").click(function(){
        obj.captcha();
    });

    //验证表单
    $("#sub").click(function(){
        var tel = $("#tel").val();
        var oldPass = $("#oldPass").val();
        var password = $("#password").val();
        var sortCode = $("#sortCode").val();
        var code = $("#code").val();
        if(oldPass.length < 6 || oldPass.length > 15){
            $("#passwordError").html("密码应为6-12个字符");
        }else if(password.length < 6 || password.length > 15){
            $("#passwordError").html("密码应为6-12个字符");
        }else if(sortCode.length != 4){
            $("#sortCodeError").html("短信验证码格式错误");
        }else if(code.length != 4){
            $("#codeError").html("图形验证码错误");
        }else{
            $("#form").submit();
        }
    });

    //焦点控制
    $("#tel").bind("blur focus",function(){
        $("#telError").html("");
    });
    $("#password").bind("blur focus",function(){
        $("#passwordError").html("");
    });
    $("#sortCode").bind("blur focus",function(){
        $("#sortCodeError").html("");
    });
    $("#code").bind("blur focus",function(){
        $("#codeError").html("");
    });

     $('#password').hideShowPassword({
         innerToggle: true,
         touchSupport: Modernizr.touch
     });
 });

//注册验证码倒计时
function sendCode(){
    //手机号码
    var phone = $("#tel").val();
    //输入验证码
    var code = $("#code").val();
    //服务器验证码
    var passCode = $.cookie("passCode");
    if(!isPhone(phone)){
        $("#telError").html("手机格式不正确");
    }else if(code != passCode){
        $("#codeError").html("图形验证码错误");
    }else{
        $.ajax({
            type:"GET",
            url: "/isReg",
            data: {tel:phone},
            dataType: "json",
            success: function(result){
                //判断是否注册
                if(parseInt(result.code) > 0){
                    obj.isReg = 1;
                    $("#telError").html("");
                    obj.sendSms();
                }else{
                    obj.isReg = 0;
                    $("#telError").html("手机号码未注册");
                }
            }
        });
    }
}

//找回密码倒计时
function setTime(){
    if (obj.countdown == 0) {
        $("#sendCodeBtn").attr("disabled", "false");
        $("#sendCodeBtn").html("免费获取验证码");
        obj.countdown = 60;
        return;
    } else {
        $("#sendCodeBtn").attr("disabled", "true");
        $("#sendCodeBtn").html("重新发送(" + obj.countdown + ")");
        obj.countdown--;
    }
    setTimeout(function() {
        setTime();
    },1000)
}
