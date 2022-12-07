package com.spark.peak.net

import com.google.gson.annotations.SerializedName

/**
 * 创建者： 霍述雷
 * 时间：  2018/5/15.
 */
/**
 * code : 200
 * results : {"ret":100,"msg":"成功","totalCount":80,"list":[{"property1":"value1","property2":"value2","property3":"value3"},{"property1":"value1","property2":"value2","property3":"value3"},{"property1":"value1","property2":"value2","property3":"value3"}]}
 */
class DataBase<out T>(val results: Results<T>,
                      val code: Int = 0)

/**
 * ret : 100
 * msg : 成功
 * totalCount : 80
 * list : [{"property1":"value1","property2":"value2","property3":"value3"},{"property1":"value1","property2":"value2","property3":"value3"},{"property1":"value1","property2":"value2","property3":"value3"}]
 */
class Results<out T>(val ret: Int = 0,
                     val totalCount: Int = 0,
                     @SerializedName(value = "body", alternate = ["list"])
                     val body: T,
                     val msg: String)

class LL<out T>(
        val levelList: T,
        val isshow: String? = null,
        val isfinish: String? = null,
        val successwords: String? = null
)

class PL<out T>(
        val paperList: T
)