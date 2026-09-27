package com.tanishq.alphabetlauncher.ui

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.tanishq.alphabetlauncher.data.AppRepository
import com.tanishq.alphabetlauncher.model.LaunchableApp

class LauncherViewModel(context: Context) : ViewModel() {

    private val repository = AppRepository(context.applicationContext)

    var apps by mutableStateOf<List<LaunchableApp>>(emptyList())
        private set

    init {
        apps = repository.loadLaunchableApps()
    }

    fun appsFor(letter: Char): List<LaunchableApp> {
        return apps.filter { it.label.firstOrNull()?.uppercaseChar() == letter }
    }

    fun favourites(): List<LaunchableApp> {
        // The assignment allows the first few installed apps as favourites.
        return apps.take(7)
    }

    fun hasApps(letter: Char): Boolean = apps.any {
        it.label.firstOrNull()?.uppercaseChar() == letter
    }
}
