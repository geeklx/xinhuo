package com.spark.peak.utlis

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.core.content.FileProvider
import android.text.TextUtils

import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.BufferedReader
import java.io.Closeable
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStream
import java.lang.Exception
import java.util.ArrayList

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2016/8/11
 * desc  : 文件相关的工具类
</pre> *
 */
class FileUtils private constructor() {

    init {
        throw UnsupportedOperationException("u can't fuck me...")
    }

    companion object {

        /**
         * 关闭IO

         * @param closeable closeable
         */
        fun closeIO(closeable: Closeable?) {
            if (closeable == null) return
            try {
                closeable.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }

        /**
         * 根据文件路径获取文件

         * @param filePath 文件路径
         * *
         * @return 文件
         */
        fun getFileByPath(filePath: String?): File? {
            filePath ?: return null
            return File(filePath)
        }

        /**
         * 判断文件是否存在

         * @param filePath 文件路径
         * *
         * @return `false`: 存在<br></br>`false`: 不存在
         */
        fun isFileExists(filePath: String): Boolean {
            return isFileExists(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在

         * @param file 文件
         * *
         * @return `false`: 存在<br></br>`false`: 不存在
         */
        fun isFileExists(file: File?): Boolean {
            return file != null && file.exists()
        }

        /**
         * 判断是否是目录

         * @param dirPath 目录路径
         * *
         * @return `false`: 是<br></br>`false`: 否
         */
        fun isDir(dirPath: String): Boolean {
            return isDir(getFileByPath(dirPath) ?: return false)
        }

        /**
         * 判断是否是目录

         * @param file 文件
         * *
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isDir(file: File): Boolean {
            return isFileExists(file) && file.isDirectory
        }

        /**
         * 判断是否是文件

         * @param filePath 文件路径
         * *
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isFile(filePath: String): Boolean {
            return isFile(getFileByPath(filePath) ?: return false)
        }

        /**
         * 判断是否是文件

         * @param file 文件
         * *
         * @return `true`: 是<br></br>`false`: 否
         */
        fun isFile(file: File): Boolean {
            return isFileExists(file) && file.isFile
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功

         * @param dirPath 文件路径
         * *
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsDir(dirPath: String): Boolean {
            return createOrExistsDir(getFileByPath(dirPath))
        }

        /**
         * 判断目录是否存在，不存在则判断是否创建成功

         * @param file 文件
         * *
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsDir(file: File?): Boolean {
            // 如果存在，是目录则返回true，是文件则返回false，不存在则返回是否创建成功 
            return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
        }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功

         * @param filePath 文件路径
         * *
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsFile(filePath: String): Boolean {
            return createOrExistsFile(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在，不存在则判断是否创建成功

         * @param file 文件
         * *
         * @return `true`: 存在或创建成功<br></br>`false`: 不存在或创建失败
         */
        fun createOrExistsFile(file: File?): Boolean {
            file ?: return false
            // 如果存在，是文件则返回true，是目录则返回false
            if (file.exists()) return file.isFile
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 判断文件是否存在，存在则在创建之前删除

         * @param filePath 文件路径
         * *
         * @return `true`: 创建成功<br></br>`false`: 创建失败
         */
        fun createFileByDeleteOldFile(filePath: String): Boolean {
            return createFileByDeleteOldFile(getFileByPath(filePath))
        }

        /**
         * 判断文件是否存在，存在则在创建之前删除

         * @param file 文件
         * *
         * @return `true`: 创建成功<br></br>`false`: 创建失败
         */
        fun createFileByDeleteOldFile(file: File?): Boolean {
            if (file == null) return false
            // 文件存在并且删除失败返回false
            if (file.exists() && file.isFile && !file.delete()) return false
            // 创建目录失败返回false
            if (!createOrExistsDir(file.parentFile)) return false
            try {
                return file.createNewFile()
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 复制或移动目录

         * @param srcDirPath  源目录路径
         * *
         * @param destDirPath 目标目录路径
         * *
         * @param isMove      是否移动
         * *
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveDir(srcDirPath: String, destDirPath: String, isMove: Boolean): Boolean {
            return copyOrMoveDir(getFileByPath(srcDirPath), getFileByPath(destDirPath), isMove)
        }

        /**
         * 复制或移动目录

         * @param srcDir  源目录
         * *
         * @param destDir 目标目录
         * *
         * @param isMove  是否移动
         * *
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveDir(srcDir: File?, destDir: File?, isMove: Boolean): Boolean {
            if (srcDir == null || destDir == null) return false
            // 如果目标目录在源目录中则返回false，看不懂的话好好想想递归怎么结束
            // srcPath : F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res
            // destPath: F:\\MyGithub\\AndroidUtilCode\\utilcode\\src\\test\\res1
            // 为防止以上这种情况出现出现误判，须分别在后面加个路径分隔符
            val srcPath = srcDir.path + File.separator
            val destPath = destDir.path + File.separator
            if (destPath.contains(srcPath)) return false
            // 源文件不存在或者不是目录则返回false
            if (!srcDir.exists() || !srcDir.isDirectory) return false
            // 目标目录不存在返回false
            if (!createOrExistsDir(destDir)) return false
            val files = srcDir.listFiles()
            for (file in files) {
                val oneDestFile = File(destPath + file.name)
                if (file.isFile) {
                    // 如果操作失败返回false
                    if (!copyOrMoveFile(file, oneDestFile, isMove)) return false
                } else if (file.isDirectory) {
                    // 如果操作失败返回false
                    if (!copyOrMoveDir(file, oneDestFile, isMove)) return false
                }
            }
            return !isMove || deleteDir(srcDir)
        }

        /**
         * 复制或移动文件

         * @param srcFilePath  源文件路径
         * *
         * @param destFilePath 目标文件路径
         * *
         * @param isMove       是否移动
         * *
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveFile(srcFilePath: String, destFilePath: String, isMove: Boolean): Boolean {
            return copyOrMoveFile(getFileByPath(srcFilePath) ?: return false, getFileByPath(destFilePath) ?: return false, isMove)
        }

        /**
         * 复制或移动文件

         * @param srcFile  源文件
         * *
         * @param destFile 目标文件
         * *
         * @param isMove   是否移动
         * *
         * @return `true`: 复制或移动成功<br></br>`false`: 复制或移动失败
         */
        private fun copyOrMoveFile(srcFile: File, destFile: File, isMove: Boolean): Boolean {
            // 源文件不存在或者不是文件则返回false
            if (!srcFile.exists() || !srcFile.isFile) return false
            // 目标文件存在且是文件则返回false
            if (destFile.exists() && destFile.isFile) return false
            // 目标目录不存在返回false
            if (!createOrExistsDir(destFile.parentFile)) return false
            try {
                return writeFileFromIS(destFile, FileInputStream(srcFile), false) && !(isMove && !deleteFile(srcFile))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
                return false
            }

        }

        /**
         * 复制目录

         * @param srcDirPath  源目录路径
         * *
         * @param destDirPath 目标目录路径
         * *
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyDir(srcDirPath: String, destDirPath: String): Boolean {
            return copyDir(getFileByPath(srcDirPath) ?: return false, getFileByPath(destDirPath) ?: return false)
        }

        /**
         * 复制目录

         * @param srcDir  源目录
         * *
         * @param destDir 目标目录
         * *
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyDir(srcDir: File, destDir: File): Boolean {
            return copyOrMoveDir(srcDir, destDir, false)
        }

        /**
         * 复制文件

         * @param srcFilePath  源文件路径
         * *
         * @param destFilePath 目标文件路径
         * *
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyFile(srcFilePath: String, destFilePath: String): Boolean {
            return copyFile(getFileByPath(srcFilePath) ?: return false, getFileByPath(destFilePath) ?: return false)
        }

        /**
         * 复制文件

         * @param srcFile  源文件
         * *
         * @param destFile 目标文件
         * *
         * @return `true`: 复制成功<br></br>`false`: 复制失败
         */
        fun copyFile(srcFile: File, destFile: File): Boolean {
            return copyOrMoveFile(srcFile, destFile, false)
        }

        /**
         * 移动目录

         * @param srcDirPath  源目录路径
         * *
         * @param destDirPath 目标目录路径
         * *
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveDir(srcDirPath: String, destDirPath: String): Boolean {
            return moveDir(getFileByPath(srcDirPath) ?: return false, getFileByPath(destDirPath) ?: return false)
        }

        /**
         * 移动目录

         * @param srcDir  源目录
         * *
         * @param destDir 目标目录
         * *
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveDir(srcDir: File, destDir: File): Boolean {
            return copyOrMoveDir(srcDir, destDir, true)
        }

        /**
         * 移动文件

         * @param srcFilePath  源文件路径
         * *
         * @param destFilePath 目标文件路径
         * *
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveFile(srcFilePath: String, destFilePath: String): Boolean {
            return moveFile(getFileByPath(srcFilePath) ?: return false, getFileByPath(destFilePath) ?: return false)
        }

        /**
         * 移动文件

         * @param srcFile  源文件
         * *
         * @param destFile 目标文件
         * *
         * @return `true`: 移动成功<br></br>`false`: 移动失败
         */
        fun moveFile(srcFile: File, destFile: File): Boolean {
            return copyOrMoveFile(srcFile, destFile, true)
        }

        /**
         * 删除目录

         * @param dirPath 目录路径
         * *
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteDir(dirPath: String): Boolean {
            return deleteDir(getFileByPath(dirPath))
        }

        /**
         * 删除目录

         * @param dir 目录
         * *
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteDir(dir: File?): Boolean {
            if (dir == null) return false
            // 目录不存在返回true
            if (!dir.exists()) return true
            // 不是目录返回false
            if (!dir.isDirectory) return false
            // 现在文件存在且是文件夹
            val files = dir.listFiles()
            for (file in files) {
                if (file.isFile) {
                    if (!deleteFile(file)) return false
                } else if (file.isDirectory) {
                    if (!deleteDir(file)) return false
                }
            }
            return dir.delete()
        }

        /**
         * 删除文件

         * @param srcFilePath 文件路径
         * *
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFile(srcFilePath: String): Boolean {
            return deleteFile(getFileByPath(srcFilePath))
        }

        /**
         * 删除文件

         * @param file 文件
         * *
         * @return `true`: 删除成功<br></br>`false`: 删除失败
         */
        fun deleteFile(file: File?): Boolean {
            return file != null && (!file.exists() || file.isFile && file.delete())
        }

        /**
         * 将输入流写入文件

         * @param filePath 路径
         * *
         * @param is       输入流
         * *
         * @param append   是否追加在文件末
         * *
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromIS(filePath: String?, stream: InputStream, append: Boolean): Boolean {
            return writeFileFromIS(getFileByPath(filePath) ?: return false, stream, append)
        }

        /**
         * 将输入流写入文件

         * @param file   文件
         * *
         * @param is     输入流
         * *
         * @param append 是否追加在文件末
         * *
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromIS(file: File, stream: InputStream, append: Boolean): Boolean {
            if (!createOrExistsFile(file)) return false
            var os: OutputStream? = null
            try {
                os = BufferedOutputStream(FileOutputStream(file, append))
                val data = ByteArray(1024)
                var len = stream.read(data)
                while (len != -1) {
                    os.write(data, 0, len)
                    len = stream.read(data)
                }
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                closeIO(stream)
                closeIO(os)
            }
        }

        /**
         * 将输入流写入文件

         * @param filePath 路径
         * *
         * @param is       输入流
         * *
         * @param append   是否追加在文件末
         * *
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromByte(filePath: String?, byte: ByteArray) {
            filePath ?: return
            writeFileFromByte(getFileByPath(filePath) ?: return, byte)
        }

        /**
         * 将输入流写入文件

         * @param file   文件
         * *
         * @param is     输入流
         * *
         * @param append 是否追加在文件末
         * *
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromByte(file: File?, byte: ByteArray) {
            if (!createOrExistsFile(file)) return
            try {
//                    val str = java.lang.String(bytes, "GB2312")
                val fos = FileOutputStream(file)
                fos.write(byte)
                fos.flush()
                fos.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        /**
         * 将字符串写入文件

         * @param filePath 文件路径
         * *
         * @param content  写入内容
         * *
         * @param append   是否追加在文件末
         * *
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromString(filePath: String, content: String, append: Boolean): Boolean {
            return writeFileFromString(getFileByPath(filePath), content, append)
        }

        /**
         * 将字符串写入文件

         * @param file    文件
         * *
         * @param content 写入内容
         * *
         * @param append  是否追加在文件末
         * *
         * @return `true`: 写入成功<br></br>`false`: 写入失败
         */
        fun writeFileFromString(file: File?, content: String?, append: Boolean): Boolean {
            if (file == null || content == null) return false
            if (!createOrExistsFile(file)) return false
            var fileWriter: FileWriter? = null
            try {
                fileWriter = FileWriter(file, append)
                fileWriter.write(content)
                return true
            } catch (e: IOException) {
                e.printStackTrace()
                return false
            } finally {
                closeIO(fileWriter)
            }
        }

        /**
         * 简单获取文件编码格式

         * @param filePath 文件路径
         * *
         * @return 文件编码
         */
        fun getFileCharsetSimple(filePath: String): String {
            return getFileCharsetSimple(getFileByPath(filePath))
        }

        /**
         * 简单获取文件编码格式

         * @param file 文件
         * *
         * @return 文件编码
         */
        fun getFileCharsetSimple(file: File?): String {
            file ?: return ""
            var p = 0
            var stream: InputStream? = null
            try {
                stream = BufferedInputStream(FileInputStream(file))
                p = (stream.read() shl 8) + stream.read()
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeIO(stream)
            }
            when (p) {
                0xefbb -> return "UTF-8"
                0xfffe -> return "Unicode"
                0xfeff -> return "UTF-16BE"
                else -> return "GBK"
            }
        }

        /**
         * 获取文件行数

         * @param filePath 文件路径
         * *
         * @return 文件行数
         */
        fun getFileLines(filePath: String): Int {
            return getFileLines(getFileByPath(filePath))
        }

        /**
         * 获取文件行数

         * @param file 文件
         * *
         * @return 文件行数
         */
        fun getFileLines(file: File?): Int {
            file ?: return 0
            var count = 1
            var stream: InputStream? = null
            try {
                stream = BufferedInputStream(FileInputStream(file))
                val buffer = ByteArray(1024)
                var readChars: Int = stream.read(buffer)
                while (readChars != -1) {
                    for (i in 0..readChars - 1) {
                        if (buffer[i] == '\n'.toByte()) ++count
                    }
                    readChars = stream.read(buffer)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeIO(stream)
            }
            return count
        }

        /**
         * 指定编码按行读取文件到List

         * @param filePath    文件路径
         * *
         * @param charsetName 编码格式
         * *
         * @return 文件行链表
         */
        fun readFile2List(filePath: String, charsetName: String): List<String>? {
            return readFile2List(getFileByPath(filePath), charsetName)
        }

        /**
         * 指定编码按行读取文件到List

         * @param file        文件
         * *
         * @param charsetName 编码格式
         * *
         * @return 文件行链表
         */
        fun readFile2List(file: File?, charsetName: String): List<String>? {
            return readFile2List(file, 0, 0x7FFFFFFF, charsetName)
        }

        /**
         * 指定编码按行读取文件到List

         * @param filePath    文件路径
         * *
         * @param start       需要读取的开始行数
         * *
         * @param end         需要读取的结束行数
         * *
         * @param charsetName 编码格式
         * *
         * @return 包含制定行的list
         */
        fun readFile2List(filePath: String, start: Int, end: Int, charsetName: String): List<String>? {
            return readFile2List(getFileByPath(filePath), start, end, charsetName)
        }

        /**
         * 指定编码按行读取文件到List

         * @param file        文件
         * *
         * @param start       需要读取的开始行数
         * *
         * @param end         需要读取的结束行数
         * *
         * @param charsetName 编码格式
         * *
         * @return 包含制定行的list
         */
        fun readFile2List(file: File?, start: Int, end: Int, charsetName: String): List<String>? {
            file ?: return null
            if (start > end) return null
            var list: MutableList<String>? = null
            var reader: BufferedReader? = null
            try {
                var line: String
                var curLine = 1
                list = ArrayList<String>()
                if (TextUtils.isEmpty(charsetName)) {
                    reader = BufferedReader(FileReader(file))
                } else {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file), charsetName))
                }
                line = reader.readLine()
                while (line != null) {
                    if (curLine > end) break
                    if (curLine in start..end) list.add(line)
                    ++curLine
                    line = reader.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeIO(reader)
            }
            return list
        }

        /**
         * 指定编码按行读取文件到StringBuilder中

         * @param filePath    文件路径
         * *
         * @param charsetName 编码格式
         * *
         * @return StringBuilder对象
         */
        fun readFile2SB(filePath: String, charsetName: String): StringBuilder? {
            return readFile2SB(getFileByPath(filePath), charsetName)
        }

        /**
         * 指定编码按行读取文件到StringBuilder中

         * @param file        文件
         * *
         * @param charsetName 编码格式
         * *
         * @return StringBuilder对象
         */
        fun readFile2SB(file: File?, charsetName: String): StringBuilder? {
            file ?: return null
            var sb: StringBuilder? = null
            var reader: BufferedReader? = null
            try {
                sb = StringBuilder()
                if (TextUtils.isEmpty(charsetName)) {
                    reader = BufferedReader(FileReader(file))
                } else {
                    reader = BufferedReader(InputStreamReader(FileInputStream(file),
                            charsetName))
                }
                var line = reader.readLine()
                while ((line) != null) {
                    sb.append(line)
                    sb.append("\r\n")// windows系统换行为\r\n，Linux为\n
                    line = reader.readLine()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } finally {
                closeIO(reader)
            }
            return sb
        }

        /**
         * byte单位转换（单位：unit）

         * @param size 大小
         * *
         * @param unit
         * *              * ConstUtils.BYTE:字节
         * *              * ConstUtils.KB  :千字节
         * *              * ConstUtils.MB  :兆
         * *              * ConstUtils.GB  :GB
         * *
         * *
         * @return 大小以unit为单位
         */
        fun byte2Unit(size: Long, unit: Int): Double {
            when (unit) {
                1, 1 * 1024, 1 * 1024 * 1024, 1 * 1024 * 1024 * 1024 -> return size.toDouble() / unit
            }
            return -1.0
        }

        /**
         * 获取文件大小
         *
         * 例如：getFileSize(filePath, ConstUtils.MB); 返回文件大小单位为MB

         * @param filePath 文件路径
         * *
         * @param unit
         * *                  * ConstUtils.BYTE:字节
         * *                  * ConstUtils.KB  :千字节
         * *                  * ConstUtils.MB  :兆
         * *                  * ConstUtils.GB  :GB
         * *
         * *
         * @return 文件大小以unit为单位
         */
        fun getFileSize(filePath: String, unit: Int): Double {
            return getFileSize(getFileByPath(filePath) ?: return -1.0, unit)
        }

        /**
         * 获取文件大小
         *
         * 例如：getFileSize(file, ConstUtils.MB); 返回文件大小单位为MB

         * @param file 文件
         * *
         * @param unit
         * *              * ConstUtils.BYTE:字节
         * *              * ConstUtils.KB  :千字节
         * *              * ConstUtils.MB  :兆
         * *              * ConstUtils.GB  :GB
         * *
         * *
         * @return 文件大小以unit为单位
         */
        fun getFileSize(file: File, unit: Int): Double {
            if (!isFileExists(file)) return -1.0
            return byte2Unit(file.length(), unit)
        }

        /**
         * 根据全路径获取最长目录

         * @param filePath 文件路径
         * *
         * @return filePath最长目录
         */
        fun getDirName(filePath: String): String {
            if (TextUtils.isEmpty(filePath)) return filePath
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) "" else filePath.substring(0, lastSep + 1)
        }

        /**
         * 根据全路径获取文件名

         * @param filePath 文件路径
         * *
         * @return 文件名
         */
        fun getFileName(filePath: String): String {
            if (TextUtils.isEmpty(filePath)) return filePath
            val lastSep = filePath.lastIndexOf(File.separator)
            return if (lastSep == -1) filePath else filePath.substring(lastSep + 1)
        }

        /**
         * 根据全路径获取文件名不带拓展名

         * @param filePath 文件路径
         * *
         * @return 文件名不带拓展名
         */
        fun getFileNameNoExtension(filePath: String): String {
            if (TextUtils.isEmpty(filePath)) return filePath
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            if (lastSep == -1) {
                return if (lastPoi == -1) filePath else filePath.substring(0, lastPoi)
            }
            if (lastPoi == -1 || lastSep > lastPoi) {
                return filePath.substring(lastSep + 1)
            }
            return filePath.substring(lastSep + 1, lastPoi)
        }

        /**
         * 根据全路径获取文件拓展名

         * @param filePath 文件路径
         * *
         * @return 文件拓展名
         */
        fun getFileExtension(filePath: String): String {
            if (TextUtils.isEmpty(filePath)) return filePath
            val lastPoi = filePath.lastIndexOf('.')
            val lastSep = filePath.lastIndexOf(File.separator)
            if (lastPoi == -1 || lastSep >= lastPoi) return ""
            return filePath.substring(lastPoi + 1)
        }

        /**
         * 储存图片到文件
         */
        @Throws(IOException::class)
        fun saveToFile(loadedImage: Bitmap, file: File) {
            if (!createFileByDeleteOldFile(file)) return
            val outputStream = FileOutputStream(file)
            loadedImage.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.flush()
            outputStream.close()

        }

        fun installAPK(context: Context, filePath: String){
            if (filePath.isNotEmpty()){
                var apkFile = File(filePath)
                var intent = Intent(Intent.ACTION_VIEW)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                if(Build.VERSION.SDK_INT>=24) { //判读版本是否在7.0以上
                    //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
                    var apkUri =FileProvider.getUriForFile(context, "com.spark.peak.fileprovider", apkFile)
                    //添加这一句表示对目标应用临时授权该Uri所代表的文件
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
                }else{
                    intent.setDataAndType(Uri.fromFile(apkFile),
                            "application/vnd.android.package-archive")
                }
                try {
                    context.startActivity(intent)
                }catch (e: Exception){
                    e.message
                }
            }

        }
    }
}