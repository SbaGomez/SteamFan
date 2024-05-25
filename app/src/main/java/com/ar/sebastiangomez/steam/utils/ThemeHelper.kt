package com.ar.sebastiangomez.steam.utils

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Color
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate

object ThemeHelper {
    fun setButtonImageBasedOnTheme(themeButton: ImageButton, context: Context) {
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
    }
}