package com.tanishq.alphabetlauncher.model

import android.content.Intent
import android.graphics.drawable.Drawable

data class LaunchableApp(
    val label: String,
    val packageName: String,
    val activityName: String,
    val icon: Drawable,
    val launchIntent: Intent
)
