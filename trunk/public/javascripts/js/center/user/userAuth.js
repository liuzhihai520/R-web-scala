/**
 * 方法描述:会员实名认证
 *
 * author 小刘
 * version v1.0
 * date 2015/11/30
 */
$(function(){
    //提交验证
    $("#sub").click(function(){
        var name = $("#name").val();
        var card = $("#card").val();
        var front = $("#front").val();
        var back = $("#back").val();
        var hand = $("#hand").val();
        if(!checkIdcard(card)){
            $("#card").focus();
            $("#cardError").html("身份证格式错误");
        }else if(front.length == 0){
            $("#img1Error").html("请上传身份证正面");
        }else if(back.length == 0){
            $("#img2Error").html("请上传身份证背面");
        }else if(hand.length == 0){
            $("#img3Error").html("请上手持身份证相片");
        }else{
            $("#form").submit();
        }
    });

    $("#name").bind("blur focus",function(){
        $("#nameError").html("");
    });
    $("#card").bind("blur focus",function(){
        $("#cardError").html("")
    });
});

