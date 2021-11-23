package com.example.bot_client_v2

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.bot_client_v2.databinding.ActivityMainBinding
import com.example.bot_client_v2.source.NetClock
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindNavigation()

        NetClock.autoLogin(this)
    }

    private fun bindNavigation() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
    }

    public fun changeLoginText(isConnected: Boolean) {
        if (isConnected) {
            main_login_text.text = getText(R.string.main_connected)
            main_login_text.setBackgroundColor(ContextCompat.getColor(this, R.color.green))
        } else {
            main_login_text.text = getText(R.string.main_not_connected)
            main_login_text.setBackgroundColor(ContextCompat.getColor(this, R.color.red))
        }
    }
}