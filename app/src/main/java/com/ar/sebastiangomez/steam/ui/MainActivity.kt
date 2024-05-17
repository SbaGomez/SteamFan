package com.ar.sebastiangomez.steam.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.ar.sebastiangomez.steam.R
import com.ar.sebastiangomez.steam.ui.fragment.BookmarkFragment
import com.ar.sebastiangomez.steam.ui.fragment.HomeFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadFragment(HomeFragment())
        bottomNav = findViewById(R.id.bottomNav) as BottomNavigationView
        bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.fabButton -> {
                    loadFragment(BookmarkFragment())
                    true
                }
                R.id.homeButton -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.settingsButton -> {
                    //loadFragment(SettingFragment())
                    true
                }

                else -> {false}
            }
        }
        bottomNav.selectedItemId = R.id.homeButton
    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.commit()
    }
}