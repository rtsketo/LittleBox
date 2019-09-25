package com.arti.games.littlebox

import android.content.Context
import android.content.SharedPreferences
import android.content.SharedPreferences.Editor
import android.preference.PreferenceManager

class Saves(context: Context) {
    internal var loader: SharedPreferences

    init {
        loader = PreferenceManager.getDefaultSharedPreferences(context)
    }

    fun write(key: String, value: Int) {
        val edit = loader.edit()
        edit.putInt(key, value)
        edit.commit()
    }

    fun read(key: String): Int {
        return loader.getInt(key, 0)
    }

}
