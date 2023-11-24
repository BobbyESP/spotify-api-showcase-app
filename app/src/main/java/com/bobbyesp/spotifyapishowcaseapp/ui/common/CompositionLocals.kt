package com.bobbyesp.spotifyapishowcaseapp.ui.common

import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bobbyesp.spotifyapishowcaseapp.util.DarkThemePreference
import com.bobbyesp.spotifyapishowcaseapp.util.PreferencesUtil.AppSettingsStateFlow

val LocalDarkTheme = compositionLocalOf { DarkThemePreference() }
val LocalDynamicColorSwitch = compositionLocalOf { false }
val LocalIndexOfPaletteStyle = compositionLocalOf { 0 }
val LocalWindowWidthState =
    staticCompositionLocalOf { WindowWidthSizeClass.Compact } //This value probably will never change, that's why it is static
val LocalNavController =
    compositionLocalOf<NavHostController> { error("No nav controller provided") }

@Composable
fun AppLocalSettingsProvider(
    windowWidthSize: WindowWidthSizeClass,
    content: @Composable () -> Unit
) {
    val appSettingsState = AppSettingsStateFlow.collectAsState().value
    val navController = rememberNavController()

    appSettingsState.run {
        CompositionLocalProvider(
            LocalDarkTheme provides darkTheme, //Tells the app if it should use dark theme or not
            LocalWindowWidthState provides windowWidthSize, //Tells the app what is the current width of the window
            LocalNavController provides navController, //Tells the app what is the current nav controller
        ) {
            content() //The content of the app
        }
    }
}