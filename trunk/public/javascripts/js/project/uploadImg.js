/**
 * 方法描述:上传文件
 *
 * author 小刘
 * version v1.0
 * date 2015/11/29
 */
 var crop;
 $(function(){
     // 初始化Web Uploader
     var uploader = WebUploader.create({

         // 选完文件后，是否自动上传。
         auto: true,

         // swf文件路径
         swf: "/assets/javascripts/webuploader/Uploader.swf",

         // 文件接收服务端。
         server: 'http://upload.ruijiutou.com/upload',

         // 选择文件的按钮。可选。
         // 内部根据当前运行是创建，可能是input元素，也可能是flash.
         pick: '#picker',

         duplicate:true,
         //文件Name
         fileVal:'Filedata',

         //参数
         formData:{filetype:'projectImg'},

         //启用FLASH上传
         runtimeOrder:'flash',

         //禁用拖拽功能
         disableGlobalDnd: true,

         // 只允许选择图片文件。
         accept: {
             title: 'Images',
             extensions: 'gif,jpg,jpeg,bmp,png',
             mimeTypes: 'image/*'
         }
     });

     // 当有文件添加进来的时候
     uploader.on("fileQueued", function(file) {
         if(crop != null){
             $("#imgView").cropper('destroy');
         }
         // 创建缩略图
         //uploader.makeThumb( file, function( error, src ) {
         //    if ( error ) {
         //        $("#imgView").replaceWith('<span>不能预览</span>');
         //        return;
         //    }
         //    $("#imgView").attr( 'src', src);
         //});
     });

     // 文件上传失败，显示上传出错。
     uploader.on( 'uploadError', function(file) {
        $("#imgurlError").html("图片上传失败");
     });

     // 文件上传成功，给item添加成功class, 用样式标记上传成功。
     uploader.on("uploadSuccess", function(file,data) {
         if(data.status == 0){
             $("#tempUrl").val(data.url);
             $("#imgView").attr("src", PATH.image+data.url);
             crop = $("#imgView").cropper({
                 aspectRatio: 4 / 3,
                 crop: function(e) {
                     $("#x").val(e.x);
                     $("#y").val(e.y);
                     $("#width").val(e.width);
                     $("#height").val(e.height);
                     console.log("x坐标："+e.x+"y坐标："+e.y+"宽度："+e.width+"高度："+e.height);
                 }
             });
             $("#cropImage").removeAttr("disabled");
         }else{
             $("#imgurlError").html(data.message);
         }
     });

     // 完成上传完了，成功或者失败，先删除进度条。
     uploader.on( 'uploadComplete', function(file) {
         $("#imgurlError").html("");
     });

     //裁剪图片
     $("#cropImage").click(function(){
         $("#cropImage").attr("disabled","true");
         var url = $("#tempUrl").val();
         var x = parseInt($("#x").val());
         var y = parseInt($("#y").val());
         var w = $("#width").val();
         var h = $("#height").val();

         $.ajax({
             type : "get",
             url: "http://upload.ruijiutou.com/cropper",
             dataType : "json",
             data:{
                 filename:url,
                 filetype:"item",
                 x:x,
                 y:y,
                 size:w+"x"+h
             },
             success: function(data) {
                 if(data.status == 0){
                     $("#imgurl").val(data.url);
                     $("#imgView").cropper('destroy');
                     $("#imgView").attr("src", PATH.image+data.url);
                     $("#imgurlError").html("图片上传成功");
                 }else{
                     $("#imgurlError").html("图片保存失败");
                 }
             }
         });
     });
 });
