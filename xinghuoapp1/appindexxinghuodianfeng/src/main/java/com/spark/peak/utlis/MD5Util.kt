package com.spark.peak.utlis

import java.security.MessageDigest

/**
 * 创建者： caodebo
 * 时间：  2017/3/7.
 */

object MD5Util {

    fun encrypt(plaintext: String): String {
        val hexDigits = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
        try {
            val btInput = plaintext.toByteArray()
            // 获得MD5摘要算法的 MessageDigest 对象
            val mdInst = MessageDigest.getInstance("MD5")
            // 使用指定的字节更新摘要
            mdInst.update(btInput)
            // 获得密文
            val md = mdInst.digest()
            // 把密文转换成十六进制的字符串形式
            val j = md.size
            val str = CharArray(j * 2)
            var k = 0
//            for (i in 0 until j) {
//                val byte0 = md[i]
//                str[k++] = hexDigits[byte0.toInt().ushr(4) and 0xf]
//                str[k++] = hexDigits[byte0.toInt() and 0xf]
//            }
            (0 until j).forEach {
                val byte0 = md[it]
                str[k++] = hexDigits[byte0.toInt().ushr(4) and 0xf]
                str[k++] = hexDigits[byte0.toInt() and 0xf]
            }
            return String(str)
        } catch (e: Exception) {
//            e.printStackTrace()
            return ""
        }

    }
}
