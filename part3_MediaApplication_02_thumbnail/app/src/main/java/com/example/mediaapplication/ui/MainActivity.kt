package com.example.mediaapplication.ui

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.example.mediaapplication.data.repository.MediaRepository
import com.example.mediaapplication.ui.theme.MediaApplicationTheme
import com.example.mediaapplication.viewmodel.MediaViewModel
import com.example.mediaapplication.viewmodel.MediaViewModelFactory

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
                Scaffold(modifier = Modifier.fillMaxSize()) {
                    MediaContentListScreen(mediaViewModel)
                }
            }
        }
    }
}
