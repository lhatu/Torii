package com.example.torii.screens.study

import LessonViewModel
import android.content.res.Configuration
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.torii.repository.AuthRepository
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.torii.model.LessonGrammar
import com.example.torii.model.LessonVocabulary
import com.example.torii.screens.home.video.VideoPlayer
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.VideoViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonDetailScreen(
    lessonId: String,
    viewModel: LessonViewModel = viewModel()
) {
    val lesson = viewModel.getLessonById(lessonId)
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("Vocabulary", "Grammar")

    val videoViewModel: VideoViewModel = viewModel()
    var currentTime by remember { mutableStateOf(0f) }
    val youtubePlayer = remember { mutableStateOf<YouTubePlayer?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        // Video Player
        lesson?.videoUrl?.let { url ->
            VideoPlayer(
                videoId = lesson.videoUrl,
                viewModel = videoViewModel,
                onPlayerReady = { player -> youtubePlayer.value = player },
                onTimeUpdate = { time ->
                    currentTime = time // Cập nhật currentTime khi video phát
                }
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = lesson?.title ?: "",
            fontWeight = FontWeight.Bold,
            fontFamily = NotoSansJP,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Date posted
        Text(
            text = lesson?.datePost ?: "",
            fontSize = 16.sp,
            modifier = Modifier.padding(horizontal = 16.dp),
            color = Color.Gray,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Secondary Tabs
        TabRow(
            selectedTabIndex = selectedTab,
            modifier = Modifier.fillMaxWidth(),
            containerColor = Color(0x54F5F5F5),
            contentColor = Color.Black,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = Color.Black
                )
            }
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    text = { Text(title, fontFamily = BeVietnamPro, fontSize = 17.sp) },
                    selected = selectedTab == index,
                    onClick = { selectedTab = index }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f) // hạn chế chiều cao cuộn
        ) {
            when (selectedTab) {
                0 -> VocabularyList(lesson?.listVocab ?: emptyList())
                1 -> GrammarList(lesson?.listGrammar ?: emptyList())
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

@Composable
fun VocabularyList(vocabularies: List<LessonVocabulary>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(vocabularies) { vocab ->
            VocabularyItem(vocab)
            Divider()
        }
    }
}

@Composable
fun VocabularyItem(vocab: LessonVocabulary) {
    Column(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(vocab.word, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansJP)
        Spacer(modifier = Modifier.height(5.dp))
        Text(vocab.reading, fontSize = 14.sp, color = Color.Gray, fontFamily = BeVietnamPro)
        Spacer(modifier = Modifier.height(5.dp))
        Text(vocab.meaning, fontSize = 16.sp, color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = BeVietnamPro)
        Spacer(modifier = Modifier.height(5.dp))
    }
}

@Composable
fun GrammarList(grammars: List<LessonGrammar>) {
    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(grammars) { grammar ->
            GrammarItem(grammar)
            Divider()
        }
    }
}

@Composable
fun GrammarItem(grammar: LessonGrammar) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(grammar.structure, fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansJP)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            grammar.explanation,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium,
            fontFamily = BeVietnamPro,
            textAlign = TextAlign.Justify
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}