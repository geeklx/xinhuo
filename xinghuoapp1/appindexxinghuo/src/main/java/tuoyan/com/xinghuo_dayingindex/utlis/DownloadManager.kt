@file:Suppress("UNUSED_EXPRESSION")

package tuoyan.com.xinghuo_dayingindex.utlis

import android.os.Environment
import com.geek.libutils.app.BaseApp
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloader
import com.liulishuo.filedownloader.model.FileDownloadStatus
import io.realm.RealmList
import io.realm.RealmResults
import tuoyan.com.xinghuo_dayingindex.DOWNLOAD_PATH
import tuoyan.com.xinghuo_dayingindex.MyApp
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DEFAULT
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DONE
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_DOWNLOADING
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.STATE_PAUSED
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_AUDIO
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_PDF
import tuoyan.com.xinghuo_dayingindex.bean.DownloadBean.Companion.TYPE_VIDEO
import tuoyan.com.xinghuo_dayingindex.encrypt.FileEnDecryptManager
import tuoyan.com.xinghuo_dayingindex.realm.RealmUtil
import tuoyan.com.xinghuo_dayingindex.realm.bean.Group
import tuoyan.com.xinghuo_dayingindex.realm.bean.Resource
import tuoyan.com.xinghuo_dayingindex.realm.create
import tuoyan.com.xinghuo_dayingindex.realm.delete
import java.io.File

/**
 * Created by  on 2018/5/11.
 */
object DownloadManager {

    val downloadPath = File(Environment.getExternalStorageDirectory().path, "$DOWNLOAD_PATH/").path
    val downloadPathEN = FileUtils.getDownLoadPath(BaseApp.get())
    val taskList = ArrayList<BaseDownloadTask>()


    /**
     * 获取文件下载路径
     * 根据下载url命名
     */
    @Deprecated("除歌词、在线升级外，其他 下载 或 读取 场景用 getFilePathWithKey 代替")
    fun getFilePath(url: String): String {
        return if (url.isEmpty() || !url.contains("/")) "" else {
            var i = url.lastIndexOf("/")
            downloadPath + "/" + url.substring(i)
        }
    }

    /**
     * 获取文件下载路径
     * 根据下载url命名
     */
    fun getExternalFilePath(url: String): String {
        return if (url.isEmpty() || !url.contains("/")) {
            ""
        } else {
            val i = url.lastIndexOf("/")
            downloadPathEN + "/" + url.substring(i)
        }
    }

    /**
     * 通过资源key 和 类型 拼接下载文件名
     */
    fun getFilePathWithKey(key: String, type: String): String {
        return if (key.isNullOrEmpty() || type.isNullOrEmpty()) {
            ""
        } else {
            when (type) {
                TYPE_VIDEO -> "$downloadPath/$key.mp4"
                TYPE_PDF -> "$downloadPath/$key.pdf"
                TYPE_AUDIO -> "$downloadPath/$key.mp3"
                else -> ""
            }
        }
    }

    /**
     * 通过资源key 和 类型 拼接下载文件名
     */
    fun getFilePathWithKey(res: DownloadBean): String {
        if ("2" == res.liveSource) {
            //1:欢拓；2：CC
            return "$downloadPath/${res.id}.ccr"
        } else if ("1" != res.liveSource) {
            return when (res.type) {
                TYPE_VIDEO -> "$downloadPath/${res.id}.mp4"
                TYPE_PDF -> "$downloadPath/${res.id}.pdf"
                TYPE_AUDIO -> "$downloadPath/${res.id}.mp3"
                else -> ""
            }
        }
        return ""
    }

    /**
     * 通过直播课Key 获取 直播课下载路径
     */
    fun getHTFilePath(key: String) = if (key.isNotEmpty()) "${DownloadManager.downloadPath}/$key" else ""

//    /**
//     * 获取直播回放下载地址
//     */
//    fun getLiveFilePath(url: String) : String{
//        return if (url.isEmpty()) "" else {
//            var i = url.lastIndexOf("/")
//            downloadPath + "/" + url.substring(i)
//        }
//    }

    /**
     * 获取当前正在运行中的下载任务
     */
    fun getRunTask(key: String): BaseDownloadTask? {
        var task: BaseDownloadTask? = null
        taskList.forEach {
            var resId = it.tag as String?
            if (resId == key) {
                task = it
                return task
            }
        }
        return task
    }


    /**
     * 获取当前进行中的任务的下载状态
     */
    fun getDownloadState(id: Int, res: DownloadBean): Int {
        return when (FileDownloader.getImpl().getStatus(id, getFilePathWithKey(res.id, res.type))) {
            FileDownloadStatus.completed -> STATE_DONE
            FileDownloadStatus.blockComplete -> STATE_DONE
            FileDownloadStatus.INVALID_STATUS -> STATE_DEFAULT
            FileDownloadStatus.paused -> STATE_PAUSED
            FileDownloadStatus.progress -> STATE_DOWNLOADING
            else -> STATE_DEFAULT
        }
    }

    fun saveEBookWord(name: String, key: String) {
        val realm = RealmUtil.instant()
        var group = realm.where(Group::class.java).equalTo("key", key).findFirst()
        if (group == null) {
            group = Group()
            group.userId = SpUtil.user.userId ?: ""
            group.key = key
            group.name = name
            group.type = "EBookWord"
            realm.create(group)
        }
    }

    fun getEBookWord(): RealmResults<Group> {
        val realm = RealmUtil.instant()
        val group = realm.where(Group::class.java).equalTo("type", "EBookWord").findAll()
        return group
    }

    fun isDownEBookWord(key: String): Boolean {
        val realm = RealmUtil.instant()
        val group = realm.where(Group::class.java).equalTo("key", key).findFirst()
        val file = File("$downloadPathEN/$key")
        return group != null && file.exists()
    }

    fun deleteEBookWord(key: String) {
        val realm = RealmUtil.instant()
        realm.delete(Group::class.java, "key", key)
    }

    /**
     * 下载完成后，存储下载记录
     * 下载未完成的 & 正在下载的，由 taskList 负责记录
     */
    fun saveDownloadInfo(groupName: String, groupKey: String, type: String, res: DownloadBean) {
        val realm = RealmUtil.instant()
        var filePath = getFilePathWithKey(res)
        if (filePath.isNotEmpty() && res.liveSource != "2" && res.type == TYPE_VIDEO) {//TODO 资源类型为视频时，启动文件加密线程
            if (!FileEnDecryptManager.getInstance().isEncrypt(filePath)) {
                Thread {
                    run {
                        FileEnDecryptManager.getInstance().encryptFile(filePath)
                    }
                }.start()
            }
        }
        if (type == "图书") {
            var resource = realm.where(Resource::class.java).equalTo("name", res.name).findFirst()
            if (resource == null) {
                resource = Resource()
                resource.key = res.id
                resource.name = res.name
                resource.path = filePath
                resource.type = res.type
                resource.from = type
                resource.duration = res.duration
                resource.url = res.downUrl
                resource.lrcUrls = res.lrcUrls
                realm.create(resource)
            }
        } else if (type == "网课") {
            var groupStr = if (groupName == "") "NO TITLE" else groupName //TODO 若传值来的资源名为空，则统一替换为 NO TITLE
            var group = realm.where(Group::class.java).equalTo("name", groupStr).equalTo("key", groupKey).findFirst()
            if (group == null) {
                group = Group()
                group.userId = SpUtil.user.userId ?: ""
                group.key = groupKey
                group.name = groupStr
                group.type = type
                realm.create(group)
            }
            group = realm.where(Group::class.java).equalTo("name", groupStr).equalTo("key", groupKey).findFirst()
            realm.beginTransaction()

            val resource = Resource()
            resource.key = res.id
            resource.name = res.name
            resource.path = filePath
            resource.type = res.type
            //网课类型
            resource.from = res.liveType
            resource.duration = res.duration
            resource.url = res.downUrl
            resource.lrcUrls = res.lrcUrls
            resource.liveType = res.liveType
            resource.liveSource = res.liveSource
            if (res.liveType == "1" && "1" == res.liveSource) {
                resource.liveKey = res.liveKey
                resource.liveToken = res.liveToken
                resource.path = "$downloadPath/${res.liveKey}"
            }
            group?.resList!!.add(resource)
            realm.commitTransaction()
        }
    }


    /**
     * 获取某网课下 已完成下载的列表
     */
    fun getDownloadedListByGroupName(groupName: String, key: String): RealmList<Resource> {
        val realm = RealmUtil.instant()
        var groupStr = if (groupName == "") "NO TITLE" else groupName //TODO 若传值来的资源名为空，则统一替换为 NO TITLE

        var group = realm.where(Group::class.java).equalTo("name", groupStr).equalTo("key", key).findFirst()
        if (group == null) {
            return RealmList()
        } else {
            return group.resList ?: RealmList()
        }
    }

    /**
     * 获取某图书 下 已完成下载的列表
     */
    fun getDownloadedListByResName(resName: String, url: String): RealmResults<Resource> {
        val realm = RealmUtil.instant()

        var resource = realm.where(Resource::class.java).equalTo("name", resName).equalTo("url", url).findAll()
        return resource
    }

    /**
     * 获取某图书 下 已完成下载的列表
     */
    fun getDownById(id: String): Resource? {
        val realm = RealmUtil.instant()
        return realm.where(Resource::class.java).equalTo("key", id).findFirst()
    }

    fun isDown(id: String): Boolean {
        return getDownById(id) != null
    }

    /**
     * 获取全部已完成下载的 网课 列表
     */
    fun getDownloadedGroupAll(): RealmResults<Group> {
        val realm = RealmUtil.instant()
        var groupList = realm.where(Group::class.java).findAll()
        return groupList
    }

    /**
     * realm 升级到3版本时需要添加默认urerid 把原来用户下载的数据绑定到当前用户下
     */
    fun updateDownloadedGroup() {
        val realm = RealmUtil.instant()
        realm.executeTransaction {
            val groupList = it.where(Group::class.java).findAll()
            groupList.forEach { group ->
                if (group.userId.isNullOrEmpty()) {
                    group.userId = SpUtil.user.userId ?: ""
                }
            }
        }
    }

    /**
     * 获取对应用户全部已完成下载的 网课 列表
     */
    fun getDownloadedGroupAll(userId: String): RealmResults<Group> {
        val realm = RealmUtil.instant()
        //realm 升级到3版本时需要添加默认urerid 把原来用户下载的数据绑定到当前用户下
        realm.executeTransaction {
            val groupList = it.where(Group::class.java).findAll()
            groupList.forEach { group ->
                if (group.userId.isNullOrEmpty()) {
                    group.userId = SpUtil.user.userId ?: ""
                }
            }
        }
        val groupList = realm.where(Group::class.java).equalTo("userId", userId).equalTo("type", "网课").findAll()
        return groupList
    }

    /**
     * 获取全部已完成下载的 图书配套资源 列表
     */
    fun getDownloadedResAll(): RealmResults<Resource> {
        val realm = RealmUtil.instant()
        var resList = realm.where(Resource::class.java).equalTo("from", "图书").findAll()
        return resList
    }

    /**
     * 获取某个资源下载信息
     */
    fun getDownloadedRes(key: String): Resource? {
        val realm = RealmUtil.instant()
        return realm.where(Resource::class.java).equalTo("key", key).findFirst()
    }

}