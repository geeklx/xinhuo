package tuoyan.com.xinghuo_dayingindex.ui.cc

import com.google.gson.Gson
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import org.json.JSONException
import org.json.JSONObject
import tuoyan.com.xinghuo_dayingindex.bean.SensorsData

/**
 * Created by Zzz on 2021/3/31
 * Email:
 */
object SaUtil {
    @Throws(JSONException::class)
    private fun saProperty(data: SensorsData): JSONObject {
        return JSONObject(Gson().toJson(data))
    }

    fun saStartView(data: SensorsData) {
        try {
            val property = saProperty(data)
            SensorsDataAPI.sharedInstance().trackTimerStart("finish_watch_course_video")
            SensorsDataAPI.sharedInstance().track("start_watch_course_video", property)
        } catch (e: Exception) {
        }
    }

    fun saFinishView(data: SensorsData) {
        try {
            val property = saProperty(data)
            SensorsDataAPI.sharedInstance().trackTimerEnd("finish_watch_course_video", property)
        } catch (e: Exception) {
        }
    }

    fun saOption(data: SensorsData, btnName: String?) {
        try {
            val property = saProperty(data)
            when (btnName) {
                "提问", "聊天", "发送聊天", "查看公告", "收起推荐课程", "展开推荐课程", "点击推荐课程" -> property.put("operation_type", "学习互动")
                else -> property.put("operation_type", "播放调节")
            }
            property.put("button_name", btnName)
            SensorsDataAPI.sharedInstance().trackTimerEnd("watch_click", property)
        } catch (e: Exception) {
        }
    }

    fun saRecommend(data: SensorsData, time: String) {
        try {
            val property = saProperty(data)
            property.put("recommend_time", time)
            SensorsDataAPI.sharedInstance().track("course_video_recommend", property)
        } catch (e: Exception) {
        }
    }

    fun saNotice(data: SensorsData, content: String) {
        try {
            val property = saProperty(data);
            property.put("broad_content", content);
            SensorsDataAPI.sharedInstance().track("course_live_broad", property);
        } catch (e: Exception) {
        }
    }

    fun saDotMinute(data: SensorsData, time: Int, seekTime: Int, speed: String) {
        try {
            val property = saProperty(data)
            property.put("dot_minute", "$time")
            property.put("seek_time", "$seekTime")
            property.put("course_speed", speed)
            SensorsDataAPI.sharedInstance().track("minute_course_video", property)
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
    }
}