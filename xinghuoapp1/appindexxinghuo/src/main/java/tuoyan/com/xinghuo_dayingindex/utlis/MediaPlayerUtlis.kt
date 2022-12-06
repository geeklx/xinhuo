package tuoyan.com.xinghuo_dayingindex.utlis

import android.content.Context
import android.media.AudioManager
import io.reactivex.internal.disposables.ListCompositeDisposable
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.OnProgress
import tv.danmaku.ijk.media.exo2.IjkExo2MediaPlayer


/**
 * 创建者：
 * 时间：  2017/11/15.
 */

object MediaPlayerUtlis : OnProgress {
    private val presenter by lazy { BasePresenter(this) }
    override val disposables by lazy { ListCompositeDisposable() }
    override fun showProgress() {
    }

    override fun dismissProgress() {
    }

    override fun onError(message: String) {
    }

    private var mediaPlayer: IjkExo2MediaPlayer? = null

    fun start(
        context: Context, uri: String,
        onStart: () -> Unit,
        onCompletion: () -> Unit,
        onError: (String) -> Unit
    ) {
        start(context, uri, "4", onStart, onCompletion, onError)
    }

    fun start(
        context: Context, uri: String, sourceType: String = "",
        onStart: () -> Unit,
        onCompletion: () -> Unit,
        onError: (String) -> Unit
    ) {
        initPlayer(context, onStart, onCompletion, onError)
        presenter.getResourceInfo(uri, sourceType) {
            if (it.url.isNotBlank()) {
                mediaPlayer?.dataSource = it.url
                mediaPlayer?.prepareAsync()
            }
        }
    }

    /**
     * from 1:直接播放
     */
    fun directStart(
        context: Context, url: String, from: String = "",
        onStart: () -> Unit,
        onCompletion: () -> Unit,
        onError: (String) -> Unit
    ) {
        initPlayer(context, onStart, onCompletion, onError)
        if (from == "1") {
            mediaPlayer?.dataSource = url
            mediaPlayer?.prepareAsync()
        } else {
            presenter.getResourceInfo(url, "") {
                if (it.url.isNotBlank()) {
                    mediaPlayer?.dataSource = it.url
                    mediaPlayer?.prepareAsync()
                }
            }
        }
    }

    fun pause() {
        mediaPlayer?.pause()
    }

    private fun initPlayer(
        context: Context,
        onStart: () -> Unit,
        onCompletion: () -> Unit,
        onError: (String) -> Unit
    ) {
        if (mediaPlayer != null) release()
        mediaPlayer = IjkExo2MediaPlayer(context)
        mediaPlayer?.let { player ->
            player.setAudioStreamType(AudioManager.STREAM_MUSIC)
            //添加播放完成监听
            player.setOnCompletionListener { mp ->
                mp.stop()
                mp.release()
                mediaPlayer = null
                onCompletion()
            }
            player.setOnErrorListener { player, what, extra ->
//                if (what == 1) context.toast("未知错误：读音播放失败")
                player.release()
                mediaPlayer = null
                onError("")
                false
            }
            player.setOnPreparedListener {
                if (!it.isPlaying) {
                    onStart()
                    it.start()
                }
            }
        }
    }

    fun release() {
        clear()
        if (mediaPlayer != null) {
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

}
