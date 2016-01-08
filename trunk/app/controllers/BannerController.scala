package controllers

import core.mvc.WAction
import model.{UV, Banner}
import play.api.mvc.Controller

/**
 * Created by 24950 on 2015/12/6.
 * HH
 */
class BannerController extends Controller{

  def BannerInfo(bannerId:Long)=WAction{
    implicit request=>
      UV.addUV("3", request.remoteAddress, bannerId)
      val banner=Banner.banner(bannerId)
      Ok(views.html.banner.banner(banner))
  }
}
