package com.bobbyesp.spotifyapishowcaseapp.ui.components.images

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.ImageLoader
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.ImageRequest
import okhttp3.OkHttpClient

@Composable
fun AsyncCoilImage(
    model: Any,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    error: Painter? = null,
    fallback: Painter? = null,
    onLoading: ((AsyncImagePainter.State.Loading) -> Unit)? = null,
    onSuccess: ((AsyncImagePainter.State.Success) -> Unit)? = null,
    onError: ((AsyncImagePainter.State.Error) -> Unit)? = null,
    alignment: Alignment = Alignment.Center,
    contentScale: ContentScale = ContentScale.Fit,
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
    filterQuality: FilterQuality = DrawScope.DefaultFilterQuality,
) {

    val context = LocalContext.current

    val imageLoader by lazy {
        ImageLoader.Builder(context).memoryCache {
            MemoryCache.Builder(context).maxSizePercent(0.05).build()
        }.diskCache {
            DiskCache.Builder().directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.2)
                .build()
        }.okHttpClient {
            OkHttpClient.Builder().build()
        }.build()
    }


    val imageRequest by lazy {
        ImageRequest.Builder(context).data(model)
            .crossfade(true).build()
    }

    AsyncImage(
        model = imageRequest,
        imageLoader = imageLoader,
        filterQuality = filterQuality,
        onError = onError,
        onLoading = onLoading,
        onSuccess = onSuccess,
        fallback = fallback,
        error = error,
        contentDescription = contentDescription,
        modifier = modifier,
        alignment = alignment,
        contentScale = contentScale,
        alpha = alpha,
        colorFilter = colorFilter,
    )
}