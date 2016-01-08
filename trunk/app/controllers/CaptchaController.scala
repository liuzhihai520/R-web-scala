package controllers

import java.awt.Color

import core.utils.CookieUtil
import org.patchca.filter.predefined.CurvesRippleFilterFactory
import play.api.mvc.Controller
import org.patchca.color.{SingleColorFactory, RandomColorFactory}
import org.patchca.service.{Captcha, ConfigurableCaptchaService}
import javax.imageio.ImageIO
import java.io.{ByteArrayInputStream, ByteArrayOutputStream}
import javax.imageio.stream.ImageOutputStream
import org.patchca.font.RandomFontFactory
import org.patchca.word.RandomWordFactory
import org.patchca.filter.ConfigurableFilterFactory
import java.awt.image.BufferedImageOp
import org.patchca.filter.library.{AbstractImageOp, WobbleImageOp}
import org.patchca.text.renderer.BestFitTextRenderer
import play.api.mvc._
import play.api.libs.iteratee.Enumerator
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * 方法描述:TODO
  *
  * author 小刘
  * version v1.0
  * date 2015/11/23
  */
class CaptchaController extends Controller{
    def getCaptcha = Action {
        implicit request =>
            val captcha: Captcha = createImage()

            val out: ByteArrayOutputStream = new ByteArrayOutputStream()
            val stream: ImageOutputStream = ImageIO.createImageOutputStream(out)
            ImageIO.write(captcha.getImage, "png", stream)
            val data: Enumerator[Array[Byte]] = Enumerator.fromStream(new ByteArrayInputStream(out.toByteArray))
            Ok.chunked(data).as("image/png").withSession(request.session + ("vcode" -> captcha.getChallenge)).withCookies(CookieUtil.toCodeCookie("passCode",captcha.getChallenge))
                .withHeaders(CACHE_CONTROL -> "no-cache").withHeaders("P3P"->"CP=\"IDC DSP COR CURa ADMa OUR IND PHY ONL COM STA\"")
    }

    def createImage() = {
        val cs: ConfigurableCaptchaService = new ConfigurableCaptchaService()
        cs.setWidth(80)
        cs.setHeight(32)

        val colorFactory = new RandomColorFactory()
        cs.setColorFactory(colorFactory)

        val fontFactory = new RandomFontFactory()
        fontFactory.setMaxSize(28)
        fontFactory.setMinSize(24)
        cs.setFontFactory(fontFactory)

        val wordFactory = new RandomWordFactory()
        wordFactory.setCharacters("abcdefghkmnpqstwxyz23456789")
        wordFactory.setMaxLength(4)
        wordFactory.setMinLength(4)
        cs.setWordFactory(wordFactory)

        // 图片滤镜设置
        val filterFactory = new ConfigurableFilterFactory()

        val filters = new java.util.ArrayList[BufferedImageOp]()
        val wobbleImageOp = new WobbleImageOp()
        wobbleImageOp.setEdgeMode(AbstractImageOp.EDGE_CLAMP)
        wobbleImageOp.setxAmplitude(2.0)
        wobbleImageOp.setyAmplitude(1.0)
        filters.add(wobbleImageOp)
        filterFactory.setFilters(filters)

        cs.setFilterFactory(filterFactory)

        // 文字渲染器设置
        val textRenderer = new BestFitTextRenderer()
        textRenderer.setBottomMargin(3)
        textRenderer.setTopMargin(3)
        cs.setTextRenderer(textRenderer)

        //干扰
        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)))
        cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory))
        cs.getCaptcha
    }
}
