package com.spark.peak.utlis

import android.content.Context
import androidx.core.app.NotificationManagerCompat

class NotificationUtils {
    companion object {
        fun checkNotifySetting(context: Context): Boolean {
            var manager = NotificationManagerCompat.from(context)
            return manager.areNotificationsEnabled()
        }
    }
}