package com.spark.peak.utlis

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.geek.libutils.app.BaseApp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.spark.peak.MyApp
import com.spark.peak.bean.*
import kotlin.properties.Delegates
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty


/**
 * 创建者： huoshulei
 * 时间：  2017/4/26.
 */
class Preference<T>(val content: Context, val name: String, val default: T) :
    ReadWriteProperty<Any?, T> {
    private val prefs: SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(content)
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return get(name, default)
    }


    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        put(name, value)
    }


    private fun get(name: String, default: T): T = with(prefs) {
        val res: Any = when (default) {
            is Long -> getLong(name, default)
            is String -> getString(name, default) ?: ""
            is Int -> getInt(name, default)
            is Boolean -> getBoolean(name, default)
            is Float -> getFloat(name, default)
            is UserInfo -> Gson().fromJson(
                getString(name, Gson().toJson(default)),
                UserInfo::class.java
            )
            is Section -> Gson().fromJson(
                getString(name, Gson().toJson(default)),
                Section::class.java
            )
            is Grade -> Gson().fromJson(getString(name, Gson().toJson(default)), Grade::class.java)
            is ArrayList<*> -> Gson().fromJson(
                getString(name, Gson().toJson(default)),
                object : TypeToken<List<DownloadInfo>>() {}.type
            )
            is LoginResponse -> Gson().fromJson(
                getString(name, Gson().toJson(default)),
                LoginResponse::class.java
            )
            is GradeBean -> Gson().fromJson(
                getString(name, Gson().toJson(default)),
                GradeBean::class.java
            )
//            is Version -> Gson().fromJson(getString(name, Gson().toJson(default)), Version::class.java)
            else -> throw IllegalArgumentException("This type can be saved into Preferences")
        }
        res as T
    }

    private fun put(name: String, value: T) = with(prefs.edit()) {
        when (value) {
            is Long -> putLong(name, value)
            is String -> putString(name, value)
            is Int -> putInt(name, value)
            is Boolean -> putBoolean(name, value)
            is Float -> putFloat(name, value)
            is UserInfo -> putString(name, Gson().toJson(value))
            is Section -> putString(name, Gson().toJson(value))
            is Grade -> putString(name, Gson().toJson(value))
            is ArrayList<*> -> putString(name, Gson().toJson(value))
            is LoginResponse -> putString(name, Gson().toJson(value))
            is GradeBean -> putString(name, Gson().toJson(value))
//            is Version -> putString(name, Gson().toJson(value))
            else -> throw IllegalArgumentException("This type can be saved into Preferences ")
        }.apply()
    }


}

fun <T : Any> Delegates.preference(name: String, default: T, context: Context = BaseApp.get()) =
    Preference(context, name, default)