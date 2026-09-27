package com.tanishq.alphabetlauncher

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.content.ContextCompat
import com.tanishq.alphabetlauncher.ui.AlphabetLauncherScreen
import com.tanishq.alphabetlauncher.ui.LauncherViewModel

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = android.graphics.Color.BLACK
        window.navigationBarColor = android.graphics.Color.BLACK

        val viewModel = LauncherViewModel(applicationContext)

        setContent {
            MaterialTheme(
                colorScheme = androidx.compose.material3.darkColorScheme()
            ) {
                Surface(
                    modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                    color = androidx.compose.ui.graphics.Color.Black
                ) {
                    AlphabetLauncherScreen(
                        viewModel = viewModel,
                        onLaunchApp = { app ->
                            try {
                                startActivity(app.launchIntent)
                            } catch (_: Exception) {
                                Toast.makeText(
                                    this,
                                    "Unable to open ${app.label}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }
        }
    }
}
