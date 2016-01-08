package controllers

import java.io.{InputStreamReader, BufferedReader, FileInputStream, File}

import org.apache.commons.io.FilenameUtils
import play.api.mvc._
import scala.sys.process.Process

/**
  * 方法描述:TODO
  *
  * author 小刘
  * version v1.0
  * date 2015/12/11
  */
class ImageController extends Controller{

    //路劲
    val PATH = "/home/resource/"
    //哈希值
    val HEXES = "0123456789ABCDEF"

    //Editor-图片特殊处理
    def editorUpload = Action(parse.multipartFormData) {
        implicit request=>
            val callback = request.getQueryString("CKEditorFuncNum").getOrElse("")
            request.body.file("upload").map { f =>
                val hash = fileHash(f.ref.file).toLowerCase()
                //后缀名
                val filename = f.filename
                val fix = filename.substring(filename.lastIndexOf(""".""")+1,filename.length())
                if(fix.equalsIgnoreCase("jpg")||fix.equalsIgnoreCase("jpeg")||fix.equalsIgnoreCase("bmp")||fix.equalsIgnoreCase("png")){
                    val url = s"editor/$hash.jpg"
                    f.ref.moveTo(new File(createPath(url)), replace = true)
                    Ok(
                        s"""
                           |<script type="text/javascript">
                           |window.parent.CKEDITOR.tools.callFunction('$callback','http://image.ruijiutou.com/$url','')
                           |</script>
                """.stripMargin
                    ).as("text/html")
                }else{
                    Ok(
                        s"""
                           |<script type="text/javascript">
                           |window.parent.CKEDITOR.tools.callFunction('$callback','','文件格式不正确(必须为.jpg/.gif/.bmp/.png文件)')
                           |</script>
                """.stripMargin
                    ).as("text/html")
                }
            } getOrElse {
                Ok(
                    s"""
                       |<script type="text/javascript">
                       |window.parent.CKEDITOR.tools.callFunction('$callback','','上传的文件不存在')
                       |</script>
                """.stripMargin
                ).as("text/html")
            }
    }


    //创建路劲
    def createPath(filePath: String): String = {
        val path = FilenameUtils.getPath(filePath)
        val name = FilenameUtils.getBaseName(filePath)
        val ext = FilenameUtils.getExtension(filePath)

        if (isWindows) {
            new File(s"$PATH$path").mkdirs()
        } else {
            Process(s"mkdir -p $PATH$path").!
        }

        PATH + path + name + "." + ext
    }

    //文件哈希
    def fileHash(file: File): String = {
        val input = new StringBuffer()
        val stream = new FileInputStream(file)
        val buffer = new BufferedReader(new InputStreamReader(stream))
        while (buffer.ready()) {
            input.append(buffer.readLine() + "\n")
        }
        buffer.close()
        byteAryToHexStr(getHash(input.toString(), "MD5"))
    }

    def byteAryToHexStr(input: Array[Byte]): String = {
        val hex = new StringBuffer(2 * input.length)
        input.foreach({ f =>
            hex.append(HEXES.charAt((f & 0xF0) >> 4)).append(HEXES.charAt((f & 0x0F)))
        })
        return hex.toString()
    }

    def getHash(toHash: String, technique: String): Array[Byte] = {
        val parse = java.security.MessageDigest.getInstance(technique)
        parse.reset()
        parse.update(toHash.getBytes())
        return parse.digest()
    }


    def isWindows(): Boolean = {
        if (System.getProperties().getProperty("os.name").toUpperCase().indexOf("WINDOWS") != -1) {
            true;
        } else {
            false
        }
    }
}
