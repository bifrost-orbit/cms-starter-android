package com.northwilds.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable

@Serializable
data class PageRoute(val path: String = "/")

val LocalOpenLink = staticCompositionLocalOf<(String) -> Unit> { {} }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val navController = rememberNavController()
                val uriHandler = LocalUriHandler.current
                val openLink: (String) -> Unit = { href ->
                    if (href.startsWith("/")) navController.navigate(PageRoute(href)) else uriHandler.openUri(href)
                }
                CompositionLocalProvider(LocalOpenLink provides openLink) {
                    NavHost(navController, startDestination = PageRoute()) {
                        composable<PageRoute> { entry ->
                            PageScreen(
                                path = entry.toRoute<PageRoute>().path,
                                onBack = navController.previousBackStackEntry?.let { { navController.popBackStack() } },
                            )
                        }
                    }
                }
            }
        }
    }
}
