package com.bobbyesp.spotifyapishowcaseapp.ui.pages.login

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.adamratzman.spotify.auth.pkce.startSpotifyClientPkceLoginActivity
import com.bobbyesp.spotifyapishowcaseapp.MainActivity
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.SpotifyPkceLoginImpl
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login.ActivityCallsShortener
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login.SpotifyAuthManager
import com.bobbyesp.spotifyapishowcaseapp.util.PageStateWithThrowable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class LoginPageViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager
): ViewModel() {
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()
    private val activityWrapper = ActivityCallsShortener(MainActivity.getActivity())

    data class PageViewState(
        val state: PageStateWithThrowable = PageStateWithThrowable.Loading,
        val isTryingToLogin: Boolean = false,
        val loggedIn: Boolean = false
    )

    fun login() {
        try {
            updateLoginState(true)
            activityWrapper.execute {
                startSpotifyClientPkceLoginActivity(SpotifyPkceLoginImpl::class.java)
            }
            updateLoginState(false)
        } catch (e: Exception) {
            Log.e("HomePageViewModel", "Error logging in", e)
            updateLoginState(false)
            deleteEncryptedSharedPrefs()
        }
    }
    suspend fun getLoggedIn() {
        val logged = runBlocking(Dispatchers.IO) { spotifyAuthManager.isAuthenticated() }
        mutablePageViewState.update {
            it.copy(loggedIn = logged)
        }
    }

    suspend fun isLogged(): Boolean {
        return spotifyAuthManager.isAuthenticated()
    }

    private fun deleteEncryptedSharedPrefs() {
        spotifyAuthManager.deleteCredentials()
    }

    fun updateLoginState(isTryingToLogin: Boolean) {
        mutablePageViewState.update {
            it.copy(isTryingToLogin = isTryingToLogin)
        }
    }
    fun updateState(state: PageStateWithThrowable) {
        mutablePageViewState.update {
            it.copy(state = state)
        }
    }
}