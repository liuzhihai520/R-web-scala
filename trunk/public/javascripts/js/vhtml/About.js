/**
 * 方法描述:关于我们
 *
 * author 小刘
 * version v1.0
 * date 2015/12/16
 */
$(function(){

    var point = $("#trun").val();
    if(point != 0){
        $("html,body").stop(true);$("html,body").animate({scrollTop: $("#about_us").offset().top}, 1000);
    }

    //创建和初始化地图函数：
    function initMap(){
        createMap();//创建地图
        setMapEvent();//设置地图事件
        addMapControl();//向地图添加控件
        addMarker();//向地图中添加marker
    }

    //创建地图函数：
    function createMap(){
        var map = new BMap.Map("dituContent");//在百度地图容器中创建一个地图
        var point = new BMap.Point(121.514799,31.240298);//定义一个中心点坐标
        map.centerAndZoom(point,17);//设定地图的中心点和坐标并将地图显示在地图容器中
        window.map = map;//将map变量存储在全局
    }

    //地图事件设置函数：
    function setMapEvent(){
        map.enableDragging();//启用地图拖拽事件，默认启用(可不写)
        map.enableScrollWheelZoom();//启用地图滚轮放大缩小
        map.enableDoubleClickZoom();//启用鼠标双击放大，默认启用(可不写)
        map.enableKeyboard();//启用键盘上下左右键移动地图
    }

    //地图控件添加函数：
    function addMapControl(){
    }

    //标注点数组
    var markerArr = [{ title: "上海睿就投互联网金融信息服务有限公司", content: "", point: "121.512535|31.241132", isOpen: 0, icon: { w: 21, h: 21, l: 0, t: 0, x: 6, lb: 5 } }
    ];
    //创建marker
    function addMarker(){
        for(var i=0;i<markerArr.length;i++){
            var json = markerArr[i];
            var p0 = json.point.split("|")[0];
            var p1 = json.point.split("|")[1];
            var point = new BMap.Point(p0,p1);
            var iconImg = createIcon(json.icon);
            var marker = new BMap.Marker(point,{icon:iconImg});
            var iw = createInfoWindow(i);
            var label = new BMap.Label(json.title,{"offset":new BMap.Size(json.icon.lb-json.icon.x+10,-20)});
            marker.setLabel(label);
            map.addOverlay(marker);
            label.setStyle({
                borderColor:"#808080",
                color:"#333",
                cursor:"pointer"
            });

            (function(){
                var index = i;
                var _iw = createInfoWindow(i);
                var _marker = marker;
                _marker.addEventListener("click",function(){
                    this.openInfoWindow(_iw);
                });
                _iw.addEventListener("open",function(){
                    _marker.getLabel().hide();
                })
                _iw.addEventListener("close",function(){
                    _marker.getLabel().show();
                })
                label.addEventListener("click",function(){
                    _marker.openInfoWindow(_iw);
                })
                if(!!json.isOpen){
                    label.hide();
                    _marker.openInfoWindow(_iw);
                }
            })()
        }
    }
    //创建InfoWindow
    function createInfoWindow(i){
        var json = markerArr[i];
        var iw = new BMap.InfoWindow("<b class='iw_poi_title' title='" + json.title + "'>" + json.title + "</b><div class='iw_poi_content'>"+json.content+"</div>");
        return iw;
    }
    //创建一个Icon
    function createIcon(json){
        var icon = new BMap.Icon("http://app.baidu.com/map/images/us_mk_icon.png", new BMap.Size(json.w,json.h),{imageOffset: new BMap.Size(-json.l,-json.t),infoWindowOffset:new BMap.Size(json.lb+5,1),offset:new BMap.Size(json.x,json.h)})
        return icon;
    }

    initMap();//创建和初始化地图

    //留言
    $("#sub").click(function(){
        $("#msgError").html("");
        $("#nameError").html("");
        $("#emailError").html("");
        $("#telPhoneError").html("");
        //留言
        var msg = $("#msg").val();
        //姓名
        var name = $("#name").val();
        //邮箱
        var email = $("#email").val();
        //电话
        var telphone = $("#telphone").val();

        if(msg.length < 5){
            $("#msgError").html("留言字数应在5字以上");
        }else if(name.length == 0){
            $("#nameError").html("请输入姓名");
        }else if(!checkEmail(email)){
            $("#emailError").html("邮箱格式不正确");
        }else if(!isPhone(telphone)){
            $("#telPhoneError").html("电话号码格式不正确");
        }else{
            $.ajax({
                type:"post",
                url: "/msg",
                data: {name:name,tel:telphone,email:email,msg:msg},
                dataType: "json",
                success: function(result){
                    //判断是否注册
                    if(parseInt(result.code) == 0){
                        $("#msg").val("");
                        $("#name").val("");
                        $("#email").val("");
                        $("#telphone").val("");
                        $("#msgTitle").html("留言已经发送,感谢您的建议!");
                    }else{
                        $("#msgTitle").html(result.msg);
                    }
                    $("#tips").show();
                }
            });
        }
    });

    //关闭弹出框
    $(".close_btn").click(function(){
        $("#tips").hide();
    });

    $("#sure").click(function(){
        $("#tips").hide();
    });
});
