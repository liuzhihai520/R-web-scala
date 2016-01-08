/**
 * 方法描述:编辑用户信息
 *
 * author 小刘
 * version v1.0
 * date 2015/11/30
 */
var has = 0;
$(function(){

    //设置性别
    $("#man").click(function(){
        $("#sex").val("男");
    });
    $("#woman").click(function(){
        $("#sex").val("女");
    });


    //绑定省
    BindProvince();

    //过滤市
    $("#ddlProvince").change(function(){
        selectMoreCity(this);
    });

    //绑定城市
    var citys = $("#citys").val();
    if(citys.length != 0){
        citys = citys.split("-");
    }
    BindCity(citys[1]);

    //查看用户名是否重复
    $("#accountname").blur(function(){
        var accountname = $("#accountname").val();
        if(accountname.length < 2){
            has = 1;
        }else {
            $.ajax({
                type:"post",
                url: "/isRegUsername",
                data: {username:accountname},
                dataType: "json",
                success: function(result){
                    if(parseInt(result.code) == 1){
                        has = 2;
                        $("#accountnameError").html("用户名已存在");
                        $("#accountname").focus();
                    }else{
                        has = 0;
                        $("#accountnameError").html("");
                    }
                }
            });
        }
    });

    //保存用户信息
    $("#save").click(function(){
        //头像
        var head = $("#head").val();
        //姓名
        var name = $("#name").val();
        //出生日志
        var birthday = $("#birthday").val();
        //手机号
        var tel = $("#tel").val();
        //公司名称
        var pcname = $("#pcname").val();
        //性别
        var sex = $('input:radio[name="sexs"]:checked').val();
        //职位
        var job = $("#job").val();
        //邮箱
        var email = $("#email").val();
        //省份
        var province = $("#ddlProvince").val();
        //城市
        var city = $("#ddlCity").val();

        if(head.length == 0){
            $("#accountname").focus();
            $("#avatarError").html("请上传头像");
        }else if(name.length == 0){
            $("#nameError").html("请填写姓名");
        }else if(birthday.length == 0){
            $("#birthdayError").html("请选择出生日期");
        }else if(typeof(sex) == "undefined"){
            $("#sexError").html("请选择性别");
        }else if(has == 1){
            $("#accountname").focus();
            $("#accountnameError").html("用户名格式为2-18个字符");
        }else if(has == 2){
            $("#accountname").focus();
            $("#accountnameError").html("用户名已存在");
        }else if(!isPhone(tel)){
            $("#telError").html("手机号格式不正确");
        }else if(!checkEmail(email)){
            $("#email").focus();
            $("#emailError").html("邮箱格式不正确");
        }else if(province.length == 0){
            $("#province").focus();
            $("#cityError").html("请选择省份");
        }else if(city.length == 0){
            $("#city").focus();
            $("#cityError").html("请选择城市");
        }else if(pcname.length == 0){
            $("#pcname").focus();
            $("#pcnameError").html("请填写公司名称");
        }else {
            $("#form").submit();
        }
    });

    //焦点
    $("#name").focus(function(){
        $("#nameError").html("");
    });
    $("#birthday").focus(function(){
        $("#birthdayError").html("");
    });
    $("#tel").focus(function(){
        $("#telError").html("");
    });
    $("#pcname").focus(function(){
        $("#pcnameError").html("");
    });
    $("#ddlProvince").focus(function(){
        $("#cityError").html("");
    });
    $("#ddlCity").focus(function(){
        $("#cityError").html("");
    });
});
