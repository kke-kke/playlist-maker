package com.example.playlistmaker.ui.main.activity

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.ActivityMainBinding
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        val navHostFragment = supportFragmentManager.findFragmentById(R.id.container_view) as NavHostFragment
        val navController = navHostFragment.navController

        mainBinding.bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            mainBinding.bottomNavigationView.isVisible = destination.id != R.id.playerFragment
                    && destination.id != R.id.createPlaylistFragment
                    && destination.id != R.id.playlistInfoFragment
            mainBinding.navBarDivider.isVisible = destination.id != R.id.playerFragment
                    && destination.id != R.id.createPlaylistFragment
                    && destination.id != R.id.playlistInfoFragment
        }

        mainBinding.bottomNavigationView.setOnItemSelectedListener { item ->
            val navOptions = NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.hold)
                .setPopEnterAnim(R.anim.hold)
                .setPopExitAnim(R.anim.fade_out)
                .build()

            when (item.itemId) {
                R.id.searchFragment -> navController.navigate(R.id.searchFragment, null, navOptions)
                R.id.libraryFragment -> navController.navigate(R.id.libraryFragment, null, navOptions)
                R.id.settingsFragment -> navController.navigate(R.id.settingsFragment, null, navOptions)
            }
            true
        }

    }

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(updateLocale(base))
        applyOverrideConfiguration(base.resources.configuration)
    }

    private fun updateLocale(context: Context): Context? {
        val ruLocale = Locale("ru")
        Locale.setDefault(ruLocale)
        val configuration: Configuration = context.resources.configuration
        configuration.setLocale(ruLocale)
        configuration.setLayoutDirection(ruLocale)
        return context.createConfigurationContext(configuration)
    }
}