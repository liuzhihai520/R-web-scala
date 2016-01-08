/**
 * 方法描述:商业计划书
 *
 * author 小刘
 * version v1.0
 * date 2015/11/29
 */
 $(function(){
     // 初始化Web Uploader
     var uploaderFile = WebUploader.create({

         // 选完文件后，是否自动上传。
         auto: true,

         // swf文件路径
         swf: "/assets/javascripts/webuploader/Uploader.swf",

         // 文件接收服务端。
         server: 'http://upload.ruijiutou.com/upload',

         // 选择文件的按钮。可选。
         // 内部根据当前运行是创建，可能是input元素，也可能是flash.
         pick: '#filePicker',

         duplicate:true,
         //文件Name
         fileVal:'Filedata',

         //参数
         formData:{filetype:'projectFile'},

         //启用FLASH上传
         runtimeOrder:'flash',

         //禁用拖拽功能
         disableGlobalDnd: true,
         // 只允许选择图片文件。
         accept: {
             title: 'Images',
             extensions: 'PPT,PPTX,PDF'
         }
     });

     // 当有文件添加进来的时候
     uploaderFile.on( 'fileQueued', function( file ) {
        //添加文件时的处理
     });

     // 文件上传失败，显示上传出错。
     uploaderFile.on( 'uploadError', function(file) {
         $("#planError").html("文件上传失败");
     });

     // 文件上传成功，给item添加成功class, 用样式标记上传成功。
     uploaderFile.on( 'uploadSuccess', function(file,data) {
         $("#plan").val(data.url);
     });

     // 完成上传完了，成功或者失败，先删除进度条。
     uploaderFile.on( 'uploadComplete', function(file) {
         $("#planError").html("文件上传成功");
     });
 });
