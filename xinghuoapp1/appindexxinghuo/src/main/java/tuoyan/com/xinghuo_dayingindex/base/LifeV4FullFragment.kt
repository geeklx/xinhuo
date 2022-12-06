package tuoyan.com.xinghuo_dayingindex.base

import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.gyf.immersionbar.ImmersionBar
import tuoyan.com.xinghuo_dayingindex.R


abstract class LifeV4FullFragment<out P : BasePresenter> : LifeV4Fragment<P>() {
    override fun configView(view: View?) {
        view?.let { initStatusBar(it) }
        super.configView(view)
    }

    private fun initStatusBar(viewGroup: View) {
        val view = viewGroup.findViewById<View>(R.id.top)
        val params = view.layoutParams as ConstraintLayout.LayoutParams
        params.height = ImmersionBar.getStatusBarHeight(this)
    }
}