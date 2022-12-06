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
package tuoyan.com.xinghuo_dayingindex.utlis.log


import android.util.Log
import com.bokecc.livemodule.BuildConfig
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

//    enum class Level {
//
//        NONE,
//
//        HEADERS,
//
//        BODY
//    }
//
//
//    @Volatile
//    private var level = Level.NONE


//    fun setLevel(level: Level): HttpLoggingInterceptor {
//        this.level = level
//        return this
//    }


    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        Log.d("HttpLoggingInterceptor", "intercept: "+request.url())
        if (!BuildConfig.DEBUG) {
            return chain.proceed(request)
        }

//        val logBody = level == Level.BODY
//        val logHeaders = logBody || level == Level.HEADERS

//        val requestBody = request.body()
//        val hasRequestBody = requestBody != null

//        val connection = chain.connection()
//        val protocol = if (connection != null) connection.protocol() else Protocol.HTTP_1_1
//        var requestStartMessage = "--> " + request.method() + ' ' + request.url() + ' ' + protocol
//        if (!logHeaders && hasRequestBody) {
//            requestStartMessage += " (" + requestBody?.contentLength() + "-请求体长度)"
//        }
//        L.d("请求地址", requestStartMessage)
//        if (logHeaders) {
//            if (hasRequestBody) {
        // Request body headers are only present when installed as a network interceptor. Force
        // them to be included (when available) so there values are known.
//                if (requestBody?.contentType() != null) {
//                    L.d("内容格式", "Content-Type: " + requestBody.contentType())
//                }
//                if (requestBody?.contentLength() != -1L) {
//                    L.d("请求体长度", "Content-Length: " + requestBody?.contentLength())
//                }
//            }

//            val headers = request.headers()
//            var i = 0
//            val count = headers.size()
//            while (i < count) {
//                val name = headers.name(i)
//                if (!"Content-Type".equals(name, ignoreCase = true) && !"Content-Length".equals(name, ignoreCase = true)) {
//                    L.d("请求头", name + ": " + headers.value(i))
//                }
//                i++
//            }

//            if (!logBody || !hasRequestBody) {
//                L.d("空请求体", "--> 请求结束 " + request.method())
//            } else if (bodyEncoded(request.headers())) {
//                L.d("未知", "--> 请求结束 " + request.method() + " (encoded body omitted)")
//            } else {
//                val buffer = Buffer()
//                requestBody?.writeTo(buffer)

//                var charset = UTF8
//                val contentType = requestBody?.contentType()
//                if (contentType != null) {
//                    charset = contentType.charset(UTF8)
//                }

//                if (isPlaintext(buffer)) {
        //                    log.log(buffer.readString(charset));
//                    L.json("请求体", buffer.readString(charset))
//                    L.d(
//                        "请求结束", "--> 请求结束 " + request.method()
//                                + " (" + requestBody?.contentLength() + "-字节 body)"
//                    )
//                } else {
//                    L.d(
//                        "请求结束", "--> 请求结束 " + request.method() + " (二进制 "
//                                + requestBody?.contentLength() + "-字节 body omitted)"
//                    )
//                }
//            }
//        }

//        val startNs = System.nanoTime()
        val response: Response
        try {
            response = chain.proceed(request)
        } catch (e: Exception) {
//            L.d("网络连接失败", "<-- 网络连接失败: " + e)
            throw e
        }

//        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)

        val responseBody = response.body()
//        val contentLength = responseBody?.contentLength()
//        val bodySize = if (contentLength != -1L) contentLength.toString() + "-字节" else "未知长度"
//        L.d(
//            "请求结束", "<-- " + response.code() + ' ' + response.message() + ' '
//                    + response.request().url() + " (" + tookMs + "ms" + (if (!logHeaders) ",$bodySize body" else "") + ')'
//        )

//        if (logHeaders) {
//            val headers = response.headers()
//            var i = 0
//            val count = headers.size()
//            while (i < count) {
//                L.d("响应信息", headers.name(i) + ": " + headers.value(i))
//                i++
//            }

//            if (!logBody) {
//                L.d("空响应体", "<-- 网络请求结束")
//            } else if (bodyEncoded(response.headers)) {
//                L.d("未知", "<-- 网络请求结束 (encoded body omitted)")
//            } else {
        val source = responseBody?.source()
        source?.request(Long.MAX_VALUE) // Buffer the entire body.
        val buffer = source?.buffer ?: Buffer()

        var charset = UTF8
        val contentType = responseBody?.contentType()
        if (contentType != null) {
            try {
                charset = contentType.charset(UTF8)
            } catch (e: UnsupportedCharsetException) {
//                        L.d("响应体解码失败", "不能解码的响应体.<-- 网络请求结束")
                return response
            }

        }

        if (!isPlaintext(buffer)) {
//                    L.d("响应体加密", "<-- 网络请求结束 (二进制 " + buffer.size() + "-字节 body omitted)")
            return response
        }

//                if (contentLength != 0L) {
//                    L.d("url ", "<-- ////////////////////////////////////////" + response.request().url())
//                    L.json("响应体", buffer.clone().readString(charset))
//                }
//        "請求數據======" + buffer.clone().readString(charset)
        Log.d("HttpLoggingInterceptor", "intercept: "+buffer.clone().readString(charset))
//                L.d("本次网络请求结束", "<-- 网络请求结束 (" + buffer.size() + "-字节 body)")
//            }
//        }
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
