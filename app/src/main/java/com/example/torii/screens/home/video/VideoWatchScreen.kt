package com.example.torii.screens.home.video

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.viewModel.VideoViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener

@Composable
fun VideoScreen(navController: NavHostController, videoId: String, videoViewModel: VideoViewModel = viewModel()) {

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    val viewModel: VideoViewModel = viewModel()
    val videoList = viewModel.videoList.value
    val video = videoList.find { it.videoId == videoId } ?: return

    val systemUiController = rememberSystemUiController()

    var currentTime by remember { mutableStateOf(0f) }
    val scrollState = rememberLazyListState()
    val youtubePlayer = remember { mutableStateOf<YouTubePlayer?>(null) }
    var highlightedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(currentTime) {
        val newIndex = video.subtitles.indexOfFirst {
            currentTime in it.timeStart..it.timeEnd
        }
        if (newIndex != -1 && newIndex != highlightedIndex) {
            scrollState.animateScrollToItem(newIndex)
            highlightedIndex = newIndex
        }
    }


    // Hide system UI when in landscape
    LaunchedEffect(isLandscape) {
        systemUiController.isSystemBarsVisible = !isLandscape
    }

    var selectedVideoId by rememberSaveable { mutableStateOf(videoId) }

    // Cập nhật viewModel với video đang chọn
    viewModel.currentVideoId = selectedVideoId

    Column(modifier = Modifier.fillMaxSize()) {
        VideoPlayer(
            videoId = selectedVideoId,
            viewModel = viewModel,
            onPlayerReady = { player -> youtubePlayer.value = player },
            onTimeUpdate = { time ->
                currentTime = time // Cập nhật currentTime khi video phát
            })

        if (!isLandscape) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    Spacer(modifier = Modifier.height(15.dp))

                    Text(
                        text = video.title,
                        fontWeight = FontWeight.Bold,
                        fontFamily = NotoSansJP,
                        fontSize = 20.sp,
                        lineHeight = 30.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = video.datePosted,
                        fontSize = 16.sp,
                        modifier = Modifier
                            .padding(horizontal = 16.dp),
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )

                    Spacer(modifier = Modifier.height(15.dp))

                }

                items (video.subtitles) { subtitle ->
                    // Sửa lại cách kiểm tra highlight
                    val isHighlighted = remember(currentTime) {
                        currentTime in subtitle.timeStart..subtitle.timeEnd
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (isHighlighted) Color.Yellow.copy(alpha = 0.3f)
                                else Color.Transparent
                            )
                            .padding(16.dp)
                    ) {
                        Row (
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp) // Kích thước vòng tròn
                                    .background(
                                        color = Color.Black, // Màu vòng tròn
                                        shape = CircleShape
                                    )
                                    .clickable {
                                        youtubePlayer.value?.seekTo(subtitle.timeStart)
                                    },
                                contentAlignment = Alignment.Center // Căn giữa icon
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play from here",
                                    tint = Color.White, // Màu icon
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = "${subtitle.timeStart.toInt()}s - ${subtitle.timeEnd.toInt()}s",
                                color = Color.Gray,
                                fontSize = 15.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = subtitle.jpText,
                            color = if (isHighlighted) Color.Black else Color.DarkGray,
                            fontWeight = if (isHighlighted) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 16.sp,
                            lineHeight = 24.sp,
                            textAlign = TextAlign.Justify
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = subtitle.enText,
                            color = if (isHighlighted) Color.DarkGray else Color.Gray,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Justify
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun VideoPlayer(
    videoId: String,
    viewModel: VideoViewModel,
    onTimeUpdate: (Float) -> Unit,
    onPlayerReady: (YouTubePlayer) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    AndroidView(
        modifier = modifier.then(
            if (isLandscape) {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            }
        ),
        factory = { ctx ->
            YouTubePlayerView(ctx).apply {
                enableAutomaticInitialization = false
                initialize(object : AbstractYouTubePlayerListener() {
                    override fun onReady(youTubePlayer: YouTubePlayer) {
                        onPlayerReady(youTubePlayer) // Gọi callback khi player ready
                        youTubePlayer.loadVideo(videoId, viewModel.currentPosition)
                        youTubePlayer.addListener(object : AbstractYouTubePlayerListener() {
                            override fun onCurrentSecond(youTubePlayer: YouTubePlayer, second: Float) {
                                viewModel.currentPosition = second
                                onTimeUpdate(second)
                            }
                        })
                    }
                }, true)
            }
        }
    )

    DisposableEffect(lifecycleOwner) {
        onDispose {
            // Cleanup nếu cần
        }
    }
}



