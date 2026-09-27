package com.tanishq.alphabetlauncher.data

import android.content.Context
import android.content.Intent
import android.os.Build
import com.tanishq.alphabetlauncher.model.LaunchableApp

class AppRepository(private val context: Context) {

    fun loadLaunchableApps(): List<LaunchableApp> {
        val packageManager = context.packageManager

        val launcherIntent = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val results = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.queryIntentActivities(
                launcherIntent,
                android.content.pm.PackageManager.ResolveInfoFlags.of(
                    android.content.pm.PackageManager.MATCH_ALL.toLong()
                )
            )
        } else {
            @Suppress("DEPRECATION")
            packageManager.queryIntentActivities(
                launcherIntent,
                android.content.pm.PackageManager.MATCH_ALL
            )
        }

        return results
            .mapNotNull { info ->
                val activityInfo = info.activityInfo ?: return@mapNotNull null
                val label = info.loadLabel(packageManager)?.toString()?.trim()
                    ?.takeIf { it.isNotEmpty() }
                    ?: return@mapNotNull null

                val launchIntent = packageManager.getLaunchIntentForPackage(
                    activityInfo.packageName
                ) ?: return@mapNotNull null

                LaunchableApp(
                    label = label,
                    packageName = activityInfo.packageName,
                    activityName = activityInfo.name,
                    icon = info.loadIcon(packageManager),
                    launchIntent = launchIntent
                )
            }
            .distinctBy { it.packageName }
            .sortedBy { it.label.lowercase() }
    }
}
