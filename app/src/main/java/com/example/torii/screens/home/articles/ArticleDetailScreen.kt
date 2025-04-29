package com.example.torii.screens.home.articles

import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.torii.model.Article
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.NotoSansJP
import kotlinx.coroutines.delay
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.foundation.text.ClickableText
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Shadow
import android.media.audiofx.LoudnessEnhancer
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.derivedStateOf
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torii.card.NewWordCard
import com.example.torii.viewModel.VocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleDetailScreen(title: String, publishDate: String, content: String, imageUrl: String, audioUrl: String, navController: NavController
    , viewModel: VocabularyViewModel = viewModel()) {

    val article = Article(title, publishDate, content, imageUrl, audioUrl)

    var clickedWord by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadAllVocabulary()
    }

    Scaffold (
        topBar = {
            TopAppBar(
                title = { Text("", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = article.title,
                fontWeight = FontWeight.Bold,
                fontFamily = NotoSansJP,
                fontSize = 24.sp,
                lineHeight = 35.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = article.publishDate,
                color = Color.Gray,
                fontSize = 16.sp,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )

            Spacer(modifier = Modifier.height(16.dp))

            AsyncImage(
                model = article.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            AudioPlayer(audioUrl = article.audioUrl.toString())

            Spacer(modifier = Modifier.height(16.dp))


            Column {
                ArticleContent(article.content ?: "") { word ->
                    clickedWord = word
                }

                clickedWord?.let { word ->
                    WordDialog(
                        word = word,
                        onDismiss = { clickedWord = null },
                        viewModel = viewModel
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AudioPlayer(audioUrl: String) {
    val context = LocalContext.current
    val mediaPlayer = remember { MediaPlayer() }
    var isPrepared by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableFloatStateOf(0f) }
    var duration by remember { mutableFloatStateOf(0f) }
    val loudnessEnhancer = remember { LoudnessEnhancer(mediaPlayer.audioSessionId) }

    // Prepare MediaPlayer once
    LaunchedEffect(audioUrl) {
        try {
            mediaPlayer.setDataSource(audioUrl)
            mediaPlayer.prepareAsync()
            mediaPlayer.setOnPreparedListener {
                isPrepared = true
                duration = it.duration.toFloat()
            }
            mediaPlayer.setOnCompletionListener {
                isPlaying = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // Tăng âm lượng video
    LaunchedEffect(isPrepared) {
        if (isPrepared) {
            loudnessEnhancer.setTargetGain(800) // Đơn vị là millibels (mB). 1000 = +10dB, 1500 = +15dB
            loudnessEnhancer.enabled = true
        }
    }

    // Update seek position periodically
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition.toFloat()
            delay(500)
        }
    }

    if (isPrepared) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Black)
                .padding(end = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // Nút Play/Pause
            IconButton(onClick = {
                if (isPlaying) {
                    mediaPlayer.pause()
                } else {
                    mediaPlayer.start()
                }
                isPlaying = !isPlaying
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White
                )
            }

            // Thời gian hiện tại
            Text(
                text = formatTime(currentPosition.toLong()),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(end = 8.dp)
            )

            // Slider chiếm phần còn lại
            Slider(
                value = currentPosition,
                onValueChange = {
                    currentPosition = it
                    mediaPlayer.seekTo(it.toInt())
                },
                valueRange = 0f..duration,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 4.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Color.White,
                    inactiveTrackColor = Color.Gray
                )
            )

            // Thời gian tổng
            Text(
                text = formatTime(duration.toLong()),
                fontSize = 12.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

    } else {
        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
    }

    // Release when done
    DisposableEffect(Unit) {
        onDispose {
            if (mediaPlayer.isPlaying) mediaPlayer.stop()
            mediaPlayer.release()
            loudnessEnhancer.release()
        }
    }
}

// Utility to format milliseconds
fun formatTime(milliseconds: Long): String {
    val minutes = (milliseconds / 1000) / 60
    val seconds = (milliseconds / 1000) % 60
    return "%d:%02d".format(minutes, seconds)
}

@Composable
fun ArticleContent(content: String, onWordClick: (String) -> Unit) {
    val katakanaKanjiRegex = remember { Regex("[\\p{Script=Katakana}\\p{Script=Han}ー]+") }
    val matches = remember(content) { katakanaKanjiRegex.findAll(content).toList() }

    val annotatedString = remember(content) {
        buildAnnotatedString {
            var lastIndex = 0
            for (match in matches) {
                // Append content before the match
                append(content.substring(lastIndex, match.range.first))

                val word = match.value
                // Add clickable annotation for the word
                pushStringAnnotation(tag = "WORD", annotation = word)
                withStyle(
                    style = SpanStyle(
                        textDecoration = TextDecoration.Underline,
                        shadow = Shadow(
                            color = Color.LightGray, // Màu "giả lập" cho gạch chân
                            offset = Offset(0f, 0f),
                            blurRadius = 0f
                        ),
                    )
                ) {
                    append(word)
                }
                pop()
                lastIndex = match.range.last + 1
            }
            // Append any remaining content
            if (lastIndex < content.length) {
                append(content.substring(lastIndex))
            }
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations("WORD", offset, offset)
                .firstOrNull()?.let { annotation ->
                    onWordClick(annotation.item)
                }
        },
        modifier = Modifier.fillMaxWidth(),
        style = TextStyle(
            fontSize = 20.sp,
            color = Color.Black,  // Màu chữ bình thường
            lineHeight = 36.sp,
            textAlign = TextAlign.Justify,
        )
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WordDialog(
    word: String,
    onDismiss: () -> Unit,
    viewModel: VocabularyViewModel
) {
    val vocabulary by remember(word) {
        derivedStateOf { viewModel.searchVocabulary(word) }
    }

    if (vocabulary != null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            content = { NewWordCard(word = vocabulary!!) },
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true
            )
        )
    } else {
        Toast.makeText(
            LocalContext.current,
            "Không tìm thấy từ: $word",
            Toast.LENGTH_SHORT
        ).show()
        onDismiss()
    }
}



