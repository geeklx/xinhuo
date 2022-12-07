package com.spark.peak.ui.home.sa

import com.sensorsdata.analytics.android.sdk.SensorsDataAPI
import com.spark.peak.utlis.SpUtil
import org.json.JSONObject

class SaEvent {
    companion object {
        /**
         * 巅峰训练-点击首页金刚区
         */
        fun bannerClick(name: String) {
            val property = JSONObject()
            property.put("function_name", name)
            baseClick(property, "df_click_function_entry")
        }

        /**
         * 巅峰训练-扫码按钮点击
         */
        fun scanClick() {
            val property = JSONObject()
            property.put("is_login", SpUtil.isLogin)
            baseClick(property, "df_click_code_scanning_button")
        }

        fun baseClick(property: JSONObject, eventName: String) {
            try {
                SensorsDataAPI.sharedInstance().track(eventName, property)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}