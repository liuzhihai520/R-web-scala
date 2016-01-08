/**
 * 方法描述:项目详情分页
 *
 * @author 小刘
 * @version v1.0
 * @date 2015/12/13
 */
var countDown;
$(function(){

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

   $("#moreBtn").click(function(){
       var pageNumber = $("#pageNumber").val();
       $.ajax({
           type:"GET",
           url: "/itemListV/"+pageNumber,
           dataType: "text",
           success: function(result){
               //清除当前页码
               $("#pageNumber").remove();
               //清除是否有下一页
               $("#hasNext").remove();
               //清除子页面集合
               $("#childItemArray").remove();
               //更新系统时间
               $("#sysTime").remove();
               //元素填充
               $("#itemRow").append(result);
               //是否有下一页
               var hasNext = $("#hasNext").val();
               if(hasNext == "false"){
                   $("#moreBtn").hide();
               }
               //初始化倒计时
               countDown = Tictac.init({
                   currentTime: Date.parse(systime), //设置当前时间
                   interval: 3000, //执行callback的时间间隔
                   callback: function() {
                       //重复执行的回调
                   }
               });
               //倒计时
               var childList = $.parseJSON($("#childItemArray").val());
               for(var i=0;i<childList.length;i++){
                   Tictac.create("childItemId"+childList[i].id, {
                       targetId: "childItemId"+childList[i].id, //显示计时器的容器
                       expires: Date.parse(childList[i].time), //目标时间
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
           }
       });
   });
});