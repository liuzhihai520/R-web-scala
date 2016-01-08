/**
 * 方法描述:描述
 *  hh
 */
var regCountDown=60;
var regCountDown2=60;
$(function(){
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

    //创建和初始化地图函数：
    function initMap() {
        createMap();//创建地图
        setMapEvent();//设置地图事件
        addMapControl();//向地图添加控件
        addMarker();//向地图中添加marker
    }

    //创建地图函数：
    function createMap() {
        var lat=$("#lat").val();
        var lng=$("#lng").val();
        var map = new BMap.Map("dituContent");//在百度地图容器中创建一个地图
        var point = new BMap.Point(lng,lat);//定义一个中心点坐标
        map.centerAndZoom(point, 17);//设定地图的中心点和坐标并将地图显示在地图容器中
        window.map = map;//将map变量存储在全局
    }

    //地图事件设置函数：
    function setMapEvent() {
        map.enableDragging();//启用地图拖拽事件，默认启用(可不写)
        map.enableScrollWheelZoom();//启用地图滚轮放大缩小
        map.enableDoubleClickZoom();//启用鼠标双击放大，默认启用(可不写)
        map.enableKeyboard();//启用键盘上下左右键移动地图
    }

    //地图控件添加函数：
    function addMapControl() {
        //向地图中添加缩放控件
        var ctrl_nav = new BMap.NavigationControl({ anchor: BMAP_ANCHOR_TOP_LEFT, type: BMAP_NAVIGATION_CONTROL_LARGE });
        map.addControl(ctrl_nav);
        //向地图中添加缩略图控件
        var ctrl_ove = new BMap.OverviewMapControl({ anchor: BMAP_ANCHOR_BOTTOM_RIGHT, isOpen: 0 });
        map.addControl(ctrl_ove);
        //向地图中添加比例尺控件
        var ctrl_sca = new BMap.ScaleControl({ anchor: BMAP_ANCHOR_BOTTOM_LEFT });
        map.addControl(ctrl_sca);
    }

    //标注点数组
    var markerArr = [{ title: "我的标记", content: "我的备注", point: "121.505838|31.245963", isOpen: 0, icon: { w: 21, h: 21, l: 0, t: 0, x: 6, lb: 5 } }
    ];
    //创建marker
    function addMarker() {
        for (var i = 0; i < markerArr.length; i++) {
            var json = markerArr[i];
            var p0 = json.point.split("|")[0];
            var p1 = json.point.split("|")[1];
            var point = new BMap.Point(p0, p1);
            var iconImg = createIcon(json.icon);
            var marker = new BMap.Marker(point, { icon: iconImg });
            var iw = createInfoWindow(i);
            var label = new BMap.Label(json.title, { "offset": new BMap.Size(json.icon.lb - json.icon.x + 10, -20) });
            marker.setLabel(label);
            map.addOverlay(marker);
            label.setStyle({
                borderColor: "#808080",
                color: "#333",
                cursor: "pointer"
            });

            (function () {
                var index = i;
                var _iw = createInfoWindow(i);
                var _marker = marker;
                _marker.addEventListener("click", function () {
                    this.openInfoWindow(_iw);
                });
                _iw.addEventListener("open", function () {
                    _marker.getLabel().hide();
                })
                _iw.addEventListener("close", function () {
                    _marker.getLabel().show();
                })
                label.addEventListener("click", function () {
                    _marker.openInfoWindow(_iw);
                })
                if (!!json.isOpen) {
                    label.hide();
                    _marker.openInfoWindow(_iw);
                }
            })()
        }
    }
    //创建InfoWindow
    function createInfoWindow(i) {
        var json = markerArr[i];
        var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='" + json.title + "'>" + json.title + "</b><div class='iw_poi_content'>" + json.content + "</div>");
        return iw;
    }
    //创建一个Icon
    function createIcon(json) {
        var icon = new BMap.Icon("http://app.baidu.com/map/images/us_mk_icon.png", new BMap.Size(json.w, json.h), { imageOffset: new BMap.Size(-json.l, -json.t), infoWindowOffset: new BMap.Size(json.lb + 5, 1), offset: new BMap.Size(json.x, json.h) })
        return icon;
    }

    initMap();//创建和初始化地图

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
        data: {telePhone:tel},
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
        data: {telePhone:tel},
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
