package tuoyan.com.xinghuo_dayingindex.bean

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import java.io.Serializable

class Book {
    var isValidate: String = ""
    var isown: String = ""

    @SerializedName(value = "coverImg", alternate = ["coverUrl", "img"])
    var coverImg: String = ""
    var year: String = ""
    var name: String = ""
    var key: String = ""
    var showYear: Boolean = false
    var endTime: String = ""
    var type: String = ""
    var isEffect: String = ""
}

class BookDetail : Serializable {
    var usernum: String = ""
    var supportingKey: String = ""
    var isValidate: String = ""
    var isown: String = ""
    var img: String = ""
    var titile: String = ""
    var name: String = ""
    var gradeName: String = ""
    var yearStr: String = ""
    var gradeKey: String = ""
    var videoList = ArrayList<BookRes>()
    var hearingList = ArrayList<BookRes>()
    var exerciseList = ArrayList<BookRes>()
    var resourceList = ArrayList<BookRes>()
    var electronicPaperList = ArrayList<BookRes>()
    var wordCatalogList = ArrayList<BookRes>()
    var bookKey = ""
    var bookName = ""
}

class SensorBook() : Parcelable {
    var book_matching_id = ""//配套ID
    var book_matching_name = ""//图书配套名称
    var section = ""//学段

    constructor(parcel: Parcel) : this() {
        book_matching_id = parcel.readString() ?: ""
        book_matching_name = parcel.readString() ?: ""
        section = parcel.readString() ?: ""
    }

    override fun describeContents(): Int {
        return 0
    }

//    override fun writeToParcel(dest: Parcel?, flags: Int) {
//        TODO("Not yet implemented")
//    }

    override fun writeToParcel(p0: Parcel, p1: Int) {
        p0?.let {
            p0.writeString(book_matching_id)
            p0.writeString(book_matching_name)
            p0.writeString(section)
        }
    }

    companion object CREATOR : Parcelable.Creator<SensorBook> {
        override fun createFromParcel(parcel: Parcel): SensorBook {
            return SensorBook(parcel)
        }

        override fun newArray(size: Int): Array<SensorBook?> {
            return arrayOfNulls(size)
        }
    }
}

//配套联系type:1:试卷,2:试卷解析,3:视频,4:图片,5:图文,6:文档,7:音频,8:链接,10:测评,11:资讯
class BookRes : DownloadBean(), Serializable {
    var downloadFlag: String = ""
    var lrcurl: String = ""
    var lrcurl2: String = ""
    var lrcKey2: String = ""
    var lrcurl3: String = ""
    var playUrl: String = ""
    var count: String = ""
    var link: String = ""
    var isCollection: String = ""
    var content: String = ""
    var score: String = ""
    var createDate: String = ""
    var userPracticeKey: String = ""
    var field1: String = ""
    var field2: String = ""
    var field3: String = ""
    var field4: String = ""
    var field5: String = ""
    var field6: String = ""
    var catalogKey: String = ""
    var catalogName: String = ""
    fun isDownload() = FileUtils.isFileExists(DownloadManager.getFilePathWithKey(id, type)) && DownloadManager.isDown(id)
    fun islrcurl() = FileUtils.isFileExists(DownloadManager.getFilePath(lrcurl))
    fun islrcurl2() = FileUtils.isFileExists(DownloadManager.getFilePath(lrcurl2))
    fun islrcurl3() = FileUtils.isFileExists(DownloadManager.getFilePath(lrcurl3))
    var ebookLrc = ""
    var supportingKey = ""
}

class Subject : Serializable {
    var key: String? = null
    var name: String? = null
    var order: String? = null

//        fun img() = when (name) {
//                "语文" -> R.drawable.menu_chinese
//                "英语" -> R.drawable.menu_english
//                "物理" -> R.drawable.menu_physics
//                "生物" -> R.drawable.menu_shengwu
//                "历史" -> R.drawable.menu_lishi
//                "地理" -> R.drawable.menu_dili
//                "政治" -> R.drawable.menu_zhengzhi
//                "数学" -> R.drawable.menu_shuxue
//
//                "词汇" -> R.drawable.menu_cihui
//                "翻译" -> R.drawable.menu_fanyi
//                "口语" -> R.drawable.menu_kouyu
//                "听力" -> R.drawable.menu_tingli
//                "写作" -> R.drawable.menu_xiezuo
//                "阅读" -> R.drawable.menu_yuedu
//                "方法讲解" -> R.drawable.menu_ffjj
//                "音标" -> R.drawable.menu_yinbiao
//                "语法" -> R.drawable.menu_yufa
//
//                else -> R.drawable.menu_shuxue
//        }
//
//        fun background() = when (name) {
//                "语文" -> R.drawable.sub_yuwen
//                "英语" -> R.drawable.sub_yingyu
//                "物理" -> R.drawable.sub_wuli
//                "生物" -> R.drawable.sub_shengwu
//                "历史" -> R.drawable.sub_lishi
//                "地理" -> R.drawable.sub_dili
//                "政治" -> R.drawable.sub_zhengzhi
//                "数学" -> R.drawable.sub_shuxue
//                else -> R.drawable.sub_yuwen
//        }

}

class BasicItemBean : Serializable {
    var id: String? = null
    var title: String? = null
}

class OrderBean(
    var key: String,
    var payPrice: String,
    var postagePrice: String
)

class RankBean {
    var img: String = ""
    var name: String = ""
    var totalCount: String = ""
    var rightScore: String = ""
    var rowNo: String = ""
    var beatRate: String = ""
    var rankingList: ArrayList<RankingBean>? = null
}

class RankingBean {
    var userKey: String = ""
    var img: String = ""
    var name: String = ""
    var score: String = ""
    var sort: String = ""
}
