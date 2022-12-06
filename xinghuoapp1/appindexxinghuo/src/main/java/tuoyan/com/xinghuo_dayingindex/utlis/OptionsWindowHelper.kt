//package tuoyan.com.xinghuo_daying.utlis
//
//import android.content.Context
//
//import com.google.gson.Gson
//import com.google.gson.reflect.TypeToken
//
//import java.io.BufferedReader
//import java.io.IOException
//import java.io.InputStreamReader
//
//import tuoyan.com.xinghuo_dayingindex.widegt.picker.CharacterPickerWindow
//import tuoyan.com.xinghuo_dayingindex.widegt.picker.Option
//
///**
// * 创建者：  * 地址选择器
// * 时间：  2017/7/13.
// */
//
//class OptionsWindowHelper private constructor() {
//
//    interface OnOptionsSelectListener {
//        fun onOptionsSelect(province: Picker?, city: Picker?, area: Picker?)
//    }
//
//    inner class Picker : Option {
//
//        /**
//         * fid : 130200
//         * code : 130201
//         * name : 市辖区
//         */
//
//        var fid: String? = null
//        override var code: String? = null
//        override var name: String? = null
//        override var list: List<Picker>? = null
//
//    }
//
//    companion object {
//
//        fun builder(context: Context, listener: OnOptionsSelectListener): CharacterPickerWindow {
//            //选项选择器
//            val mOptions = CharacterPickerWindow(context)
//            //初始化选项数据
//            var options: List<Picker>? = null
//            try {
//                val sb = StringBuilder()
//                val bf = BufferedReader(InputStreamReader(context.assets.open("phoneMobile.json")))
//                var line = bf.readLine()
//                while (line != null) {
//                    sb.append(line)
//                    line = bf.readLine()
//                }
//
//                options = Gson().fromJson(sb.toString(), object : TypeToken<List<Picker>>() {
//                }.type)
//            } catch (e: IOException) {
//                e.printStackTrace()
//            }
//
//            if (options != null) mOptions.setPicker(options)
//            //设置默认选中的三级项目
//            mOptions.setSelectOptions(0, 0, 0)
//            //监听确定选择按钮
//            val finalOptions = options
//            mOptions.setOnoptionsSelectListener { option1, option2, option3 ->
//
//                var province: Picker? = null
//                var city: Picker? = null
//                var area: Picker? = null
//                try {
//                    province = finalOptions?.get(option1)
//                    city = province?.list?.get(option2)
//                    area = city?.list?.get(option3)
//                } catch (e: Exception) {
//                } finally {
//                    listener.onOptionsSelect(province, city, area)
//                }
//            }
//            return mOptions
//        }
//    }
//
//}
