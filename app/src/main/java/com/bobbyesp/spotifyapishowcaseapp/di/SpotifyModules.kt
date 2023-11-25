package com.bobbyesp.spotifyapishowcaseapp.di

import android.content.Context
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login.SpotifyAuthManager
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login.SpotifyAuthManagerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SpotifyModules {
    @Provides
    @Singleton
    fun provideSpotifyAuthManager(applicationContext: Context): SpotifyAuthManager {
        return SpotifyAuthManagerImpl(applicationContext)
    }
}