package com.example.mediaapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mediaapplication.data.repository.MediaRepository
import com.example.mediaapplication.domain.usecase.PlayMusicUseCase
import com.example.mediaapplication.ui.theme.MediaApplicationTheme
import com.example.mediaapplication.viewmodel.MediaViewModel
import com.example.mediaapplication.viewmodel.MediaViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    private val mediaViewModel: MediaViewModel by viewModels {
        MediaViewModelFactory(MediaRepository(), PlayMusicUseCase())
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        mediaViewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        setContent {
            MediaApplicationTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "media_list"
                ) {
                    composable("media_list") {
                        MediaContentListScreen(
                            viewModel = mediaViewModel,
                            onImageClick = { filePath ->
                                val encodedFilePath = URLEncoder.encode(filePath, "UTF-8")
                                println("Navigating to: image_viewer/$encodedFilePath")
                                navController.navigate("image_viewer/$encodedFilePath")
                            },
                            onMusicClick = { filePath ->
                                val encodedFilePath = URLEncoder.encode(filePath, "UTF-8")
                                println("Navigating to: music_player/$encodedFilePath")
                                navController.navigate("music_player/$encodedFilePath")
                            }
                        )
                    }
                    composable(
                        route = "image_viewer/{filePath}",
                        arguments = listOf(navArgument("filePath") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val filePath = backStackEntry.arguments?.getString("filePath")?.let {
                            URLDecoder.decode(it, "UTF-8")
                        } ?: ""
                        ImageViewerScreen(filePath = filePath, onBackClick = { navController.popBackStack() })
                    }
                    composable(
                        route = "music_player/{filePath}",
                        arguments = listOf(navArgument("filePath") { type = NavType.StringType })
                    ) { backStackEntry ->
                        val filePath = backStackEntry.arguments?.getString("filePath")?.let {
                            URLDecoder.decode(it, "UTF-8")
                        } ?: ""
                        MusicPlayerScreen(
                            viewModel = mediaViewModel,
                            filePath = filePath,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
