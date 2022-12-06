package tuoyan.com.xinghuo_dayingindex.ui.mine.offline
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.realm.RealmList
import org.jetbrains.anko.matchParent
import org.jetbrains.anko.recyclerview.v7.recyclerView
import org.jetbrains.anko.support.v4.ctx
import org.jetbrains.anko.support.v4.startActivityForResult
import org.jetbrains.anko.support.v4.toast
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeV4Fragment
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.realm.RealmUtil
import tuoyan.com.xinghuo_dayingindex.realm.bean.Group
import tuoyan.com.xinghuo_dayingindex.realm.bean.Resource
import tuoyan.com.xinghuo_dayingindex.realm.delete
import tuoyan.com.xinghuo_dayingindex.utlis.DownloadManager
import tuoyan.com.xinghuo_dayingindex.utlis.NetWorkUtils
import tuoyan.com.xinghuo_dayingindex.utlis.SpUtil
import kotlin.properties.Delegates
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.support.v4.startActivity
class OLessonFragment : LifeV4Fragment<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = 0
    private var rv by Delegates.notNull<RecyclerView>()
    var currentIndex = -1 //TODO 记录当前点击的目录索引

    override fun initView(): View? {
        rv = ctx.recyclerView {
            lparams(matchParent, matchParent)
            layoutManager = LinearLayoutManager(ctx)
        }
        return rv
    }

    private val adapter by lazy {
        OfflineAdapter { position, data ->
            if (NetWorkUtils.isNetWorkReachable()) {//当前网络已连接，则判断网课是否有效
                presenter.isValidOfNetcourse(data.key) {
                    if (it) {
                        val dataList = ArrayList<DownloadBean>() //TODO RealmList 不能当传值用
                        data.resList!!.forEach { resource ->
                            val res = DownloadBean()
                            res.id = resource.key
                            res.name = resource.name
                            res.type = resource.type
                            res.path = resource.path
                            res.form = resource.from ?: ""
                            res.duration = resource.duration
                            res.lrcUrls = resource.lrcUrls
                            res.liveSource = resource.liveSource
                            res.parentKey = data.key
                            res.parentName = data.name
                            if (resource.liveType == "1") {
                                //TODO 直播回放时多处理三个值
                                res.liveType = resource.liveType
                                res.liveKey = resource.liveKey
                                res.liveToken = resource.liveToken
                            }
                            dataList.add(res)
                        }
                        currentIndex = position
                        startActivityForResult<OfflineResActivity>(99, OfflineResActivity.DATA to dataList,
                                OfflineResActivity.NAME to data.name)
                    } else {
                        toast("该网课已失效")
                        val realm = RealmUtil.instant()
                        data.resList?.let {
                            deleteLessonRes(it)
                        }
                        realm.delete(Group::class.java, "key", data.key)
                    }
                }
            } else {
                val dataList = ArrayList<DownloadBean>() //TODO RealmList 不能当传值用
                data.resList!!.forEach { resource ->
                    val res = DownloadBean()
                    res.id = resource.key
                    res.name = resource.name
                    res.type = resource.type
                    res.path = resource.path
                    res.form = resource.from ?: ""
                    res.duration = resource.duration
                    res.lrcUrls = resource.lrcUrls
                    res.liveSource = resource.liveSource
                    res.parentKey = data.key
                    res.parentName = data.name
                    if (resource.liveType == "1") {
                        //TODO 直播回放时多处理三个值
                        res.liveType = resource.liveType
                        res.liveKey = resource.liveKey
                        res.liveToken = resource.liveToken
                    }
                    dataList.add(res)
                }
                currentIndex = position
                startActivityForResult<OfflineResActivity>(99, OfflineResActivity.DATA to dataList,
                        OfflineResActivity.NAME to data.name)
            }
        }
    }

    override fun initData() {
//        var groupList = DownloadManager.getDownloadedGroupAll()
        val user = SpUtil.user
        val groupList = DownloadManager.getDownloadedGroupAll(user.userId ?: "")
        adapter.setData(groupList)
        rv.adapter = adapter
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == 100) {
            val key = adapter.getData()[currentIndex].key
            val realm = RealmUtil.instant()
            realm.delete(Group::class.java, "key", key)
            deleteItem()
        }
    }

    fun deleteItem() {
        adapter.remove(currentIndex)
    }

    fun deleteLessonRes(list: RealmList<Resource>) {
//        list.forEach {
//            try {
//                PlaybackDownloader.getInstance().deleteDownload(it.liveKey)
//                FileUtils.deleteFile(it.path)
//            } catch (e: Exception) {
//            }
//        }
    }
}