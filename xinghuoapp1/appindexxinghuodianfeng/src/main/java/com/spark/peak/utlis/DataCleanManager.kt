package com.spark.peak.utlis


import android.content.Context
import android.os.Environment

import java.io.File

/**
 * 创建者： huoshulei
 * 时间：  2017/3/27.
 * 文 件 名:  DataCleanManager.java
 * 描    述:  主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录
 *
 *
 * 本应用数据清除管理器
 */
object DataCleanManager {


    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    private fun cleanInternalCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir)
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    private fun cleanDatabases(context: Context) {
        deleteFilesByDirectory(File("/data/data/"
                + context.packageName + "/databases"))
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    fun cleanSharedPreference(context: Context) {
        deleteFilesByDirectory(File("/data/data/"
                + context.packageName + "/shared_prefs"))
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    fun cleanDatabaseByName(context: Context, dbName: String) {
        context.deleteDatabase(dbName)
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    private fun cleanFiles(context: Context) {
        deleteFilesByDirectory(context.filesDir)
    }

    /**
     * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    private fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    private fun cleanCustomCache(filePath: String) {
        deleteFilesByDirectory(File(filePath))
    }

    /**
     *
     * 本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    fun getInternalCache(context: Context): Long {
        return getDirSize(context.cacheDir)
    }

    /**
     * 本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    private fun getDatabases(context: Context): Long {
        return getDirSize(File("/data/data/"
                + context.packageName + "/databases"))
    }

    /**
     * * 本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    fun getSharedPreference(context: Context): Long {
        return getDirSize(File(("/data/data/"
                + context.packageName + "/shared_prefs")))
    }


    /**
     * /data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    private fun getFiles(context: Context): Long {
        return getDirSize(context.filesDir)
    }

    /**
     * * 外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    private fun getExternalCache(context: Context): Long {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            return getDirSize(context.externalCacheDir)
        }
        return 0
    }

    /**
     * 自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath
     */
    private fun getCustomCache(filePath: String): Long {
        return getDirSize(File(filePath))
    }

    /**
     * 清除本应用所有的数据 * * @param context * @param filepath
     */
    fun cleanApplicationData(context: Context, vararg filepath: String) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
//        cleanSharedPreference(context);
        cleanFiles(context)

        for (filePath in filepath) {
            cleanCustomCache(filePath)
        }
    }

    /**
     * 获取本应用所有的数据的大小 * * @param context * @param filepath
     */
    fun getApplicationDataSize(context: Context, vararg filepath: String): String {
        var size: Long = 0
//        size += getInternalCache(context);
        size += getExternalCache(context)
        size += getDatabases(context)
//        size += getSharedPreference(context);
        size += getFiles(context)
        for (path in filepath) {
            size += getCustomCache(path)
        }
        return formatFileSize(size)
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    private fun deleteFilesByDirectory(directory: File?) {
        try {
            if (directory != null && directory.exists() && directory.isDirectory) {
                for (item in directory.listFiles()) {
                    if (item.isDirectory) {
                        deleteFilesByDirectory(item)
                        item.delete()
                    }
                    if (item.isFile) item.delete()
                }
            }
        } catch (e: Exception) {
        }
    }

    /**
     * 获取目录文件大小
     *
     * @param dir
     * @return
     */
    private fun getDirSize(dir: File?): Long {
        if (dir == null) {
            return 0
        }
        if (!dir.isDirectory) {
            return 0
        }
        var dirSize: Long = 0
        val files = dir.listFiles() ?: return 0
        for (file in files) {
            if (file.isFile) {
                dirSize += file.length()
            } else if (file.isDirectory) {
                dirSize += file.length()
                dirSize += getDirSize(file) // 递归调用继续统计
            }
        }
        return dirSize
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return B/KB/MB/GB
     */
    private fun formatFileSize(fileS: Long): String {
        val df = java.text.DecimalFormat("0.00")
        val fileSizeString: String
        fileSizeString = when {
            fileS < 1024 -> df.format(fileS) + "B"
            fileS < 1048576 -> df.format(fileS / 1024) + "KB"
            fileS < 1073741824 -> df.format(fileS / 1048576) + "MB"
            else -> df.format(fileS / 1073741824) + "G"
        }
        return fileSizeString
    }

}