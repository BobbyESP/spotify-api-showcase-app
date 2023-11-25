package com.bobbyesp.spotifyapishowcaseapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import com.bobbyesp.spotifyapishowcaseapp.ui.Navigation
import com.bobbyesp.spotifyapishowcaseapp.ui.common.AppLocalSettingsProvider
import com.bobbyesp.spotifyapishowcaseapp.ui.common.LocalDarkTheme
import com.bobbyesp.spotifyapishowcaseapp.ui.theme.SpotifyAPIShowcaseAppTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        ViewCompat.setOnApplyWindowInsetsListener(window.decorView) { view, insets ->
            view.setPadding(0, 0, 0, 0)
            insets
        }
        activity = this

        setContent {
            val windowSizeClass = calculateWindowSizeClass(this)
            AppLocalSettingsProvider(windowWidthSize = windowSizeClass.widthSizeClass) {
                SpotifyAPIShowcaseAppTheme(
                    darkTheme = LocalDarkTheme.current.isDarkTheme(),
                    isHighContrastModeEnabled = LocalDarkTheme.current.isHighContrastModeEnabled,
                ) {
                    Navigation()
                }
            }
        }
    }

    companion object {
        private const val TAG = "MainActivity"
        private lateinit var activity: MainActivity
        fun getActivity(): MainActivity {
            return activity
        }
    }
}