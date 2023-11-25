package com.bobbyesp.spotifyapishowcaseapp.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Route(
    val route: String,
    val title: String?,
    val icon: ImageVector? = null,
) {
    //NAVIGATION HOSTS
    data object MainHost : Route("main_host", null, null)

    //NAVIGATORS
    data object LoginNavigator : Route("login_navigator", "Login", Icons.Default.AccountBox)
    data object HomeNavigator : Route("home_navigator", "Home", Icons.Default.Home)
    data object SearchNavigator: Route("search_navigator", "Search", Icons.Default.Search)
    data object UtilitiesNavigator: Route("utilities_navigator", "Utilities", Icons.Default.Star)

    //ROUTES
    data object Login : Route("login", "Login", Icons.Default.AccountBox)

    data object Home : Route("home", "Home", Icons.Default.Home)

    data object Search : Route("search", "Search", Icons.Default.Search)

    data object Utilities : Route("utilities", "Utilities", Icons.Default.Search)
}