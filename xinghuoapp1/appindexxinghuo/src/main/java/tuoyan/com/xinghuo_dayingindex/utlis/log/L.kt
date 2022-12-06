package tuoyan.com.xinghuo_dayingindex.utlis.log

import android.text.TextUtils
import android.util.Log

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.PrintWriter
import java.io.StringWriter
import java.net.UnknownHostException
import java.util.Arrays

/**
 * 创建者： huoshulei
 * 时间：  2017/4/19.
 */

object L {

    private val CHUNK_SIZE = 4000


    private val JSON_INDENT = 5


    private val MIN_STACK_OFFSET = 3

    private val TOP_LEFT_CORNER = '╔'
    private val BOTTOM_LEFT_CORNER = '╚'
    private val MIDDLE_CORNER = '╟'
    private val HORIZONTAL_DOUBLE_LINE = '║'
    private val DOUBLE_DIVIDER = "════════════════════════════════════════════"
    private val SINGLE_DIVIDER = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~"
    private val TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER
    private val MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER


    private var tag = "日志"
    private var methodCount = 2
    private var showThreadInfo = true
    private var methodOffset = 0
    private var logLevel = LogLevel.FULL

    private val localTag = ThreadLocal<String>()
    private val localMethodCount = ThreadLocal<Int>()




    fun init(tag: String): Builder {
        L.tag = tag
        return Builder()
    }


    fun d(tag: String?, methodCount: Int, obj: Any) {
        if (tag != null) {
            localTag.set(tag)
        }
        localMethodCount.set(methodCount)
        d(obj)
    }

    fun d(tag: String?, obj: Any) {
        if (tag != null) {
            localTag.set(tag)
        }
        d(obj)
    }


    fun d(obj: Any) {
        val message: String
        if (obj.javaClass.isArray) {
            message = Arrays.deepToString(obj as Array<*>)
        } else {
            message = obj.toString()
        }
        log(null, message)
    }

    fun json(tag: String?, json: String) {
        if (tag != null) {
            localTag.set(tag)
        }
        json(json)
    }

    fun json(json: String) {
        var jsonr = json
        if (TextUtils.isEmpty(jsonr)) {
            return
        }
        try {
            jsonr = jsonr.trim { it <= ' ' }
            if (jsonr.startsWith("{")) {
                val jsonObject = JSONObject(jsonr)
                val message = jsonObject.toString(JSON_INDENT)
                d(message)
                return
            }
            if (jsonr.startsWith("[")) {
                val jsonArray = JSONArray(jsonr)
                val message = jsonArray.toString(JSON_INDENT)
                d(message)
                return
            }
            d(jsonr)
        } catch (e: JSONException) {
            d(jsonr)
        }

    }


    @Synchronized private fun log(tag: String, message: String?, throwable: Throwable?) {
        var messagar = message
        if (logLevel == LogLevel.NONE) {
            return
        }
        if (throwable != null && messagar != null) {
            messagar += " : " + getStackTraceString(throwable)
        }
        if (throwable != null && messagar == null) {
            messagar = getStackTraceString(throwable)
        }
        if (messagar == null) {
            messagar = "没有设置日志信息"
        }
        val methodCount = getMethodCount()
        if (TextUtils.isEmpty(messagar)) {
            messagar = "空日志信息"
        }

        logTopBorder(tag)
        logHeaderContent(tag, methodCount)

        val bytes = messagar.toByteArray()
        val length = bytes.size
        if (length <= CHUNK_SIZE) {
            if (methodCount > 0) {
                logDivider(tag)
            }
            logContent(tag, messagar)
            logBottomBorder(tag)
            return
        }
        if (methodCount > 0) {
            logDivider(tag)
        }
        var i = 0
        while (i < length) {
            val count = Math.min(length - i, CHUNK_SIZE)
            logContent(tag, String(bytes, i, count))
            i += CHUNK_SIZE
        }
        logBottomBorder(tag)
    }


    @Synchronized private fun log(throwable: Throwable?, msg: String, vararg args: Any) {
        if (logLevel == LogLevel.NONE) {
            return
        }
        val tag = getTag()
        val message = createMessage(msg, *args)
        log(tag, message, throwable)
    }

    private fun logTopBorder(tag: String) {
        logChunk(tag, TOP_BORDER)
    }

    private fun logHeaderContent(tag: String, methodCount: Int) {
        var methodCountr = methodCount
        val trace = Thread.currentThread().stackTrace
        if (showThreadInfo) {
            logChunk(tag, HORIZONTAL_DOUBLE_LINE + " Thread: " + Thread.currentThread().name)
            logDivider(tag)
        }
        var level = ""

        val stackOffset = getStackOffset(trace) + methodOffset

        if (methodCountr + stackOffset > trace.size) {
            methodCountr = trace.size - stackOffset - 1
        }

        for (i in methodCountr downTo 1) {
            val stackIndex = i + stackOffset
            if (stackIndex >= trace.size) {
                continue
            }
            val builder = StringBuilder()
            builder.append("║ ")
                    .append(level)
                    .append(getSimpleClassName(trace[stackIndex].className))
                    .append(".")
                    .append(trace[stackIndex].methodName)
                    .append(" ")
                    .append(" (")
                    .append(trace[stackIndex].fileName)
                    .append(":")
                    .append(trace[stackIndex].lineNumber)
                    .append(")")
            level += "   "
            logChunk(tag, builder.toString())
        }
    }

    private fun logBottomBorder(tag: String) {
        logChunk(tag, BOTTOM_BORDER)
    }

    private fun logDivider(tag: String) {
        logChunk(tag, MIDDLE_BORDER)
    }

    private fun logContent(tag: String, chunk: String) {
        val lines = System.getProperty("line.separator")?.let { chunk.split(it.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
        if (lines != null) {
            for (line in lines) {
                logChunk(tag, HORIZONTAL_DOUBLE_LINE + " " + line)
            }
        }
    }

    private fun logChunk(tag: String, chunk: String) {
        Log.d(formatTag(tag), chunk)
    }

    private fun getSimpleClassName(name: String): String {
        val lastIndex = name.lastIndexOf(".")
        return name.substring(lastIndex + 1)
    }

    private fun formatTag(tag: String): String {
        if (!TextUtils.isEmpty(tag) && !TextUtils.equals(L.tag, tag)) {
            return L.tag + "-" + tag
        }
        return L.tag
    }


    private fun getTag(): String {
        val tag = localTag.get()
        if (tag != null) {
            localTag.remove()
            return tag
        }
        return L.tag
    }

    private fun createMessage(message: String, vararg args: Any): String {
        return if (args.isEmpty()) message else String.format(message, *args)
    }

    private fun getMethodCount(): Int {
        val count = localMethodCount.get()
        var result = methodCount
        if (count != null) {
            localMethodCount.remove()
            result = count
        }
        if (result < 0) {
            throw IllegalStateException("methodCount cannot be negative")
        }
        return result
    }

    private fun getStackOffset(trace: Array<StackTraceElement>): Int {
        var i = MIN_STACK_OFFSET
        while (i < trace.size) {
            val e = trace[i]
            val name = e.className
            if (name != L::class.java.name) {
                return --i
            }
            i++
        }
        return -1
    }

    private fun getStackTraceString(tr: Throwable?): String {
        if (tr == null) {
            return ""
        }

        // This is to reduce the amount of log spew that apps do in the non-error
        // condition of the network being unavailable.
        var t = tr
        while (t != null) {
            if (t is UnknownHostException) {
                return ""
            }
            t = t.cause
        }

        val sw = StringWriter()
        val pw = PrintWriter(sw)
        tr.printStackTrace(pw)
        pw.flush()
        return sw.toString()
    }

    class Builder
    /**
     * Determines to how logs will be printed
     */
    internal constructor() {

        fun hideThreadInfo(): Builder {
            showThreadInfo = false
            return this
        }

        fun methodCount(methodCount: Int): Builder {
            var methodCountr = methodCount
            if (methodCountr < 0) {
                methodCountr = 0
            }
            L.methodCount = methodCountr
            return this
        }

        fun logLevel(logLevel: LogLevel): Builder {
            L.logLevel = logLevel
            return this
        }

        fun methodOffset(offset: Int): Builder {
            methodOffset = offset
            return this
        }

        fun bulid() {

        }
    }

    enum class LogLevel {
        /**
         * Prints all logs
         */
        FULL,
        /**
         * No log will be printed
         */
        NONE
    }
}
