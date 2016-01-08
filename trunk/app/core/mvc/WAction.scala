package core.mvc

import core.utils.KeyUtils
import play.api.Routes
import play.api.mvc._
import scala.concurrent.Future

object WAction extends ActionBuilder[ViewData] {
  def invokeBlock[A](request: Request[A], block: (ViewData[A]) => Future[Result]) = {
    val v = new ViewData(request).apply()
    block(v)
  }
}

object Authorize extends ActionBuilder[ViewData] {
  def invokeBlock[A](request: Request[A], block: (ViewData[A]) => Future[Result]) = {
    request.cookies.get(KeyUtils.USER_INFO).map { token =>
      block(new ViewData(request).apply())
    } getOrElse {
      Future.successful(
        Results.Redirect("/regLogin")
      )
    }
  }
}

object Action extends ActionBuilder[Request] {
  def invokeBlock[A](request: Request[A], block: (Request[A]) => Future[Result]) = block(request)
}