package com.example.kueat.sharedpreference

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity


class MyTag(context: Context) {
    private val prefsFilename = "tags"
    private val pref: SharedPreferences = context.getSharedPreferences(prefsFilename, Context.MODE_PRIVATE)

    fun saveMyLoc(context: Context, loc: String) {
        val edit = pref.edit() // 수정모드
        edit.putString("keyLoc", loc) // 값 넣기
        edit.apply() // 적용하기
    }

    fun getMyLoc(context: Context): String {
        return pref.getString("keyLoc", "후문")!!
    }

    fun saveMyMenu(context: Context, menu: String) {
        val edit = pref.edit() // 수정모드
        edit.putString("keyMenu", menu) // 값 넣기
        edit.apply() // 적용하기
    }

    fun getMyMenu(context: Context): String {
        return pref.getString("keyMenu", "다 좋아")!!
    }

}