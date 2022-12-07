package com.spark.peak.utlis

import android.annotation.SuppressLint

import android.content.ContentUris

import android.content.Context

import android.database.Cursor

import android.net.Uri

import android.os.Build

import android.os.Environment

import android.provider.DocumentsContract

import android.provider.MediaStore

object GetImagePath {

    //  4.4以上  content://com.android.providers.media.documents/document/image:3952

    //  4.4以下  content://media/external/images/media/3951

    @SuppressLint("NewApi")
    fun getPath(context: Context, uri: Uri): String? {

        val isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT

        // DocumentProvider

        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {

            // ExternalStorageProvider

            if (isExternalStorageDocument(uri)) {

                val docId = DocumentsContract.getDocumentId(uri)

                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                val type = split[0]

                if ("primary".equals(type, ignoreCase = true)) {

                    return Environment.getExternalStorageDirectory().toString() + "/" + split[1]

                }

            } else if (isDownloadsDocument(uri)) {

                val id = DocumentsContract.getDocumentId(uri)

                val contentUri = ContentUris.withAppendedId(

                        Uri.parse("content://downloads/public_downloads"), java.lang.Long.valueOf(id)!!)

                return getDataColumn(context, contentUri, null, null)

            } else if (isMediaDocument(uri)) {

                val docId = DocumentsContract.getDocumentId(uri)

                val split = docId.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                val type = split[0]

                var contentUri: Uri? = null

                if ("image" == type) {

                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

                } else if ("video" == type) {

                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

                } else if ("audio" == type) {

                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

                }

                val selection = "_id=?"

                val selectionArgs = arrayOf(

                        split[1])

                return getDataColumn(context, contentUri, selection, selectionArgs)

            }// MediaProvider
            // DownloadsProvider

        } else if ("content".equals(uri.scheme, ignoreCase = true)) {

            // Return the remote address

            return if (isGooglePhotosUri(uri)) uri.lastPathSegment else getDataColumn(context, uri, null, null)

        } else if ("file".equals(uri.scheme, ignoreCase = true)) {

            return uri.path

        }// File
        // MediaStore (and general)

        return null

    }

    //Android 4.4以下版本自动使用该方法

    fun getDataColumn(context: Context, uri: Uri?, selection: String?,

                      selectionArgs: Array<String>?): String? {

        var cursor: Cursor? = null

        val column = "_data"

        val projection = arrayOf(

                column)

        try {

            cursor = context.contentResolver.query(uri!!, projection, selection, selectionArgs, null)

            if (cursor != null && cursor.moveToFirst()) {

                val index = cursor.getColumnIndexOrThrow(column)

                return cursor.getString(index)

            }

        } finally {

            if (cursor != null)

                cursor.close()

        }

        return null

    }

    /**
     * @paramuriThe Uri to check.
     * @returnWhether the Uri authority is ExternalStorageProvider.
     */

    fun isExternalStorageDocument(uri: Uri): Boolean {

        return "com.android.externalstorage.documents" == uri.authority

    }

    /**
     * @paramuriThe Uri to check.
     * @returnWhether the Uri authority is DownloadsProvider.
     */

    fun isDownloadsDocument(uri: Uri): Boolean {

        return "com.android.providers.downloads.documents" == uri.authority

    }

    /**
     * @paramuriThe Uri to check.
     * @returnWhether the Uri authority is MediaProvider.
     */

    fun isMediaDocument(uri: Uri): Boolean {

        return "com.android.providers.media.documents" == uri.authority

    }

    /**
     * @paramuriThe Uri to check.
     * @returnWhether the Uri authority is Google Photos.
     */

    fun isGooglePhotosUri(uri: Uri): Boolean {

        return "com.google.android.apps.photos.content" == uri.authority

    }

}


