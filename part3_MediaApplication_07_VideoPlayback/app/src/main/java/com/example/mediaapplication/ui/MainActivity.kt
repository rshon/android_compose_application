package com.example.mediaapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mediaapplication.ui.theme.MediaApplicationTheme
import com.example.mediaapplication.viewmodel.MediaViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.net.URLDecoder
import java.net.URLEncoder

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter", "UnrememberedGetBackStackEntry")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MediaApplicationTheme {
                val navController = rememberNavController()

                MainNaviGraph(navController)
            }
        }
    }
}

@Composable
fun MainNaviGraph(navController: NavHostController) {
    val sharedViewModel: MediaViewModel = hiltViewModel()

    NavHost(
        navController = navController,
        startDestination = "media_list"
    ) {
        composable("media_list") {
            MediaContentListScreen(
                viewModel = sharedViewModel,
                onImageClick = { filePath ->
                    val encodedFilePath = URLEncoder.encode(filePath, "UTF-8")
                    println("Navigating to: image_viewer/$encodedFilePath")
                    navController.navigate("image_viewer/$encodedFilePath")
                },
                onMusicClick = { filePath ->
                    val encodedFilePath = URLEncoder.encode(filePath, "UTF-8")
                    println("Navigating to: music_player/$encodedFilePath")
                    navController.navigate("music_player/$encodedFilePath")
                },
                onVideoClick = { filePath -> // ← 새로 추가한 콜백
                    val encoded = URLEncoder.encode(filePath, "UTF-8")
                    navController.navigate("video_player/$encoded")
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
                viewModel = sharedViewModel,
                filePath = filePath,
                onBackClick = { navController.popBackStack() }
            )
        }
        composable(
            route = "video_player/{filePath}",
            arguments = listOf(navArgument("filePath") { type = NavType.StringType })
        ) { backStackEntry ->
//            val fp = URLDecoder.decode(backStackEntry.arguments!!.getString("filePath")!!, "UTF-8")

            val filePath = backStackEntry.arguments?.getString("filePath")?.let {
                URLDecoder.decode(it, "UTF-8")
            } ?: ""

            VideoPlayerScreen(
                viewModel = sharedViewModel,
                navController = navController,
                uri = filePath,
                onBack = {
                    println("back from video screen")
                    navController.popBackStack()
                }
            )
        }
    }
}
