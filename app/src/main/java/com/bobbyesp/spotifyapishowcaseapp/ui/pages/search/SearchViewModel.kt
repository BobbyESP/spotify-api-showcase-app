package com.bobbyesp.spotifyapishowcaseapp.ui.pages.search

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.adamratzman.spotify.models.Artist
import com.adamratzman.spotify.models.SimpleAlbum
import com.adamratzman.spotify.models.SimplePlaylist
import com.adamratzman.spotify.models.Track
import com.bobbyesp.spotifyapishowcaseapp.R
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.app.ArtistsPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.app.SimpleAlbumPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.app.SimplePlaylistPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.app.TrackPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.client.ArtistsClientPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.client.SimpleAlbumsClientPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.client.SimplePlaylistsClientPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.data.remote.paging.client.TracksClientPagingSource
import com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login.SpotifyAuthManager
import com.bobbyesp.spotifyapishowcaseapp.util.pagination.createPager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@SuppressLint("StaticFieldLeak")
@HiltViewModel
class SearchViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {
    private val isLoggedIn = runBlocking(Dispatchers.IO) { spotifyAuthManager.isAuthenticated() }
    private val mutablePageViewState = MutableStateFlow(PageViewState())
    val pageViewState = mutablePageViewState.asStateFlow()

    private var searchJob: Job? = null

    private val clientApi = spotifyAuthManager.getSpotifyClientApi()

    data class PageViewState(
        val searchViewState: SearchViewState = SearchViewState.Idle,
        val query: String = "",
        val activeSearchType: SpotifyItemType = SpotifyItemType.TRACKS,
        val searchedTracks: Flow<PagingData<Track>>? = null,
        val searchedAlbums: Flow<PagingData<SimpleAlbum>>? = null,
        val searchedArtists: Flow<PagingData<Artist>>? = null,
        val searchedPlaylists: Flow<PagingData<SimplePlaylist>>? = null,
    )

    private fun chooseSearchType(spotifyItemType: SpotifyItemType) {
        val actualFilter = pageViewState.value.activeSearchType
        if (actualFilter == spotifyItemType) {
            return
        } else {
            mutablePageViewState.update {
                it.copy(
                    activeSearchType = spotifyItemType
                )
            }
        }
    }

    fun chooseSearchTypeAndSearch(searchType: SpotifyItemType) {
        chooseSearchType(searchType)
        viewModelScope.launch {
            if (pageViewState.value.query.isNotEmpty()) search(
                searchType = searchType
            )
        }
    }

    suspend fun search(
        searchType: SpotifyItemType = pageViewState.value.activeSearchType,
    ) {
        val query = pageViewState.value.query
        searchJob?.cancel()
        updateViewState(SearchViewState.Loading)
        searchJob = viewModelScope.launch {
            runCatching {
                when (searchType) {
                    SpotifyItemType.TRACKS -> {
                        getTracksPaginatedData(query = query)
                    }

                    SpotifyItemType.ALBUMS -> {
                        getAlbumsPaginatedData(query = query)
                    }

                    SpotifyItemType.ARTISTS -> {
                        getArtistsPaginatedData(query = query)
                    }

                    SpotifyItemType.PLAYLISTS -> {
                        getSimplePaginatedData(query = query)
                    }
                }
            }.onSuccess {
                updateViewState(SearchViewState.Success)
            }.onFailure {
                updateViewState(SearchViewState.Error(it))
            }
        }
    }

    //********************* CLIENT ***********************//
    private suspend fun getTracksPaginatedData(query: String) {
        val tracksPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
            pagingSourceFactory = { api ->
                TracksClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                TrackPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )
        mutablePageViewState.update {
            it.copy(
                searchedTracks = tracksPager
            )
        }
    }

    private fun getAlbumsPaginatedData(query: String) {
        val albumsPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
            pagingSourceFactory = { api ->
                SimpleAlbumsClientPagingSource(
                    spotifyApi = api,
                    query = query
                )
            },
            nonLoggedSourceFactory = {
                SimpleAlbumPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )
        mutablePageViewState.update {
            it.copy(
                searchedAlbums = albumsPager
            )
        }
    }

    private fun getSimplePaginatedData(query: String) {
        val playlistsPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
            pagingSourceFactory = { api ->
                SimplePlaylistsClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                SimplePlaylistPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )
        mutablePageViewState.update {
            it.copy(
                searchedPlaylists = playlistsPager
            )
        }
    }

    private fun getArtistsPaginatedData(query: String) {
        val artistsPager = createPager(
            clientApi = clientApi,
            isLogged = isLoggedIn,
            pagingSourceFactory = { api ->
                ArtistsClientPagingSource(
                    spotifyApi = api,
                    query = query,
                )
            },
            nonLoggedSourceFactory = {
                ArtistsPagingSource(
                    spotifyApi = null,
                    query = query,
                )
            },
            coroutineScope = viewModelScope,
        )

        if (artistsPager == null) {
            updateViewState(SearchViewState.Error(Exception("artistsPager is null")))
        } else {
            mutablePageViewState.update {
                it.copy(
                    searchedArtists = artistsPager
                )
            }
        }
    }

    private fun updateViewState(searchViewState: SearchViewState) {
        mutablePageViewState.update {
            it.copy(
                searchViewState = searchViewState,
            )
        }
    }

    fun updateQuery(query: String) {
        mutablePageViewState.update {
            it.copy(
                query = query,
            )
        }
    }

    companion object {
        sealed class SearchViewState {
            data object Idle : SearchViewState()
            data object Loading : SearchViewState()
            data object Success : SearchViewState()
            data class Error(val error: Throwable) : SearchViewState()
        }
    }
}

enum class SpotifyItemType {
    TRACKS,
    ALBUMS,
    ARTISTS,
    PLAYLISTS;

    @Composable
    fun toComposableStringPlural(): String {
        return when (this) {
            TRACKS -> stringResource(id = R.string.tracks)
            ALBUMS -> stringResource(id = R.string.albums)
            ARTISTS -> stringResource(id = R.string.artists)
            PLAYLISTS -> stringResource(id = R.string.playlists)
        }
    }

    @Composable
    fun toComposableStringSingular(): String {
        return when (this) {
            TRACKS -> stringResource(id = R.string.track)
            ALBUMS -> stringResource(id = R.string.album)
            ARTISTS -> stringResource(id = R.string.artist)
            PLAYLISTS -> stringResource(id = R.string.playlist)
        }
    }
}

fun String.toSpotifyItemType(): SpotifyItemType {
    return when (this) {
        "track" -> SpotifyItemType.TRACKS
        "album" -> SpotifyItemType.ALBUMS
        "artist" -> SpotifyItemType.ARTISTS
        "playlist" -> SpotifyItemType.PLAYLISTS
        else -> throw IllegalArgumentException("String $this is not a valid SpotifyItemType")
    }
}