package com.ar.sebastiangomez.steam.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ar.sebastiangomez.steam.R

class ThemeHelper(private val context: Context) {
    private val preferences: SharedPreferences = context.getSharedPreferences("ThemePrefs", Context.MODE_PRIVATE)

    fun applyTheme() {
        val theme = preferences.getString("theme", "light") ?: "light"
        Log.d("Theme: ", theme)

        when (theme) {
            "light" -> context.setTheme(R.style.Base_Theme_Steam_Light)
            "dark" -> context.setTheme(R.style.Base_Theme_Steam_Dark)
        }
    }

    fun changeTheme(theme: String) {
        val editor = preferences.edit()
        editor.putString("theme", theme)
        editor.apply()

        (context as? AppCompatActivity)?.recreate() // Recrea la actividad para aplicar el nuevo tema
    }
}
