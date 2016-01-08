$(function () {
    $('.process_bar').each(function () {
        var processBar = new ProcessBar($(this));
    })
    $('.layer_main').click(function (e) {
        e.stopPropagation();
    })
    $('.close_btn').click(function () {
        $('.layer').hide();
    })

    $('.invitation .layer_submit').click(function () {
        $('.layer').hide();
    })

    //$('.enter_btn').click(function () {
    //    if ($(this).text() == "立即报名") {
    //        $(this).text("取消报名")
    //        $('.layer.enter').show();
    //        setTimeout(function () {
    //            $('body').one('click', function () {
    //                $('.layer.enter').hide();
    //            })
    //        })
    //    }
    //    else {
    //        $(this).text("立即报名");
    //        $('.layer.cancel_enter').show();
    //        setTimeout(function () {
    //            $('body').one('click', function () {
    //                $('.layer.cancel_enter').hide();
    //            })
    //        })
    //    }
    //});

    //$('.enter_btn').click(function () {
    //    if ($(this).text() == "立即报名") {
    //        $(this).text("取消报名")
    //        $('.layer.enter').show();
    //        setTimeout(function () {
    //            $('body').one('click', function () {
    //                $('.layer.enter').hide();
    //            })
    //        })
    //    }
    //    else {
    //        $(this).text("立即报名");
    //        $('.layer.cancel_enter').show();
    //        setTimeout(function () {
    //            $('body').one('click', function () {
    //                $('.layer.cancel_enter').hide();
    //            })
    //        })
    //    }
    //});

    //$('.invitation').click(function () {
    //    $('.layer.invitation').show();
    //})

    $('.enter_login').click(function () {
        $('.layer.enter_login').show();
    })

    $('.realNameExamine').click(function () {
        $('.layer.realNameExamine').show();
    })

    $('.bp_download').click(function () {
        $('.layer.bp_download').show();
    })

    $('.weixin').click(function () {
        $('.layer.weixin').show();
    })

})