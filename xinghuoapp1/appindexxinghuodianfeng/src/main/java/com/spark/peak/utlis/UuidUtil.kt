package com.spark.peak.utlis

import android.content.Context
import android.os.Environment
import java.io.*
import java.util.*

class UuidUtil {
    companion object {
        var CACHE_IMAGE_DIR = "df/cache/uuid"
        var DEVICES_FILE_NAME = ".UUID"
        fun readUuid(context: Context): String {
            try {
                var file = getUuidDir(context)
                var buffer = StringBuffer()
                var fis = FileInputStream(file)
                var isr = InputStreamReader(fis, "UTF-8")
                var reader = BufferedReader(isr)
                reader.lineSequence().forEach {
                    buffer.append(it)
                }
                reader.close()
                isr.close()
                fis.close()
                return buffer.toString()
            } catch (e: Exception) {
                e.stackTrace
                return ""
            }
        }

        fun saveUuid(uuid: String, context: Context) {
            var file = getUuidDir(context)
            try {
                var fos = FileOutputStream(file)
                var out = OutputStreamWriter(fos, "UTF-8")
                out.write(uuid)
                out.close()
                fos.close()
            } catch (e: Exception) {
                e.stackTrace
            }
        }

        fun getUuidDir(context: Context): File {
            var mCropFile: File? = null;
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                var cropFile = File(Environment.getExternalStorageDirectory(), CACHE_IMAGE_DIR)
                if (!cropFile.exists()) {
                    cropFile.mkdirs()
                }
                mCropFile = File(cropFile, DEVICES_FILE_NAME);
            } else {
                var cropFile = File(context.filesDir, CACHE_IMAGE_DIR)
                if (!cropFile.exists()) {
                    cropFile.mkdirs()
                }
                mCropFile = File(cropFile, DEVICES_FILE_NAME);
            }
            return mCropFile
        }

        fun getUuid(): String {
            return UUID.randomUUID().toString().replace("-", "")
        }
    }
}