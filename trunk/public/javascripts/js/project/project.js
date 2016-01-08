/**
 * 方法描述:发布
 *
 * author 小刘
 * version v1.0
 * date 2015/11/28
 */

//关键词过滤
var isKey = false;
 $(function(){

     //实例化编辑器
     var editor = CKEDITOR.replace("description");

     //更改状态
     $("#projectstage").change(function(){
        var projectstage= $("#projectstage").val();
        if(projectstage == "自定义"){
            $('#customStage').removeClass("hidden");
        }else{
            $("#custom").val("");
            $('#customStage').addClass("hidden");
        }
     });

     //审核发布项目
     $("#sub").click(function(){
        //参数
        var name = $("#name").val();
        var tag = $("#tag").val();
        var financingsum = $("#financingsum").val();
        var use = $("#use").val();
        var equity = $("#equity").val();
        var imgurl = $("#imgurl").val();
        var plan = $("#plan").val();
        var synopsis = $("#synopsis").val();
        var description = editor.document.getBody().getText();;
        var projectstage = $("#projectstage").val();
        var financingterm = $("#financingterm").val();
        var startnum = $("#startnum").val();
        var shares = $("#shares").val();
        var regcapital = $("#regcapital").val();
        var tradedescription = $("#tradedescription").val();
        //关键词
        var json = $.parseJSON($("#key").val());
        var key = json.txt.split("\|");
        for(var i=0;i<key.length;i++){
            if(name.length > 0 && name.indexOf(key[i]) >= 0){
                isKey = true;
                $("#name").focus();
                $("#nameError").html("包含敏感词:"+key[i]+"");
                return;
            }else if(tag.length > 0 && tag.indexOf(key[i]) >= 0){
                isKey = true;
                $("#tag").focus();
                $("#tagError").html("包含敏感词:"+key[i]+"");
                return;
            }else if(tradedescription.length > 0 && tradedescription.indexOf(key[i]) >= 0){
                isKey = true;
                $("#tradedescription").focus();
                $("#tradedescriptionError").html("包含敏感词:"+key[i]+"");
                return;
            }else if(use.length > 0 && use.indexOf(key[i]) >= 0){
                isKey = true;
                $("#use").focus();
                $("#useError").html("包含敏感词:"+key[i]+"");
                return;
            }else if(synopsis.length  > 0 && synopsis.indexOf(key[i]) >= 0){
                isKey = true;
                $("#synopsis").focus();
                $("#synopsisError").html("包含敏感词:"+key[i]+"");
                return;
            }else if(description.length > 0 && description.indexOf(key[i]) >= 0){
                isKey = true;
                $("#description").focus();
                $("#descriptionError").html("包含敏感词:"+key[i]+"");
                return;
            }else {
                isKey = false;
            }
        }
        //验证
        if(name.length == 0 || name.length > 50){
            $("#name").focus();
            $("#nameError").html("项目名称长度为4-50");
        }else if(tag.length == 0){
            $("#tag").focus();
            $("#tagError").html("请输入Tag");
        }else if(financingsum.length == 0 || parseFloat(financingsum) <= 0){
            $("#financingsum").focus();
            $("#financingsumError").html("融资金额应为整数");
        }else if(startnum.length == 0 || parseFloat(startnum) < 0){
            $("#startnum").focus();
            $("#startnumError").html("请输入起投金额");
        }else if(0 == regcapital.length){
            $("#regcapital").focus();
            $("#regcapitalError").html("请输入注册资金");
        }else if(regcapital.length == 0 || parseFloat(regcapital) <= 0){
            $("#regcapital").focus();
            $("#regcapitalError").html("注册资金必须大于0");
        }else if(shares.length == 0 || parseFloat(shares) <= 0){
            $("#shares").focus();
            $("#sharesError").html("每股价格必须大于0");
        }else if(use.length < 4 || use.length > 200){
            $("#use").focus();
            $("#useError").html("资金用途最小为4个字符,最大为200个字符");
        }else if(equity.length == 0 || parseFloat(equity) <= 0){
            $("#equity").focus();
            $("#equityError").html("输入的股权百分比不正确");
        }else if(!isNumeric(financingterm) || parseInt(financingterm) < 30){
            $("#financingterm").focus();
            $("#financingtermError").html("只能输入数字");
        }else if(imgurl.length == 0){
            $("#imgurlError").html("请上传项目封面图");
        }else if(plan.length == 0){
            $("#planError").html("请上传商业计划书");
        }else if(synopsis.length == 0){
            $("#synopsis").focus();
            $("#synopsisError").html("请填写项目简介");
        }else if(description.length == 0){
            $("#description").focus();
            $("#descriptionError").html("请填写项目描述");
        }else {
            $("#form").submit();
        }
     });

     //处理焦点移除
     $("#name").blur(function(){
        var name = $("#name").val();
        if(name.length > 0){
            $("#nameError").html("");
        }
     });
     $("#tag").blur(function(){
         var name = $("#tag").val();
         if(name.length > 0){
             $("#tagError").html("");
         }
     });
     $("#custom").blur(function(){
         var custom = $("#custom").val();
         if(custom.length > 0){
             $("#customError").html("");
         }
     });
     $("#financingsum").blur(function(){
         var financingsum = $("#financingsum").val();
         if(isMoney(financingsum)){
             $("#financingsumError").html("");
         }
     });
     $("#use").blur(function(){
         var use = $("#use").val();
         if(use.length > 0){
             $("#useError").html("");
         }
     });
     $("#equity").blur(function(){
         var equity = $("#equity").val();
         if(parseFloat(equity) > 0){
             $("#equityError").html("");
         }
     });
     $("#imgurl").blur(function(){
         var imgurl = $("#imgurl").val();
         if(imgurl.length > 0){
             $("#imgError").html("");
         }
     });
     $("#plan").blur(function(){
         var plan = $("#plan").val();
         if(plan.length > 0){
             $("#planError").html("");
         }
     });
     $("#synopsis").blur(function(){
         var synopsis = $("#synopsis").val();
         if(synopsis.length > 0){
             $("#synopsisError").html("");
         }
     });
     $("#description").blur(function(){
         var description = $("#description").val();
         if(description.length > 0){
             $("#descriptionError").html("");
         }
     });
     $("#startnum").blur(function(){
         var startnum = $("#startnum").val();
         if(parseFloat(startnum) > 0){
             $("#startnumError").html("");
         }
     });
     $("#shares").blur(function(){
         var shares = $("#shares").val();
         if(parseFloat(shares) > 0){
             $("#sharesError").html("");
         }
     });
     $("#financingterm").blur(function(){
        var financingterm = $("#financingterm").val();
         if(!isNumeric(financingterm) || parseInt(financingterm) < 30){
             $("#financingtermError").html("只能输入数字");
         }else{
             $("#financingtermError").html("");
         }
     });
 });

// 计算融资金额股份(股权)占比(%)
function sumEquity(){
    var regcapital = $("#regcapital").val();
    var financingsum = $("#financingsum").val();
    var shares = $("#shares").val();
    if(0==regcapital.length){
        return false;
    }
    if(0==financingsum.length){
        return false;
    }
    if(0==shares.length){
        return false;
    }
    var a=Number(regcapital)*10000;
    var b=Number(financingsum)*10000;
    var c=Number(shares);
    $("#equity").val((b/c/(a+(b/c))*100).toFixed(4));
}