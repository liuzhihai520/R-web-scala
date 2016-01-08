/**
 * 方法描述:描述
 *
 * author 小刘
 * version v1.0
 * date 2015/11/27
 */
var countDown;
$(function(){

    $('.flexslider').flexslider();
    $('.process_bar').each(function(){
        var processBar=new ProcessBar($(this));
    });

    //初始化当前时间
    var systime = $("#sysTime").val();
    countDown = Tictac.init({
        currentTime: Date.parse(systime), //设置当前时间
        interval: 3000, //执行callback的时间间隔
        callback: function() {
            //重复执行的回调
        }
    });

    //JSON
    var list = $.parseJSON($("#itemArray").val());
    for(var i=0;i<list.length;i++){
        Tictac.create("itemId"+list[i].id, {
            targetId: "itemId"+list[i].id, //显示计时器的容器
            expires: Date.parse(list[i].time), //目标时间
            format: { //格式化对象
                days: '{d}天 ',
                hours: '{hh}小时 ',
                minutes: '{mm}分 ',
                seconds: '{ss}秒'
            },
            timeout: function() {
                //计时器 timeout 回调
            }
        });
    }
});

//计算banner pv uv
function countBanner(bid){
    $.ajax({
        type:"post",
        url: "/countBanner",
        data: {bid:bid},
        dataType: "json",
        success: function(result){

        }
    });
}
