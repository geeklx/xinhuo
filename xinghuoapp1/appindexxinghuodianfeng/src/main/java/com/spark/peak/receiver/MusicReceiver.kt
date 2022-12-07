package com.spark.peak.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class MusicReceiver : BroadcastReceiver() {

    var callBacks: CallBacks? = null

    companion object {
        val PLAYER_TAG = "SPARK_AUDIO_PLAYER"
    }

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
        var status = intent.getStringExtra("status")
        when (status) {
            "pause" -> callBacks?.playOrPause()
            "previous" -> callBacks?.previous()
            "next" -> callBacks?.next()
            "close" -> callBacks?.close()
            "open" -> callBacks?.open()
        }
    }

    interface CallBacks {
        fun playOrPause()
        fun previous()
        fun next()
        fun close()
        fun open()
    }
}
