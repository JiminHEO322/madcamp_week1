package com.example.exhibition

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.exhibition.databinding.ActivityMainBinding
import com.example.exhibition.ui.place.PlaceAdapter
import com.example.exhibition.ui.place.PlaceFragment
import android.graphics.Color
import android.content.res.ColorStateList
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, PlaceFragment())
                .commit()

            val navView: BottomNavigationView = binding.navView

            val navController = findNavController(R.id.nav_host_fragment_activity_main)
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
            val appBarConfiguration = AppBarConfiguration(
                setOf(
                    R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
                )
            )
            setupActionBarWithNavController(navController, appBarConfiguration)
            navView.setupWithNavController(navController)

            navController.addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.navigation_dashboard -> { // 두 번째 탭
                        navView.setBackgroundColor(Color.TRANSPARENT) // 투명
                        navView.itemIconTintList = ColorStateList.valueOf(Color.WHITE)
                        navView.itemTextColor = ColorStateList.valueOf(Color.WHITE) // 텍스트 흰색
                    }
                    else -> {
                        navView.setBackgroundColor(getColor(R.color.white)) // 기본 색상
                        navView.itemIconTintList = ColorStateList.valueOf(Color.BLACK)
                        navView.itemTextColor = ColorStateList.valueOf(Color.BLACK)// 기본 텍스트 색상
                    }
                }
            }
        }

    }
}