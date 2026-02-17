package com.example.mediaapplication.ui.video

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.mediaapplication.viewmodel.VideoPlayerViewModel
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import android.view.SurfaceHolder
import androidx.annotation.OptIn
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.AspectRatioFrameLayout
import com.example.mediaapplication.data.player.PlayerEngineType

@OptIn(UnstableApi::class)
@Composable
fun PlayerRenderer(
    viewModel: VideoPlayerViewModel,
    modifier: Modifier = Modifier
) {
    when (viewModel.playerType) {
        PlayerEngineType.EXO_PLAYER -> {
            val uiState by viewModel.uiState.collectAsState()
            var playerView by remember { mutableStateOf<PlayerView?>(null) }

            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        playerView = this

                        try {
                            player = viewModel.getPlayer() as? androidx.media3.common.Player
                        } catch (e: UnsupportedOperationException) {
                            Log.e("VideoPlayerScreen", "Cannot set MediaPlayer to PlayerView directly.", e)
                        }
                        useController = false
                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                        alpha = 0f
                    }
                },
                modifier = modifier.fillMaxSize()
            )

            // shows PlayerView after setting video size
            LaunchedEffect(uiState.videoSize) {
                val (videoWidth, videoHeight) = uiState.videoSize

                if (videoWidth > 0 && videoHeight > 0) {
                    Log.d("PlayerRenderer", "Video size available: ${videoWidth}x${videoHeight}, showing PlayerView")
//                    playerView?.animate()?.alpha(1f)?.setDuration(200)?.start() // white bg problem
                    playerView?.alpha = 1f
                }
            }

            // hides PlayerView when new URI has been set
            LaunchedEffect(uiState.currentUri) {
                Log.d("PlayerRenderer", "New URI: ${uiState.currentUri}, hiding PlayerView")
                playerView?.alpha = 0f
            }
        }
        PlayerEngineType.MEDIA_PLAYER -> {
            println("PlayerEngineType.MEDIA_PLAYER")
            val uiState by viewModel.uiState.collectAsState()

            Box(
                modifier = modifier,
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        AspectRatioSurfaceView(ctx).apply {
                            holder.addCallback(object : SurfaceHolder.Callback {
                                override fun surfaceCreated(holder: SurfaceHolder) {
                                    Log.d("PlayerRenderer", "Surface created")

                                    viewModel.setSurfaceFromNative(holder.surface)
                                }

                                override fun surfaceChanged(
                                    holder: SurfaceHolder,
                                    format: Int,
                                    width: Int,
                                    height: Int
                                ) {
                                    Log.d("PlayerRenderer", "Surface changed: ${width}x${height}")
                                    viewModel.setSurfaceFromNative(holder.surface)
                                }

                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    Log.d("PlayerRenderer", "Surface destroyed")
                                    viewModel.setSurfaceFromNative(null)
                                }
                            })

                            // Set surface type for better video rendering
                            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                        }
                    },
                    update = { surfaceView ->
                        val (vw, vh) = uiState.videoSize  // refers StateFlow directly!

                        Log.d("PlayerRenderer", "♢ update() – videoSize=$vw×$vh")
                        if (vw > 0 && vh > 0) {
                            surfaceView.setVideoSize(vw, vh)
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}

class AspectRatioSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr) {

    private var videoWidth = 0
    private var videoHeight = 0

    /**
     * 비디오의 실제 해상도를 ViewModel로부터 전달받으면 호출하여
     * 내부 상태를 저장하고 레이아웃 갱신(requestLayout)합니다.
     */
    fun setVideoSize(width: Int, height: Int) {
        Log.d("AspectRatioSurfaceView", ": ${width}x${height}")

        if (width > 0 && height > 0) {
            videoWidth = width
            videoHeight = height
            Log.d("AspectRatioSurfaceView", "Video size set to: ${width}x${height}")
            requestLayout()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 1) 부모가 허용한 최대 크기 (종종 화면 전체 크기나 부모 박스 크기)
        val specWidth = MeasureSpec.getSize(widthMeasureSpec)
        val specHeight = MeasureSpec.getSize(heightMeasureSpec)

        println("PlayerRenderer, onMeasure: $videoWidth x $videoHeight")

        // 2) 비디오 원본 크기가 유효할 때만 비율 계산
        if (videoWidth > 0 && videoHeight > 0) {
            val videoRatio = videoWidth.toFloat() / videoHeight.toFloat()
            val viewRatio = specWidth.toFloat() / specHeight.toFloat()

            if (videoRatio > viewRatio) {
                // 비디오가 화면보다 "더 넓은(가로가 긴)" 경우 → 폭에 맞추고 높이를 줄임
                val fittedHeight = (specWidth / videoRatio).toInt()
                setMeasuredDimension(specWidth, fittedHeight)
            } else {
                // 비디오가 화면보다 "더 좁거나(세로가 긴) 같은" 경우 → 높이에 맞추고 폭을 줄임
                val fittedWidth = (specHeight * videoRatio).toInt()
                setMeasuredDimension(fittedWidth, specHeight)
            }
        } else {
            // 비디오 해상도를 아직 모르면 부모가 지정한 전체 크기를 그대로 사용
            setMeasuredDimension(specWidth, specHeight)
        }

        Log.d("AspectRatioSurfaceView",
            "onMeasure() spec=(${specWidth}×${specHeight}) " +
                    "=> measured=(${measuredWidth}×${measuredHeight})"
        )
    }
}