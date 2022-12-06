package tuoyan.com.xinghuo_dayingindex.ui._public

import android.os.Bundle
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_image.*
import org.jetbrains.anko.ctx
import tuoyan.com.xinghuo_dayingindex.R
import tuoyan.com.xinghuo_dayingindex.base.BasePresenter
import tuoyan.com.xinghuo_dayingindex.base.LifeActivity

class ImageActivity : LifeActivity<BasePresenter>() {
    override val presenter: BasePresenter
        get() = BasePresenter(this)
    override val layoutResId: Int
        get() = R.layout.activity_image

    override fun onCreate(savedInstanceState: Bundle?) {
        fullScreen = true
        super.onCreate(savedInstanceState)
    }

    val imageUrl by lazy { intent.getStringExtra(URL) }
    val imageName by lazy { intent.getStringExtra(NAME) }

    companion object {
        val URL = "imageUrl"
        val NAME = "imageName"

    }

    override fun configView() {
        super.configView()
        setSupportActionBar(tb_img_act)
        tv_title.text = imageName
        Glide.with(ctx).load(imageUrl).into(imageView)
    }

    override fun handleEvent() {
        super.handleEvent()
        tb_img_act.setNavigationOnClickListener { onBackPressed() }
    }
}
