package com.spark.peak.utlis

import android.text.TextUtils
import com.spark.peak.utlis.ConstUtils.Companion.PASSWORD

import java.util.regex.Pattern

import com.spark.peak.utlis.ConstUtils.Companion.REGEX_CHZ
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_DATE
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_EMAIL
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_IDCARD15
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_IDCARD18
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_IP
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_MOBILE_EXACT
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_MOBILE_SIMPLE
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_TEL
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_URL
import com.spark.peak.utlis.ConstUtils.Companion.REGEX_USERNAME


/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/2
 * desc  : 正则相关的工具类
</pre> *
 */
class RegularUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't fuck me...")
    }

    companion object {

        /**
         * If u want more please visit http://toutiao.com/i6231678548520731137/
         */

        /**
         * 验证手机号（简单）

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isMobileSimple(string: String): Boolean {
            return isMatch(REGEX_MOBILE_SIMPLE, string)
//            return isMatch(REGEX_MOBILE_EXACT, string)
        }

        /**
         * 验证手机号（精确）

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isMobileExact(string: String): Boolean {
            return isMatch(REGEX_MOBILE_EXACT, string)
        }

        /**
         * 验证电话号码

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isTel(string: String): Boolean {
            return isMatch(REGEX_TEL, string)
        }

        /**
         * 验证身份证号码15位

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isIDCard15(string: String): Boolean {
            return isMatch(REGEX_IDCARD15, string)
        }

        /**
         * 验证身份证号码18位

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isIDCard18(string: String): Boolean {
            return isMatch(REGEX_IDCARD18, string)
        }

        /**
         * 验证邮箱

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isEmail(string: String): Boolean {
            return isMatch(REGEX_EMAIL, string)
        }

        /**
         * 验证URL

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isURL(string: String): Boolean {
            return isMatch(REGEX_URL, string)
        }

        /**
         * 验证汉字

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isChz(string: String): Boolean {
            return isMatch(REGEX_CHZ, string)
        }

        /**
         * 验证用户名
         *
         * 取值范围为a-z,A-Z,0-9,"_",汉字，不能以"_"结尾,用户名必须是6-20位

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isUsername(string: String): Boolean {
            return isMatch(REGEX_USERNAME, string)
        }

        /**
         * 验证yyyy-MM-dd格式的日期校验，已考虑平闰年

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isDate(string: String): Boolean {
            return isMatch(REGEX_DATE, string)
        }

        /**
         * 验证IP地址

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isIP(string: String): Boolean {
            return isMatch(REGEX_IP, string)
        }

        /**
         * 验证密码

         * @param string 待验证文本
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isPWD(string: String): Boolean {
            return isMatch(PASSWORD, string)
        }

        /**
         * string是否匹配regex

         * @param regex  正则表达式字符串
         * *
         * @param string 要匹配的字符串
         * *
         * @return `true`: 匹配<br></br>`false`: 不匹配
         */
        fun isMatch(regex: String, string: String): Boolean {
            return !TextUtils.isEmpty(string) && Pattern.matches(regex, string)
        }
    }
}