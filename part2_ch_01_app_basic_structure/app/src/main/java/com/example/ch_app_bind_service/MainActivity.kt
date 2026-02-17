package com.example.ch_app_bind_service

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.ch_app_bind_service.service.MusicService
import kotlinx.coroutines.flow.StateFlow

private const val READ_EXTERNAL_STORAGE_PERMISSION_CODE = 200

class MainActivity : ComponentActivity() {
    private var musicService: MusicService? = null
    private var isBound by mutableStateOf(false)
    private val musicUri = mutableStateOf<Uri?>(null)
    private val isPermissionGranted = mutableStateOf(false)
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicService.MusicBinder
            musicService = binder.service
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            musicService = null
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isPermissionGranted.value = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED

        if (isPermissionGranted.value) {
            musicUri.value = Uri.parse("/sdcard/06_너에게들려주고싶은이야기.mp3")
        } else {
            requestAppropriatePermissions()
        }

        setContent {
            val isPlaying by musicService?.isPlaying?.collectAsState() ?: mutableStateOf(false)

            MusicPlayerScreen(
                isPlaying = isPlaying,
                onPlay = { musicUri.value?.let { if (isBound && musicService != null) musicService?.playMusic(it) } },
                onPause = { if (isBound && musicService != null) musicService?.pauseMusic() },
                onStop = { if (isBound && musicService != null) musicService?.stopMusic() },
                onResume = { if (isBound && musicService != null) musicService?.resumeMusic() },
                isServiceBound = isBound,
                isPermissionGranted = isPermissionGranted.value,
                requestPermission = { requestAppropriatePermissions() }
            )
        }
    }

    private fun requestAppropriatePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                READ_EXTERNAL_STORAGE_PERMISSION_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == READ_EXTERNAL_STORAGE_PERMISSION_CODE) {
            android.util.Log.d("MainActivity", "grant results = ${grantResults.isNotEmpty()}, ${grantResults[0]}")
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                isPermissionGranted.value = true
                musicUri.value = Uri.parse("/sdcard/06_너에게들려주고싶은이야기.mp3")
            } else {
                Toast.makeText(this, "Permission to read external storage is required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, MusicService::class.java).also { intent ->
            bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(serviceConnection)
            isBound = false
            musicService = null
        }
    }
}

@Composable
fun MusicPlayerScreen(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onStop: () -> Unit,
    onResume: () -> Unit,
    isServiceBound: Boolean,
    isPermissionGranted: Boolean,
    requestPermission: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = if (isPlaying) "Playing..." else "Stopped")
        Spacer(modifier = Modifier.height(16.dp))

        if (!isPermissionGranted) {
            Text("Permission to read external storage is required to play music.")
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = requestPermission) {
                Text("Request permission")
            }

            return
        }

        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onPlay, enabled = isServiceBound && !isPlaying) {
                Text("Play")
            }
            Button(
                onClick = { if (isPlaying) onPause() else onResume() },
                enabled = isServiceBound
            ) {
                Text(if (isPlaying) "Pause" else "Resume")
            }
            Button(onClick = onStop, enabled = isServiceBound) {
                Text("Stop")
            }
        }
    }
}

