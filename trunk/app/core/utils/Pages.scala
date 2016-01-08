package core.utils

import anorm._
import anorm.SqlParser._
import play.api.db.DB
import play.api.Play.current

/**
  * Created with IDEA.
  * User: lzh
  * Date: 13-10-8
  * Time: 下午4:36
  */
case class Pages[T](list: List[T] = Nil, total: Int = 0, var navigatePages: Int = 0, var pageNumber: Int = 1, limit: Int = 10) {

    var pages: Int = (this.total - 1) / this.limit + 1
    // 总页数
    var nowNumber: Int = pageNumber
    // 当前页
    var isFirstPage: Boolean = false
    // 是否为第一页
    var isLastPage: Boolean = false
    // 是否为最后一页
    var hasPreviousPage: Boolean = false
    // 是否有前一页
    var hasNextPage: Boolean = false
    // 是否有下一页
    var navigatePageNumbers: Array[Int] = Array(1) // 所有导航页号

    if (pageNumber < 1) {
        this.pageNumber = 1
    } else if (pageNumber > this.pages) {
        this.pageNumber = this.pages
    } else {
        this.pageNumber = pageNumber
    }

    calcNavigatePageNumbers()
    judgePageBoudary()

    /**
      * 计算导航页
      */
    def calcNavigatePageNumbers() {
        // 当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatePageNumbers = new Array[Int](pages)
            for (i <- 0 to pages - 1) {
                navigatePageNumbers(i) = i + 1
            }
        } else {
            // 当总页数大于导航页码数时
            navigatePageNumbers = new Array[Int](navigatePages)
            var startNum: Int = pageNumber - navigatePages / 2
            var endNum: Int = pageNumber + navigatePages / 2

            if (startNum < 1) {
                startNum = 1
                // (最前navigatePages页
                for (i <- 0 to navigatePages - 1) {
                    navigatePageNumbers(i) = startNum
                    startNum += 1
                }
            } else if (endNum > pages) {
                endNum = pages
                // 最后navigatePages页

                for (i <- 0 to navigatePages - 1 reverse) {
                    navigatePageNumbers(i) = endNum
                    endNum -= 1
                }

            } else {
                // 所有中间页
                for (i <- 0 to navigatePages - 1) {
                    navigatePageNumbers(i) = startNum
                    startNum += 1
                }
            }
        }
    }

    /**
      * 判定页面边界
      */
    def judgePageBoudary() {
        isFirstPage = pageNumber == 1
        isLastPage = pageNumber == pages && pageNumber != 1
        hasPreviousPage = pageNumber > 1
        hasNextPage = pageNumber < pages
    }

    def size() = {
        list.size
    }

    implicit def get(i: Int) = {
        list(i)
    }
}

case class SqlPage[T](var navigatePages: Int = 0, var pageNumber: Int = 1, limit: Int = 10, sqlParser: RowParser[T], sql: String = null, sort: String = null) {

    var total: Int = 0
    var list: List[T] = Nil
    var pages: Int = 0
    var nowPage: Int = pageNumber
    // 当前页
    // 总页数
    var isFirstPage: Boolean = false
    // 是否为第一页
    var isLastPage: Boolean = false
    // 是否为最后一页
    var hasPreviousPage: Boolean = false
    // 是否有前一页
    var hasNextPage: Boolean = false
    // 是否有下一页
    var navigatePageNumbers: Array[Int] = Array(1) // 所有导航页号

    initPage()

    if (pageNumber < 1) {
        this.pageNumber = 1
    } else if (pageNumber > this.pages) {
        this.pageNumber = this.pages
    } else {
        this.pageNumber = pageNumber
    }

    calcNavigatePageNumbers()
    judgePageBoudary()

    /**
      * 计算导航页
      */
    def calcNavigatePageNumbers() {
        // 当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatePageNumbers = new Array[Int](pages)
            for (i <- 0 to pages - 1) {
                navigatePageNumbers(i) = i + 1
            }
        } else {
            // 当总页数大于导航页码数时
            navigatePageNumbers = new Array[Int](navigatePages)
            var startNum: Int = pageNumber - navigatePages / 2
            var endNum: Int = pageNumber + navigatePages / 2

            if (startNum < 1) {
                startNum = 1
                // (最前navigatePages页
                for (i <- 0 to navigatePages - 1) {
                    navigatePageNumbers(i) = startNum
                    startNum += 1
                }
            } else if (endNum > pages) {
                endNum = pages
                // 最后navigatePages页

                for (i <- 0 to navigatePages - 1 reverse) {
                    navigatePageNumbers(i) = endNum
                    endNum -= 1
                }

            } else {
                // 所有中间页
                for (i <- 0 to navigatePages - 1) {
                    navigatePageNumbers(i) = startNum
                    startNum += 1
                }
            }
        }
    }

    /**
      * 判定页面边界
      */
    def judgePageBoudary() {
        isFirstPage = pageNumber == 1
        isLastPage = pageNumber == pages && pageNumber != 1
        hasPreviousPage = pageNumber > 1
        hasNextPage = pageNumber < pages
    }

    def size() = {
        list.size
    }

    implicit def get(i: Int) = {
        list(i)
    }

    def initPage() = DB.withConnection {
        implicit conn =>
            val sqlCount = "with A as (" + sql + ") select count(0) from A"
            total = SQL(sqlCount).as(scalar[Int].singleOpt).getOrElse(0)
            val sqlRow = "with A as (" + sql.toLowerCase.replaceFirst("select", "select top " + (limit * pageNumber)) + ") " +
                ", B as(select ROW_NUMBER() OVER (ORDER BY sticky desc) as rowid, A.* from A) " +
                "select  B.* from B WHERE rowid > {limit} * ({pageNumber}-1)"
            list = SQL(sqlRow).on('sort -> sort, 'limit -> limit, 'pageNumber -> pageNumber).as(sqlParser.*)
            pages = (total - 1) / limit + 1
    }
}
