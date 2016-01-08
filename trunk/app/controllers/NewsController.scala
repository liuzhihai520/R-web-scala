package controllers

import core.mvc.WAction
import model.{News, UV}
import play.api.mvc.Controller

/**
  * Created by 24950 on 2015/12/2.
  */
class NewsController extends Controller {

    //新闻列表
    def newsList(pageNumber:Int) = WAction{
        implicit dataView=>
        //资讯
        val index = (pageNumber - 1) * 8
        val newsList = News.newsList(0,pageNumber,index,8)
        Ok(views.html.news.newsList(newsList))
    }

    //新闻详情
    def newsInfo(newsid: Long) = WAction {
        implicit request =>
        val newsInfo = News.newsInfo(newsid)
        UV.addUV("1", request.remoteAddress, newsid);
        if (null != newsInfo) {
            val newsList = News.newsList(1, 1, 1, 3)
            Ok(views.html.news.news(newsInfo, newsList))
        } else {
            Redirect("/")
        }
    }
}
