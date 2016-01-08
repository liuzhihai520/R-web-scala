/**
 * 方法描述:描述
 *  hh
 */
var regCountDown=60;
var regCountDown2=60;
var marker;
$(function(){
    $("#FAQ_tab a").click(function () {
        var index = $("#FAQ_tab a").index(this);
        $(this).addClass("active").siblings().removeClass("active");
        $(".w980").eq(index).show().siblings(".w980").hide();
    });

    var lat = $("#lat").val();
    var lng = $("#lng").val();

    // 初始化百度地图
    var map = new BMap.Map("dituContent");

    //初始化地图中心点
    var point = new BMap.Point(lng, lat);

    // 添加带有定位的导航控件
    var navigationControl = new BMap.NavigationControl({
        // 靠左上角位置
        anchor: BMAP_ANCHOR_TOP_LEFT,
        // LARGE类型
        type: BMAP_NAVIGATION_CONTROL_LARGE,
        // 启用显示定位
        enableGeolocation: true
    });
    map.addControl(navigationControl);

    //初始化地图标记
    marker = new BMap.Marker(point);
    //设置中心点坐标和地图级别
    map.centerAndZoom(point, 15);
    //将标记添加到地图中
    map.addOverlay(marker);
    //获取坐标
    var gc = new BMap.Geocoder();
    //添加标记点击监听
    marker.addEventListener("click", function(e){
        gc.getLocation(e.point, function(rs){
            showLocationInfo(e.point, rs);
        });
    });

    //添加地图点击监听
    map.addEventListener("click",function(e){
        map.clearOverlays();
        marker = new BMap.Marker(e.point);
        map.addOverlay(marker);
        map.panTo(e.point);
        gc.getLocation(e.point, function(rs){
            showLocationInfo(e.point, rs);
        });
    });

    var point = $("#trunId").val();
    if(point != 0){
        if(point == 1){

        }else if(point == 2){
            $("html,body").stop(true);$("html,body").animate({scrollTop: $("#howInvest").offset().top}, 1000);
        }else if(point == 3){
            $("html,body").stop(true);$("html,body").animate({scrollTop: $("#howPublish").offset().top}, 1000);
        }else if(point == 4){
            $("html,body").stop(true);$("html,body").animate({scrollTop: $("#howPay").offset().top}, 1000);
        }
    }

    $('#investBtn').zclip({
        path: "/assets/javascripts/js/zclip/ZeroClipboard.swf",
        copy: function(){
            return $('#webURL').val();
        },
        afterCopy:function(e){
            $("#LinkBox").show();
        },
        beforeCopy:function(){
        }
    });

    var isreg=$("#isreg").val();
    if("false"==isreg){
        $('.enter_btn').text("取消报名");
    }
    $('.weixin').click(function () {
        $('.layer.weixin').show();
    })
    //登录
    $("#login").click(function(){
        var loginPhone = $("#loginPhone").val();
        var loginPassword = $("#loginPassword").val();
        if (!isPhone(loginPhone)) {
            $("#loginPhone").focus();
            $("#loginPhoneError").html("手机号码格式不正确");
        } else if (!checkPassword(loginPassword)) {
            $("#loginPassword").focus();
            $("#loginPasswordError").html("密码格式为6-16位字符");
        } else {
            $.ajax({
                type: "post",
                url: "/ajaxLogin",
                data: {phone:loginPhone,password:loginPassword},
                dataType: "json",
                success: function (result) {
                    if(parseInt(result.code) == 1){
                        $("#loginPhoneError").html("手机号码格式不正确");
                    }else if(parseInt(result.code) == 2){
                        $("#loginPasswordError").html("密码格式为6-16位字符");
                    }else if(parseInt(result.code) == 3){
                        $("#loginPhoneError").html("手机号或密码错误");
                    }else{
                        window.location.href = window.location.href;
                    }
                }
            });
        }
    });

    $('.close_btn').click(function () {
        $('.layer').hide();
    })

    //报名
    $("#registrationbtn").click(function(){
        registration();
    });

    $('.enter_btn').click(function () {
        var isLogin = $("#isLogin").val();
        if (isLogin == "false") {
            $('.layer.enter_login').show();
        } else {
            var activiyisauth=$("#activiyisauth").val();
            var isauth=false;
            if('1'== activiyisauth){
                var userstatus=$("#userstatus").val();
                if('2'==userstatus){
                    isauth=true;
                }else{
                    $('.layer.realNameExamine').show();
                    return false;
                }
            }else{
                isauth=true;
            }
            if(isauth) {
                showRegOrOutDiv();
            }
        }
    });

    //$('.enter_btn').click(function () {
    //    if ($(this).text() == "立即报名") {
    //        $(this).text("取消报名")
    //        $('.layer.enter').show();
    //        setTimeout(function () {
    //            $('body').one('click', function () {
    //                $('.layer.enter').hide();
    //            })
    //        })
    //    }
    //    else {
    //        $(this).text("立即报名");
    //        $('.layer.cancel_enter').show();
    //        setTimeout(function () {
    //            $('body').one('click', function () {
    //                $('.layer.cancel_enter').hide();
    //            })
    //        })
    //    }
    //});
});

//验证码倒计时
function sendCode(){
    //手机号
    var tel = $("#tel").val();
    $.ajax({
        type:"post",
        url: "/sendSms",
        data: {telePhone:tel,codeType:4},
        dataType: "json",
        success: function(result){
            if(parseInt(result.code) == 0){
                setTime();
            }else{
                $("#codeError").html(result.msg);
            }
        }
    });
}

//验证码倒计时
function sendCode2(){
    //手机号
    var tel = $("#usertel").val();
    $.ajax({
        type:"post",
        url: "/sendSms",
        data: {telePhone:tel,codeType:5},
        dataType: "json",
        success: function(result){
            if(parseInt(result.code) == 0){
                setTime2();
            }else{
                $("#outcodeError").html(result.msg);
            }
        }
    });
}

//找回密码倒计时
function setTime2(){
    if (regCountDown2 == 0) {
        $("#codeButton2").attr("disabled", "false");
        $("#codeButton2").html("免费获取验证码");
        regCountDown2 = 60;
        return;
    } else {
        $("#codeButton2").attr("disabled", "true");
        $("#codeButton2").html("重新发送(" + regCountDown2 + ")");
        regCountDown2--;
    }
    setTimeout(function() {
        setTime();
    },1000)
}

//找回密码倒计时
function setTime(){
    if (regCountDown == 0) {
        $("#codeButton").attr("disabled", "false");
        $("#codeButton").html("免费获取验证码");
        regCountDown = 60;
        return;
    } else {
        $("#codeButton").attr("disabled", "true");
        $("#codeButton").html("重新发送(" + regCountDown + ")");
        regCountDown--;
    }
    setTimeout(function() {
        setTime();
    },1000)
}

function showRegOrOutDiv(){

    if ($('.enter_btn').text() == "立即报名") {
        $('.layer.enter').show();
    }
    else {
        $('.layer.cancel_enter').show();
    }
}

//报名
function registration(){
    var userid=$("#activityUserId").val();
    var activityId=$("#activityId").val()
    var name=$("#activityname").val();
    var email=$("#email").val();
    var pcname=$("#pcname").val();
    var code=$("#code").val();
    var job=$("#job").val();
    var sex = $('input:radio[name="sex"]:checked').val();
    if(0==name.length){
        $("#nameError").html("请输入报名人员姓名");
    }else if(!checkEmail(email)){
        $("#emailError").html("请输入正确的电子邮箱");
    }else if(0==pcname.length){
        $("#pcnameError").html("请输入公司名称");
    }else if(4>code.length){
        $("#codeError").html("请输入正确的验证码");
    }else{
        $.ajax({
            type: "post",
            url: "/regActivity",
            data: {activityId:activityId,userid:userid,name:name,email:email,pcname:pcname,code:code,job:job,sex:sex},
            dataType: "json",
            success: function (result) {
                if(parseInt(result.code) == 1){
                    $("#nameError").html("请输入报名人员姓名!");
                }else if(parseInt(result.code) == 2){
                    $("#emailError").html("请输入正确的电子邮箱!");
                }else if(parseInt(result.code) == 3){
                    $("#pcnameError").html("请输入公司名称!");
                }else if(parseInt(result.code) == 4){
                    $("#codeError").html("请输入正确的验证码!");
                }else if(parseInt(result.code) == 5){
                    $("#nameError").html("账号不存在!");
                }else if(parseInt(result.code) == 6){
                    alert("已经报名不能重复报名!");
                }else{
                    alert("报名成功!");
                    $('.enter_btn').text("取消报名");
                    $('.layer.enter').hide();
                }
            }
        });
    }
}
/**
 * 取消报名
 */
function registrationout(){
    var usertel=$("#usertel").val();
    var outcode=$("#outcode").val();
    var userid=$("#activityUserId").val();
    var activityId=$("#activityId").val()
    if(!checkMoblie(usertel)){
        $("#usertelError").html("请输入正确的手机号码");
    }else if(4>outcode.length){
        $("#outcodeError").html("请输入正确的验证码");
    }else{
        $.ajax({
            type: "post",
            url: "/regOutActivity",
            data: {activityId:activityId,userid:userid,usertel:usertel,outcode:outcode},
            dataType: "json",
            success: function (result) {
                if(parseInt(result.code) == 1){
                    $("#nameError").html("请输入正确的手机号!");
                }else if(parseInt(result.code) == 2){
                    $("#nameError").html("账号不存在!");
                }else if(parseInt(result.code) == 3){
                    $("#outcodeError").html("验证码不正确!");
                }else{
                    alert("取消报名成功!");
                    $('.enter_btn').text("立即报名");
                    $('.layer.cancel_enter').hide();
                }
            }
        });
    }
}


//显示地址信息窗口
function showLocationInfo(pt, rs){
    var opts = {
        width : 250,     //信息窗口宽度
        height: 100,     //信息窗口高度
        title : ""  //信息窗口标题
    }

    var addComp = rs.addressComponents;
    var addr = "当前位置：" + addComp.province + "-" + addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber + "<br />";
    addr += "纬度: " + pt.lat + ", " + "经度：" + pt.lng;

    var address = addComp.province + "-" + addComp.city + "-" + addComp.district + "-" + addComp.street + "-" + addComp.streetNumber;

    var infoWindow = new BMap.InfoWindow(addr, opts);  //创建信息窗口对象
    marker.openInfoWindow(infoWindow,pt);

    //设置地址信息
    $("#address").val(address);
    $("#lat").val(pt.lat);
    $("#lng").val(pt.lng);
}
