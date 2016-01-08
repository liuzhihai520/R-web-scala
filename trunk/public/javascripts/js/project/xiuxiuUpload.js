/**
 * 方法描述:项目图片上传
 *
 * author 小刘
 * version v1.0
 * date 2016/1/6
 */
//初始化加载
$(document).ready(function(){
    //设置SWF语言
    xiuxiu.setLaunchVars("language", "zh_cn");
    //设置参数
    //xiuxiu.setLaunchVars("maxFinalWidth", 220);
    //xiuxiu.setLaunchVars("maxFinalHeight", 220);
    //禁用摄像头
    xiuxiu.setLaunchVars("cameraEnabled", 0);
    //去掉左侧
    xiuxiu.setLaunchVars("customMenu", []);
    //去掉右侧
    xiuxiu.setLaunchVars("avatarPreview", {visible:false});
    xiuxiu.setLaunchVars("cropPresets", "4:3");
    //初始化SWF
    xiuxiu.embedSWF("swfContainer",5,"460","400");
    //设置上传地址
    xiuxiu.setUploadURL("http://upload.ruijiutou.com/upload");
    //设置上传参数
    xiuxiu.setUploadArgs({filetype:"projectImg"});
    //设置上传方式
    xiuxiu.setUploadType (2);
    //初始化图片
    xiuxiu.onInit = function (){
        xiuxiu.loadPhoto("http://139.196.182.217:8088/assets/images/picture.jpg");
    }
    //成功返回
    xiuxiu.onUploadResponse = function (data){
        var obj = $.parseJSON(data);
        if(obj.status == 0){
            $("#imgurlError").html("上传成功");
            $("#imgurl").val(data.url);
        }else{
            $("#imgurlError").html(data.message);
        }
    }
});
