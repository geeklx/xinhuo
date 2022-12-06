package tuoyan.com.xinghuo_dayingindex.base

import android.os.Bundle
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.gyf.immersionbar.ImmersionBar
import tuoyan.com.xinghuo_dayingindex.R

/**
 * 继承次类  外层为ConstraintLayout
 */
abstract class LifeFullActivity<out P : BasePresenter> : LifeActivity<P>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        ImmersionBar.with(this).fullScreen(true)
            .statusBarColor(R.color.transparent)
            .statusBarDarkFont(true)
            .navigationBarColor(R.color.transparent)
            .init()
        fullScreen = true
        super.onCreate(savedInstanceState)
        initStatusBar()
    }

    private fun initStatusBar() {
        val view = findViewById<View>(R.id.top)
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this)
    }
}
