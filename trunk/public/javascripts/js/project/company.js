/**
 * 方法描述:公司信息
 *
 * author 小刘
 * version v1.0
 * date 2015/12/1
 */
 var obj = {

 }

 $(function(){

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

    //绑定挂牌
    $("#otc").click(function(){
        $("#islisting").val(1);
    });
    //绑定新三板
    $("#three").click(function(){
        $("#islisting").val(2);
    });

    //提交表单
    $("#sub").click(function(){
        var pcname = $("#pcname").val();
        var ddlProvince = $("#ddlProvince").val();
        var ddlCity = $("#ddlCity").val();
        var licensenum = $("#licensenum").val();
        var linkman = $("#linkman").val();
        var linkmantel = $("#linkmantel").val();
        var islisting = $("#islisting").val();
        var listingcode = $("#listingcode").val();
        if(pcname.length == 0){
            $("#pcname").focus();
            $("#pcnameError").html("请填写公司名称");
        }else if(ddlProvince.length == 0 || ddlProvince == "省份"){
            $("#ddlProvince").focus();
            $("#provinceError").html("请填选择省市");
        }else if(ddlCity.length == 0 || ddlCity == "城市"){
            $("#ddlCity").focus();
            $("#provinceError").html("请填选择城市");
        }else if(licensenum.length == 0 ){
            $("#licensenum").focus();
            $("#licensenumError").html("请输入工商执照注册号");
        }else if(parseInt(islisting) != 0 && listingcode.length == 0){
            $("#listingcode").focus();
            $("#listingcodeError").html("请填写对应代码");
        }else if(linkman.length == 0){
            $("#linkman").focus();
            $("#linkmanError").html("请输入联系人");
        }else if(!isPhone(linkmantel) && !checkEmail(linkmantel)){
            $("#linkmantel").focus();
            $("#linkmantelError").html("联系方式为邮箱或者电话号码");
        }else{
            $("#form").submit();
        }
    });

    //控制显示
    $("#pcname").bind("blur focus",function(){
        $("#pcnameError").html("");
    });
     $("#licensenum").bind("blur focus",function(){
        $("#licensenumError").html("");
     });
     $("#linkman").bind("blur focus",function(){
         $("#linkmanError").html("");
     });
     $("#linkmantel").bind("blur focus",function(){
         $("#linkmantelError").html("");
     });
 });
