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


import okhttp3.Headers
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.EOFException
import java.io.IOException
import java.nio.charset.Charset
import java.nio.charset.UnsupportedCharsetException


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
class HttpLoggingInterceptor : Interceptor {
    enum class Level {
        NONE,
        HEADERS,
        BODY
    }

    @Volatile
    private var level = Level.NONE

    fun setLevel(level: Level): HttpLoggingInterceptor {
        this.level = level
        return this
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (level == Level.NONE) {
            return chain.proceed(request)
        }
        val logBody = level == Level.BODY
        val logHeaders = logBody || level == Level.HEADERS
        val requestBody = request.body()
        val hasRequestBody = requestBody != null
        if (logHeaders) {
            if (!logBody || !hasRequestBody) {
            } else if (bodyEncoded(request.headers())) {
            } else {
                val buffer = Buffer()
                requestBody?.writeTo(buffer)
            }
        }
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
            L.d("网络连接失败", "<-- 网络连接失败: " + e)
            throw e
        }
        val responseBody = response.body()
        val contentLength = responseBody?.contentLength()
        if (logHeaders) {
            if (!logBody) {
            } else if (bodyEncoded(response.headers())) {
            } else {
                val source = responseBody?.source()
                source?.request(Long.MAX_VALUE) // Buffer the entire body.
                val buffer = source?.buffer() ?: Buffer()
                var charset = UTF8
                val contentType = responseBody?.contentType()
                if (contentType != null) {
                    try {
                        charset = contentType.charset(UTF8)
                    } catch (e: UnsupportedCharsetException) {
                        L.d("响应体解码失败", "不能解码的响应体.<-- 网络请求结束")
                        return response
                    }
                }
                if (!isPlaintext(buffer)) {
                    return response
                }
                if (contentLength != 0L) {
                    "响应体" + buffer.clone().readString(charset)
                }
            }
        }
        return response
    }

    private fun bodyEncoded(headers: Headers): Boolean {
        val contentEncoding = headers.get("Content-Encoding")
        return contentEncoding != null && !contentEncoding.equals("identity", ignoreCase = true)
    }

    companion object {
        private val UTF8 = Charset.forName("UTF-8")

        /**
         * 是否加密
         */
        private fun isPlaintext(buffer: Buffer): Boolean {
            try {
                val prefix = Buffer()
                val byteCount = if (buffer.size < 64) buffer.size else 64
                buffer.copyTo(prefix, 0, byteCount)
                for (i in 0..15) {
                    if (prefix.exhausted()) {
                        break
                    }
                    val codePoint = prefix.readUtf8CodePoint()
                    if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint)) {
                        return false
                    }
                }
                return true
            } catch (e: EOFException) {
                return false // Truncated UTF-8 sequence.
            }
        }
    }
}
