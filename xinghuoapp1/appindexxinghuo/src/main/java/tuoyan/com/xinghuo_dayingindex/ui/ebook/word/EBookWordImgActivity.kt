package tuoyan.com.xinghuo_dayingindex.ui.ebook.word
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.os.Build
import android.os.Handler
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_ebook_jian_read.fragment
import kotlinx.android.synthetic.main.activity_ebook_jian_read.img_catalog
import kotlinx.android.synthetic.main.activity_ebook_jian_read.toolbar
import kotlinx.android.synthetic.main.activity_ebook_word_img.*
import org.jetbrains.anko.toast
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeActivity
import tuoyan.com.xinghuo_dayingindex.base.EBookLifeFullActivity
import tuoyan.com.xinghuo_dayingindex.bean.EBook
import tuoyan.com.xinghuo_dayingindex.bean.EBookCatalog
import tuoyan.com.xinghuo_dayingindex.bean.EBookImg
import tuoyan.com.xinghuo_dayingindex.bean.VideoParam
import tuoyan.com.xinghuo_dayingindex.ui.bookstore.post.PostActivity
import tuoyan.com.xinghuo_dayingindex.ui.dialog.DDialog
import tuoyan.com.xinghuo_dayingindex.ui.dialog.EBookImgDialog
import tuoyan.com.xinghuo_dayingindex.ui.ebook.EBookPresenter
import tuoyan.com.xinghuo_dayingindex.ui.ebook.fragment.EBookWordCatalogFragment
import tuoyan.com.xinghuo_dayingindex.ui.ebook.word.adpter.EBookImgAdapter
import tuoyan.com.xinghuo_dayingindex.ui.netLesson.lesson.video.VideoActivity
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.FileUtils
import tuoyan.com.xinghuo_dayingindex.utlis.MediaPlayerUtlis
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import java.io.File

/**
 * 是否已经下载  已经下载走本地资源
 */
class EBookWordImgActivity : EBookLifeFullActivity<EBookPresenter>() {
    override val layoutResId: Int
        get() = R.layout.activity_ebook_word_img
    override val presenter: EBookPresenter
        get() = EBookPresenter(this)

    private val isOwn by lazy { intent.getStringExtra(IS_OWN) ?: "0" }
    private val catalogFragment by lazy { EBookWordCatalogFragment.newInstance(bookParam?.bookKey ?: "", isOwn) }
    private val layoutManager by lazy { LinearLayoutManager(this) }
    private var isUp = false;//是否提交记录
    private var loadingTag = 0
    private var loading = false
    private var currentPos = 0//播放本组  当前单词位置
    private val imgDataList by lazy { mutableListOf<EBookImg>() }
    private var isPlaying = false
    var isDown = false
    private val catalogKeyList by lazy { mutableListOf<String>() }
    private val imgMap by lazy { HashMap<String, List<EBookImg>>() }
    private val imgDialog by lazy {
        EBookImgDialog(this)
    }

    private val imgAdapter by lazy {
        EBookImgAdapter { clickType, item ->
            if (ctl_play.isSelected) {
                ctl_play.performClick()
            }
            when (clickType) {
                1 -> {
                    if (item.audioUrl.isNotBlank() && !isPlaying) {
                        playDelayed(item.audioUrl)
                    }
                }
                2 -> {
                    val videoParam = VideoParam()
                    videoParam.key = item.videoUrl
                    startActivity<VideoActivity>(VideoActivity.VIDEO_PARAM to videoParam, EBookLifeActivity.EBOOK_PARAM to bookParam)
                }
            }
        }
    }
    var bookDetail: EBook? = null
    private var dDialog: DDialog? = null

    override fun onPause() {
        super.onPause()
        MediaPlayerUtlis.pause()
        ctl_play.isSelected = false
        tv_play.text = "播放本组"
    }

    override fun configView() {
        super.configView()
        isDown = DownloadManager.isDownEBookWord(bookParam?.bookKey ?: "")
        initCatalog()
        rlv_img.layoutManager = layoutManager
        rlv_img.adapter = imgAdapter
        if (!SpUtil.showedEBookWord) {
            SpUtil.showedEBookWord = true
            imgDialog.show()
        }
        img_catalog.visibility = if (bookParam?.from == "1") View.GONE else View.VISIBLE
    }

    override fun handleEvent() {
        super.handleEvent()
        toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
        img_catalog.setOnClickListener {
            fragment.visibility = View.VISIBLE
            if (ctl_play.isSelected) {
                ctl_play.performClick()
            }
            bookParam?.let { params ->
                catalogFragment.currentCatalogKey(params.catalogKey ?: "")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            rlv_img.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                        if (!recyclerView.canScrollVertically(1)) {
                            if (!isUp) {
                                isUp = true
                                postRecord("0", "1")
                            }
                            if (isOwn == "1") {
                                loadingTag++
                                if (loadingTag >= 2 && !loading) {
                                    loading = true
                                    bookParam?.let { params ->
                                        if (isDown) {
                                            val index = catalogKeyList.indexOf(params.catalogKey)
                                            if (index >= 0 && index + 1 < catalogKeyList.size) {
                                                params.catalogKey = catalogKeyList[index + 1]
                                                getReadList()
                                            }
                                        } else {
                                            val index = catalogFragment.catalogKeys.indexOf(params.catalogKey)
                                            if (index >= 0 && index + 1 < catalogFragment.catalogKeys.size) {
                                                params.catalogKey = catalogFragment.catalogKeys[index + 1]
                                                getReadList()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    super.onScrollStateChanged(recyclerView, newState)
                }
            })
        }

        srl.setOnRefreshListener {
            loading = true
            bookParam?.let { params ->
                if (isDown) {
                    val index = catalogKeyList.indexOf(params.catalogKey)
                    if (index > 0 && index + 1 <= catalogKeyList.size) {
                        params.catalogKey = catalogKeyList[index - 1]
                        getReadList()
                    } else {
                        loading = false
                        srl.isRefreshing = false
                    }
                } else {
                    val index = catalogFragment.catalogKeys.indexOf(params.catalogKey)
                    if (index > 0 && index + 1 <= catalogFragment.catalogKeys.size) {
                        params.catalogKey = catalogFragment.catalogKeys[index - 1]
                        getReadList()
                    } else {
                        loading =false
                        srl.isRefreshing = false
                    }
                }
            }
        }
        ctl_play.setOnClickListener {
            ctl_play.isSelected = !ctl_play.isSelected
            tv_play.text = if (ctl_play.isSelected) "暂停音频" else "播放本组"
            if (ctl_play.isSelected) {
                sortPlay()
            } else {
                MediaPlayerUtlis.pause()
            }
        }
        rlv_img.setOnTouchListener { view, motionEvent ->
            if (ctl_play.isSelected) {
                ctl_play.performClick()
            }
            super.onTouchEvent(motionEvent)
        }
    }

    override fun initData() {
        super.initData()
        if (bookParam?.from != "1") {
            presenter.getEBookDetail(bookParam?.bookKey ?: "") { book ->
                bookDetail = book
                catalogFragment.setSize(book.fileSize)
            }
        }
        initDownRes {
            resetImgs()
        }
    }

    fun initDownRes(success: () -> Unit) {
        bookParam?.let { params ->
            if (isDown) {
                val file = File("${DownloadManager.downloadPathEN}/${params.bookKey ?: ""}/smart.json")
                if (file.exists()) {
                    FileUtils.readFileSB(file, "UTF-8", {
                        runOnUiThread {
                            toast("读取本地文件失败，请重新进入")
                        }
                    }) { jsonStr ->
                        runOnUiThread {
                            val list: List<EBookCatalog> = Gson().fromJson(jsonStr, object : TypeToken<List<EBookCatalog>>() {}.type)
                            formatList(list)
                            success()
                        }
                    }
                } else {
                    Toast.makeText(this, "文件不存在", Toast.LENGTH_SHORT).show()
                }
            } else {
                getReadListHttp()
            }
        }
    }

    fun getReadList() {
        bookParam?.let { params ->
            if (isDown) {
                imgDataList.clear()
                imgMap[params.catalogKey ?: ""]?.let { imgDataList.addAll(it) }
                resetImgs()
            } else {
                getReadListHttp()
            }
        }
    }

    private fun getReadListHttp() {
        bookParam?.let { params ->
            presenter.getReadList(params.catalogKey ?: "") {
                imgDataList.clear()
                imgDataList.addAll(it)
                resetImgs()
            }
        }
    }

    private fun resetImgs() {
        isUp = false
        loading = false
        loadingTag = 0
        currentPos = 0
        imgAdapter.setData(imgDataList)
        srl.isRefreshing = false
        if (imgDataList.isNotEmpty()) {
            Handler().postDelayed({
                layoutManager.scrollToPositionWithOffset(0, 0)
            }, 100)
            ctl_play.visibility = View.GONE
            kotlin.run breaking@{
                imgDataList.forEach {
                    if (it.audioUrl.isNotBlank()) {
                        ctl_play.visibility = View.VISIBLE
                        return@breaking
                    }
                }
            }
        }
    }

    private fun formatList(list: List<EBookCatalog>) {
        catalogKeyList.clear()
        imgMap.clear()
        val basePath = "${DownloadManager.downloadPathEN}/${bookParam?.bookKey ?: ""}/"
        list.forEach { pCatalog ->
            pCatalog.catalogList.forEach { cCatalog ->
                if (cCatalog.readList.isNotEmpty()) {
                    catalogKeyList.add(cCatalog.catalogKey)
                    cCatalog.readList.forEach { img ->
                        img.imgUrl = "$basePath${img.imgName}"
                        img.audioUrl = if (img.audioName.isNotBlank()) "$basePath${img.audioName}" else ""
                        img.videoUrl = img.videoResourceKey
                    }
                    imgMap[cCatalog.catalogKey] = cCatalog.readList
                }
            }
        }
        imgDataList.clear()
        imgMap[bookParam?.catalogKey ?: ""]?.let { imgDataList.addAll(it) }
    }

    private fun initCatalog() {
        if (bookParam?.from != "1") {
            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment, catalogFragment)
            transaction.show(catalogFragment).commit()
        }
    }

    fun goBuy() {
        val msg = "该资源为虚拟产品，购买后不可退款，${if ("长期" != bookDetail?.endTime) "有效期至${bookDetail?.endTime}，过期则失效不能使用，" else ""}是否确认购买？"
        dDialog = DDialog(this).setWidth(250).setGravity(Gravity.LEFT).setMessage(msg)
            .setNegativeButton("考虑一下") {
                dDialog?.dismiss()
            }.setPositiveButton("立即购买") {
                dDialog?.dismiss()
                startActivity<PostActivity>(PostActivity.URL to "ordersubmit?goodsKey=${bookParam?.bookKey ?: ""}&goodsNum=1&goodsType=7&isCart=0")
                super.onBackPressed()
            }
        dDialog?.show()
    }

    fun catalogClick() {
        fragment.visibility = View.GONE
    }

    private fun sortPlay() {
        if (currentPos < imgDataList.size) {
            val data = imgDataList[currentPos].audioUrl
            if (data.isNotBlank()) {
                playDelayed(data)
            } else if (ctl_play.isSelected) {
                currentPos++
                sortPlay()
            }
        } else {
            if (!isUp) {
                isUp = true
                postRecord("0", "1")
            }
            currentPos = 0
            ctl_play.isSelected = false
            tv_play.text = "播放本组"
        }
    }

    private fun playDelayed(uri: String) {
        if (ctl_play.isSelected) {
            layoutManager.scrollToPositionWithOffset(currentPos, 40)
        } else {
            isPlaying = true
        }
        Handler().postDelayed({
            musicPlay(uri)
        }, 1000)
    }

    private fun musicPlay(uri: String) {
        MediaPlayerUtlis.directStart(this, uri, if (isDown) "1" else "", {//onStart
        }, {//onCompletion
            if (ctl_play.isSelected) {
                currentPos++
                sortPlay()
            } else {
                isPlaying = false
            }
        }) {
            musicPlay(uri)
        }
    }

    companion object {
        const val IS_OWN = "IS_OWN"
        const val IS_DOWN = "IS_DOWN"
    }

    override fun onBackPressed() {
        if (fragment.visibility == View.VISIBLE) {
            catalogClick()
        } else {
            super.onBackPressed()
        }
    }
}