package com.ar.sebastiangomez.steam.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {

    private const val PREFS_NAME = "MyPrefsFile"
    private const val IS_DARK_THEME_ENABLED = "isDarkThemeEnabled"

    /*fun setButtonImageBasedOnTheme(themeButton: ImageButton, context: Context) {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        val currentTheme = if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) "dark" else "light"
        themeButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(if (currentTheme == "dark") "#914040" else "#EAC69C")))
    }

    fun toggleTheme(context: Context) {
        val currentNightMode = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        }
    }*/

    fun applyTheme(context: Context) {
        val isDarkThemeEnabled = isDarkThemeEnabled(context)

        if (isDarkThemeEnabled) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    fun saveThemeState(context: Context, isDarkThemeEnabled: Boolean) {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        sharedPreferences.edit().putBoolean(IS_DARK_THEME_ENABLED, isDarkThemeEnabled).apply()
    }

    fun isDarkThemeEnabled(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean(IS_DARK_THEME_ENABLED, false)
    }
}