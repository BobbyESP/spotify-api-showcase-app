package com.bobbyesp.spotifyapishowcaseapp.features.spotify_api.login

import android.app.Activity

class ActivityCallsShortener(
    private val activity: Activity
) {
    fun <T> executeInActivityContext(action: T): T {
        return action
    }

    fun <T> executeInActivityContext(action: (Activity) -> T): T {
        return action(activity)
    }

    fun execute(action: Activity.() -> Unit) {
        activity.action()
    }
}