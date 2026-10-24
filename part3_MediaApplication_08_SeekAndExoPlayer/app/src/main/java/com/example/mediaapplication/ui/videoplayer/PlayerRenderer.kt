package com.example.mediaapplication.ui.videoplayer

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.example.mediaapplication.viewmodel.PlayerEngineType
import com.example.mediaapplication.viewmodel.VideoPlayerViewModel

@Composable
fun PlayerRenderer(
    viewModel: VideoPlayerViewModel,
    modifier: Modifier = Modifier
) {
    when (viewModel.playerType) {
        PlayerEngineType.EXO_PLAYER -> {
            AndroidView(
                factory = { ctx ->
                    PlayerView(ctx).apply {
                        try {
                            player = viewModel.getPlayer() // ExoPlayer 인스턴스
                        } catch (e: UnsupportedOperationException) {
                            // MediaPlayer 사용 시 PlayerView에 직접 설정 불가.
                            // 이 경우 SurfaceView 등을 사용하고 MediaPlayer에 직접 Surface를 설정해야 함.
                            // 여기서는 간단히 로그만 남기거나, 대체 UI 로직이 필요.
                            Log.e("VideoPlayerScreen", "Cannot set MediaPlayer to PlayerView directly.", e)
                            // TODO: MediaPlayer 사용 시 대체 비디오 출력 방법 구현 (예: SurfaceView 사용)
                        }
                        useController = false  // custom control 사용으로 ExoPlayer control 사용 X
                    }
                },
                modifier = Modifier
                    .fillMaxSize()
            )
        }
        PlayerEngineType.MEDIA_PLAYER -> {
            println("PlayerEngineType.MEDIA_PLAYER")

            val videoSizeState by viewModel.videoSize.collectAsState()

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
                                    viewModel.setSurface(holder.surface)
                                }

                                override fun surfaceChanged(
                                    holder: SurfaceHolder,
                                    format: Int,
                                    width: Int,
                                    height: Int
                                ) {
                                    Log.d("PlayerRenderer", "Surface changed: ${width}x${height}")
                                    viewModel.setSurface(holder.surface)
                                }

                                override fun surfaceDestroyed(holder: SurfaceHolder) {
                                    Log.d("PlayerRenderer", "Surface destroyed")
                                    viewModel.setSurface(null)
                                }
                            })

                            // Set surface type for better video rendering
                            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
                        }
                    },
                    update = { surfaceView ->
                        val (vw, vh) = videoSizeState  // refers StateFlow directly!

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

        Log.d(
            "AspectRatioSurfaceView",
            "onMeasure() spec=(${specWidth}×${specHeight}) " +
                "=> measured=(${measuredWidth}×${measuredHeight})"
        )
    }
}
