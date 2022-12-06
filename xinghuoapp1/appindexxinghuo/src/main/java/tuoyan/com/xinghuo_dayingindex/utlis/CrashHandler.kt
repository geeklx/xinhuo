package tuoyan.com.xinghuo_dayingindex.utlis

import android.content.Context
import android.os.Environment
import android.os.Looper
import android.widget.Toast
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.system.exitProcess

/**
 * Created by Zzz on 2021/3/10
 */

class CrashHandler private constructor(context: Context) : Thread.UncaughtExceptionHandler {
    private val mContext = context
    private val info = LinkedHashMap<String, String>()
    private val mDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    companion object {
        private var instance: CrashHandler? = null
        fun getInstance(context: Context): CrashHandler? {
            if (instance == null) {
                synchronized(CrashHandler::class.java) {
                    instance = CrashHandler(context)
                }
            }
            return instance
        }
    }

    init {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
//        handleException(e)
//        try {
//            Thread.sleep(2000)
//        } catch (e: Exception) {
//        }
        exitProcess(0)
    }

    private fun handleException(e: Throwable) {
//        writeSD(e)
        Thread {
            Looper.prepare()
            Toast.makeText(mContext, "星火英语出现错误，即将退出", Toast.LENGTH_SHORT).show()
            Looper.loop()
        }.start()
    }

    fun writeSD(e: Throwable) {
        val mLodPath: String = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/crashHandler/"
        val file = File(mLodPath)
        if (!file.exists()) {
            file.mkdirs()
        }
        val time = SimpleDateFormat("yyyy-mm-dd-HH:mm:ss", Locale.CHINA).format(Date(System.currentTimeMillis()))
        val logFile = File(mLodPath, time.toString() + ".txt")
        val pw = PrintWriter(BufferedWriter(FileWriter(logFile)))
        pw.println(time);
        pw.println();
        e.printStackTrace(pw);
        pw.close();
    }
}