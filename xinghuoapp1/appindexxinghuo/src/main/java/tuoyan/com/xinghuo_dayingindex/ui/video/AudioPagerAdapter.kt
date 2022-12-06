//package tuoyan.com.xinghuo_daying.ui.video
//
//import android.graphics.Color
//import android.graphics.Paint
//import android.view.View
//import android.view.ViewGroup
//import androidx.viewpager.widget.PagerAdapter
//import com.hw.lrcviewlib.DefaultLrcRowsParser
//import com.hw.lrcviewlib.ILrcViewSeekListener
//import com.hw.lrcviewlib.LrcDataBuilder
//import com.hw.lrcviewlib.LrcView
//import tuoyan.com.xinghuo_dayingindex.utlis.DeviceUtil
//
//
///**
// * 创建者：
// * 时间：  2018/9/21.
// */
//class AudioPagerAdapter(val seekListener: ILrcViewSeekListener) : PagerAdapter() {
//    val listView by lazy { mutableMapOf<Int, LrcView>() }
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view == `object`
//    }
//
//    fun currentItem(position: Int): LrcView? {
//        if (position < 0) return null
//        if (position < listView.size) return listView[position]
//        return null
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val context = container.context
//        val textSize = DeviceUtil.dp2px(context, 16f).toInt()
//        val textSizeCH = DeviceUtil.dp2px(context, 14f).toInt()
//        var view = listView[position]
//        if (view == null) {
//            view = LrcView(context)
//            view.lrcSetting
//                .setTimeTextSize(textSize)//时间字体大小
//                .setSelectLineTextSize(textSize)//选中线大小
//                .setNormalRowTextSize(textSize)//正常行字体大小
//                .setHeightLightRowTextSize(textSize)//高亮行字体大小
//                .setTrySelectRowTextSize(textSize)//尝试选中行字体大小
//                .setChRowTextSize(textSizeCH)//尝试选中行字体大小
//                .setSelectLineColor(Color.parseColor("#008AFF"))//选中线颜色
//                .setHeightRowColor(Color.parseColor("#008AFF"))//高亮字体颜色
//                .setTimeTextColor(Color.parseColor("#008AFF"))//时间字体颜色
//                .setTrySelectRowColor(Color.parseColor("#AA008AFF"))//尝试选中字体颜色
//                .setNormalRowColor(Color.parseColor("#AFB3BF")).align = Paint.Align.CENTER
//
//            view.commitLrcSettings()
//            view.setNoDataMessage("")
//            view.setLrcViewSeekListener(seekListener)
//            listView[position] = view
//        }
//        container.addView(view)
//        return view
//    }
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        if (`object` is View)
//            container.removeView(`object`)
//    }
//
//    override fun getCount(): Int {
//        //    2020年7月9日 只展示英语字幕
//        return 1
//    }
//
//    override fun getPageTitle(position: Int): CharSequence? {
//        return "英文"
//        //    2020年7月9日 只展示英语字幕
////        return when (position) {
////            0 -> "英文"
////            1 -> "中英文"
////            else -> "中文"
////        }
//    }
//
//    fun setLrc(lrc: String?) {
//        val lrcRows = LrcDataBuilder().Build(lrc, DefaultLrcRowsParser())
//        listView.forEach {
//            if (lrcRows == null) {
//                it.value.setNoDataMessage("暂无字幕")
//            } else
//                it.value.setLrcData(lrcRows)
//        }
//    }
//}