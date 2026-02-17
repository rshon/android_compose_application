package com.example.mediaapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.navigation.NavHost
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mediaapplication.data.repository.MediaRepository
import com.example.mediaapplication.ui.theme.MediaApplicationTheme
import com.example.mediaapplication.viewmodel.MediaViewModel
import com.example.mediaapplication.viewmodel.MediaViewModelFactory
import java.net.URLDecoder
import java.net.URLEncoder

class MainActivity : ComponentActivity() {
    private val mediaViewModel: MediaViewModel by viewModels {
        MediaViewModelFactory(MediaRepository())
    }

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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
                                println("Navigating to: image_viewer/$encodedFilePath") // 디버깅 로그
                                navController.navigate("image_viewer/$encodedFilePath")
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
                        println("Received filePath: $filePath") // 디버깅 로그
                        ImageViewerScreen(filePath = filePath, onBackClick = { navController.popBackStack() })
                    }
                }
            }
        }
    }
}