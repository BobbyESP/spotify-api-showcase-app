package com.bobbyesp.spotifyapishowcaseapp.ui

import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import com.bobbyesp.spotifyapishowcaseapp.ui.common.LocalNavController
import com.bobbyesp.spotifyapishowcaseapp.ui.common.animatedComposable
import com.bobbyesp.spotifyapishowcaseapp.ui.pages.login.LoginPage
import com.bobbyesp.spotifyapishowcaseapp.ui.pages.login.LoginPageViewModel
import com.bobbyesp.spotifyapishowcaseapp.ui.pages.search.SearchPage
import com.bobbyesp.spotifyapishowcaseapp.ui.pages.search.SearchViewModel
import com.bobbyesp.spotifyapishowcaseapp.ui.pages.utilities.UtilitiesPage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun Navigation() {
    val navController = LocalNavController.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val loginViewModel = hiltViewModel<LoginPageViewModel>()
    var isLoggedIn: Boolean? by remember { mutableStateOf(null) }

    val routesToShowInBottomBar: List<Route> = remember {
        listOf(
            Route.HomeNavigator,
            Route.SearchNavigator,
            Route.UtilitiesNavigator
        )
    }

    val currentRootRoute = rememberSaveable(navBackStackEntry, key = "currentRootRoute") {
        mutableStateOf(
            navBackStackEntry?.destination?.parent?.route ?: Route.MainHost.route
        )
    }

    val currentRoute by remember {
        mutableStateOf(navBackStackEntry?.destination?.route)
    }

    LaunchedEffect(true) {
        isLoggedIn = withContext(Dispatchers.IO) { loginViewModel.isLogged() }
    }

    val routesIfLogged = if (isLoggedIn == true) {
        Route.HomeNavigator.route
    } else {
        Route.LoginNavigator.route
    }


    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isLoggedIn, label = "Crossfade between login verification and navigator"
        ) {
            when (it) {
                null -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }

                else -> {
                    NavHost(
                        modifier = Modifier
                            .fillMaxSize()
                            .apply {
                                if (isLoggedIn == true) this.padding(bottom = 72.dp)
                            },
                        navController = navController,
                        route = Route.MainHost.route,
                        startDestination = routesIfLogged,
                    ) {
                        navigation(
                            route = Route.LoginNavigator.route,
                            startDestination = Route.Login.route,
                        ) {
                            animatedComposable(Route.Login.route) {
                                LoginPage(loginViewModel)
                            }
                        }

                        navigation(
                            route = Route.HomeNavigator.route,
                            startDestination = Route.Home.route,
                        ) {
                            animatedComposable(Route.Home.route) {
                                //MainPage()
                            }
                        }

                        navigation(
                            route = Route.SearchNavigator.route,
                            startDestination = Route.Search.route
                        ) {
                            animatedComposable(Route.Search.route) {
                                val viewModel = hiltViewModel<SearchViewModel>()
                                SearchPage(viewModel = viewModel)
                            }
                        }

                        navigation(
                            route = Route.UtilitiesNavigator.route,
                            startDestination = Route.Utilities.route
                        ) {
                            animatedComposable(
                                route = Route.Utilities.route
                            ) {
                                UtilitiesPage()
                            }
                        }
                    }
                }
            }
        }
        if (currentRootRoute.value != Route.LoginNavigator.route && isLoggedIn == true) {
            NavigationBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .height(90.dp)
            ) {
                routesToShowInBottomBar.forEach { route ->
                    val isSelected = currentRootRoute.value == route.route

                    val onClick = remember(isSelected, navController, route.route) {
                        {
                            if (!isSelected) {
                                navController.navigate(route.route) {
                                    popUpTo(Route.MainHost.route) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                    }
                    NavigationBarItem(
                        modifier = Modifier.animateContentSize(),
                        selected = isSelected,
                        onClick = onClick,
                        icon = {
                            Icon(
                                imageVector = route.icon ?: return@NavigationBarItem,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        },
                        label = {
                            Text(
                                text = route.title ?: "",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        },
                        alwaysShowLabel = false,
                        enabled = true
                    )
                }
            }
        }
    }
}