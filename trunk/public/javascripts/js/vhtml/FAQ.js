/**
 * 方法描述:平台答疑
 *
 * author 小刘
 * version v1.0
 * date 2015/12/4
 */
 $(function(){
     $("#FAQ_tab a").click(function () {
         var index = $("#FAQ_tab a").index(this);
         $(this).addClass("active").siblings().removeClass("active");
         $(".w980").eq(index).show().siblings(".w980").hide();
     });

     $("#FAQ_center_tab a").click(function () {
         var index = $("#FAQ_center_tab a").index(this);
         $(this).addClass("active").siblings().removeClass("active");
         $(".FAQ_center_con").eq(index).show().siblings(".FAQ_center_con").hide();
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
 });