/*
 * Copyright (C) 2015 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.spark.peak.utlis.log


import com.spark.peak.net.ApiException
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.nio.charset.Charset


/**
 * 项目名称:  MSHB
 * 类描述:
 * 创建人:    ICOGN
 * 创建时间:  2016/9/23 15:49
 * 修改人:    ICOGN
 * 修改时间:  2016/9/23 15:49
 * 备注:
 * 版本:
 */
class RequestFilterInterceptor : Interceptor {
    private val filter by lazy { mutableSetOf<String>() }
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val buffer = Buffer()
        val requestBody = request.body()
        requestBody?.writeTo(buffer)
        val charset = requestBody?.contentType()?.charset(UTF8) ?: UTF8
        val element = request.url().toString() + buffer.readString(charset)
        // : 2017/5/20 18:23 huoshulei 判断是否存在本次请求 不存则记录本次请求信息 否则过滤掉本次请求
        if (element in filter && (!element.contains("support/myBookLis") || !element.contains("netcourse/myNetCourse"))) {
            L.d("intercept:请求重复 $element")
            filter.forEach { L.d("intercept:请求队列 $it") }
            throw ApiException("忽略")
        }
        filter.add(element)
        try {
            val response = chain.proceed(request)
            // : 2017/5/20 18:25 huoshulei 请除本次请求信息
            filter.remove(element)
            return response
        } catch (e: Exception) {
            filter.remove(element)
            // : 2017/5/20 18:25 huoshulei 请除本次请求信息
            throw e
        }
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")
    }
}
