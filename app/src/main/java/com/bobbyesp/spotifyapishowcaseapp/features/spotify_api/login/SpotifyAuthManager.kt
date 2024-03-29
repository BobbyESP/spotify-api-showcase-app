package com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login

import com.adamratzman.spotify.SpotifyClientApi

interface SpotifyAuthManager {
    suspend fun createCredentials(): Boolean
    fun launchLoginActivity()
    fun getSpotifyClientApi(): SpotifyClientApi?
    suspend fun isAuthenticated(): Boolean
    fun shouldRefreshToken(): Boolean
    suspend fun refreshToken(): Boolean
    fun credentialsFileExists(): Boolean
    fun deleteCredentials(): Boolean
}