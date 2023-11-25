package com.bobbyesp.spotifyapishowcaseapp

import android.annotation.SuppressLint
import android.app.Application
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.os.Build
import androidx.core.content.getSystemService
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Inject

@HiltAndroidApp
class App: Application() {

    @Inject
    @ApplicationContext
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
        packageInfo = packageManager.run {
            if (Build.VERSION.SDK_INT >= 33) getPackageInfo(
                packageName, PackageManager.PackageInfoFlags.of(0)
            ) else
                getPackageInfo(packageName, 0)
        }
        applicationScope = CoroutineScope(SupervisorJob())
        appContext = applicationContext
        clipboard = getSystemService()!!
        connectivityManager = getSystemService()!!
    }

    companion object {
        @SuppressLint("StaticFieldLeak") lateinit var appContext: Context
        lateinit var applicationScope: CoroutineScope
        lateinit var clipboard: ClipboardManager
        lateinit var connectivityManager: ConnectivityManager
        lateinit var packageInfo: PackageInfo
    }
}