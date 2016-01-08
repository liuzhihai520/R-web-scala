/**
 * 方法描述:个人中心
 *
 * author 小刘
 * version v1.0
 * date 2015/11/29
 */
$(function () {
    $("[data-toggle='tooltip']").tooltip();
    $("#user_nav .nav_item").click(function () {
        var index = $("#user_nav .nav_item").index(this);
        $(this).addClass("active").siblings().removeClass("active");
        $(".result").eq(index).show().siblings(".result").hide();
    });

    $("#isPublish").click(function(){
        $.ajax({
            type:"post",
            url: "/isRealAuth",
            data: {},
            dataType: "json",
            success: function(result){
                if(parseInt(result.status) == 2){
                    window.location.href = "/publish"
                }else{
                    $("#publishLayer").show();
                }
            }
        });
    });

    $("#closeLayer").click(function(){
        $("#publishLayer").hide();
    });
    $("#sure").click(function(){
        $("#publishLayer").hide();
    });
});