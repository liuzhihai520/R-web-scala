/**
 * 方法描述:描述
 *
 * author 小刘
 * version v1.0
 * date 2015/11/30
 */
$(function(){
    // 初始化Web Uploader
    var uploader = WebUploader.create({

        // 选完文件后，是否自动上传。
        auto: true,

        // swf文件路径
        swf: "/assets/javascripts/webuploader/Uploader.swf",

        // 文件接收服务端。
        server: PATH.upload,

        // 选择文件的按钮。可选。
        // 内部根据当前运行是创建，可能是input元素，也可能是flash.
        pick: '#picker1',

        duplicate:true,
        //文件Name
        fileVal:'Filedata',

        //参数
        formData:{filetype:'cardImg',userId:$("#userId").val()},

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
    uploader.on( 'fileQueued', function( file ) {
        // 创建缩略图
        // 如果为非图片文件，可以不用调用此方法。
        // thumbnailWidth x thumbnailHeight 为 100 x 100
        uploader.makeThumb( file, function( error, src ) {
            if ( error ) {
                $("#frontImg").replaceWith('<span>不能预览</span>');
                return;
            }
            $("#frontImg").attr( 'src', src );
        }, 200, 200);
    });

    // 文件上传失败，显示上传出错。
    uploader.on( 'uploadError', function(file) {
        $("#img1Error").html("图片上传失败");
    });

    // 文件上传成功，给item添加成功class, 用样式标记上传成功。
    uploader.on( 'uploadSuccess', function(file,data) {
        $("#frontImg").val(PATH.image+data.url);
        $("#front").val(data.url);
    });

    // 完成上传完了，成功或者失败，先删除进度条。
    uploader.on( 'uploadComplete', function(file) {
        $("#img1Error").html("");
    });
});

