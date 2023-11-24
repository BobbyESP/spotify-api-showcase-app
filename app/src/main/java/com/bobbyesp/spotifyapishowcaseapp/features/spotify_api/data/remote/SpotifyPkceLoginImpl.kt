package com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.adamratzman.spotify.SpotifyClientApi
import com.adamratzman.spotify.SpotifyScope
import com.adamratzman.spotify.auth.pkce.AbstractSpotifyPkceLoginActivity
import com.bobbyesp.spotifyapishowcaseapp.BuildConfig
import com.bobbyesp.spotifyapishowcaseapp.MainActivity
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.local.CredentialsStorer
import com.bobbyesp.spotifyapishowcaseapp.util.ToastUtil
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

internal var pkceClassBackTo: Class<out Activity>? = MainActivity::class.java //null or other

class SpotifyPkceLoginImpl : AbstractSpotifyPkceLoginActivity() {

    @ApplicationContext
    private val context: Context = MainActivity.getActivity().applicationContext

    override val clientId: String = BuildConfig.CLIENT_ID
    override val redirectUri: String = BuildConfig.SPOTIFY_REDIRECT_URI_PKCE
    override val scopes: List<SpotifyScope> = SpotifyScope.entries //We use all scopes
    override fun onSuccess(api: SpotifyClientApi) {
        val credentialStore = runBlocking(Dispatchers.IO) { CredentialsStorer().provideCredentials(context) }
        credentialStore.setSpotifyApi(api)
        val classToGoBackTo =
            pkceClassBackTo ?: throw IllegalStateException("No class to go back to")
        ToastUtil.makeToast(
            context,
            "Authentication via PKCE has completed. Launching ${classToGoBackTo.simpleName}.."
        )
        startActivity(Intent(this, classToGoBackTo))
    }

    override fun onFailure(exception: Exception) {
        exception.printStackTrace()
        ToastUtil.makeToast(context, "Auth failed: ${exception.message}")
    }

}