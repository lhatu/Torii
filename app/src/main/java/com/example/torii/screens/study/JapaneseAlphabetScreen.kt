package com.example.torii.screens.study

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.torii.screens.home.video.VideoPlayer
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.VideoViewModel
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

@Composable
fun JapaneseAlphabetScreen(navController: NavHostController, selectTab: String,
    viewModel: VideoViewModel = viewModel(),
) {
    // State để lưu trữ YouTube player và thời gian hiện tại
    val youtubePlayer = remember { mutableStateOf<YouTubePlayer?>(null) }
    var currentTime by remember { mutableStateOf(0f) }

    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Phần Video Player
        VideoPlayer(
            videoId = if (selectTab == "hiragana") "6p9Il_j0zjc" else "s6DKRgtVLGA",
            viewModel = viewModel,
            onPlayerReady = { player -> youtubePlayer.value = player },
            onTimeUpdate = { time ->
                currentTime = time // Cập nhật currentTime khi video phát
            }
        )

        Spacer(modifier = Modifier.height(15.dp))

        Text(
            text = if (selectTab == "hiragana") "Japanese Alphabet - Hiragana" else "Japanese Alphabet - Katakana",
            fontWeight = FontWeight.Bold,
            fontFamily = NotoSansJP,
            fontSize = 20.sp,
            lineHeight = 30.sp,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "May 20, 2025",
            fontSize = 16.sp,
            modifier = Modifier
                .padding(horizontal = 16.dp),
            color = Color.Gray,
            textAlign = TextAlign.End
        )

        Spacer(modifier = Modifier.height(15.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectTab == "hiragana") {
                items(hiraganaList.size) { index ->
                    HiraganaCard(
                        hiragana = hiraganaList[index].hiragana,
                        romaji = hiraganaList[index].romaji,
                        timestamp = hiraganaList[index].timestamp,
                        onClick = {
                            youtubePlayer.value?.seekTo(hiraganaList[index].timestamp)
                        }
                    )
                }
            } else {
                items(katakanaList.size) { index ->
                    KatakanaCard(
                        katakana = katakanaList[index].katakana,
                        romaji = katakanaList[index].romaji,
                        timestamp = katakanaList[index].timestamp,
                        onClick = {
                            youtubePlayer.value?.seekTo(katakanaList[index].timestamp)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun HiraganaCard(hiragana: String, romaji: String, timestamp: Float, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = hiragana,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = romaji,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
            )
        }
    }
}

@Composable
fun KatakanaCard(katakana: String, romaji: String, timestamp: Float, onClick: () -> Unit = {}) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = katakana,
                fontSize = 25.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 6.dp)
            )
            Text(
                text = romaji,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Gray,
            )
        }
    }
}

private val katakanaList = listOf(
    Katakana("ア", "a", 122f),
    Katakana("イ", "i", 138f),
    Katakana("ウ", "u", 152f),
    Katakana("エ", "e", 184f),
    Katakana("オ", "o", 199f),
    Katakana("カ", "ka", 35f),
    Katakana("キ", "ki", 40f),
    Katakana("ク", "ku", 45f),
    Katakana("ケ", "ke", 50f),
    Katakana("コ", "ko", 55f),
    Katakana("サ", "sa", 60f),
    Katakana("シ", "shi", 65f),
    Katakana("ス", "su", 70f),
    Katakana("セ", "se", 75f),
    Katakana("ソ", "so", 80f),
    Katakana("タ", "ta", 85f),
    Katakana("チ", "chi", 90f),
    Katakana("ツ", "tsu", 95f),
    Katakana("テ", "te", 100f),
    Katakana("ト", "to", 105f),
    Katakana("ナ", "na", 110f),
    Katakana("ニ", "ni", 115f),
    Katakana("ヌ", "nu", 120f),
    Katakana("ネ", "ne", 125f),
    Katakana("ノ", "no", 130f),
    Katakana("ハ", "ha", 135f),
    Katakana("ヒ", "hi", 140f),
    Katakana("フ", "fu", 145f),
    Katakana("ヘ", "he", 150f),
    Katakana("ホ", "ho", 155f),
    Katakana("マ", "ma", 160f),
    Katakana("ミ", "mi", 165f),
    Katakana("ム", "mu", 170f),
    Katakana("メ", "me", 175f),
    Katakana("モ", "mo", 180f),
    Katakana("ヤ", "ya", 185f),
    Katakana("", "", 185f),
    Katakana("ユ", "yu", 190f),
    Katakana("", "", 185f),
    Katakana("ヨ", "yo", 195f),
    Katakana("ラ", "ra", 200f),
    Katakana("リ", "ri", 205f),
    Katakana("ル", "ru", 210f),
    Katakana("レ", "re", 215f),
    Katakana("ロ", "ro", 220f),
    Katakana("ワ", "wa", 225f),
    Katakana("", "", 185f),
    Katakana("", "", 185f),
    Katakana("", "", 185f),
    Katakana("ヲ", "wo", 230f),
    Katakana("ン", "n", 235f)
)

private fun formatTimestamp(seconds: Float): String {
    val minutes = (seconds / 60).toInt()
    val remainingSeconds = (seconds % 60).toInt()
    return String.format("%02d:%02d", minutes, remainingSeconds)
}

private val hiraganaList = listOf(
    Hiragana("あ", "a", 169f),
    Hiragana("い", "i", 187f),
    Hiragana("う", "u", 210f),
    Hiragana("え", "e", 227f),
    Hiragana("お", "o", 241f),
    Hiragana("か", "ka", 25f),
    Hiragana("き", "ki", 30f),
    Hiragana("く", "ku", 35f),
    Hiragana("け", "ke", 40f),
    Hiragana("こ", "ko", 45f),
    Hiragana("さ", "sa", 50f),
    Hiragana("し", "shi", 55f),
    Hiragana("す", "su", 60f),
    Hiragana("せ", "se", 65f),
    Hiragana("そ", "so", 70f),
    Hiragana("た", "ta", 75f),
    Hiragana("ち", "chi", 80f),
    Hiragana("つ", "tsu", 85f),
    Hiragana("て", "te", 90f),
    Hiragana("と", "to", 95f),
    Hiragana("な", "na", 100f),
    Hiragana("に", "ni", 105f),
    Hiragana("ぬ", "nu", 110f),
    Hiragana("ね", "ne", 115f),
    Hiragana("の", "no", 120f),
    Hiragana("は", "ha", 125f),
    Hiragana("ひ", "hi", 130f),
    Hiragana("ふ", "fu", 135f),
    Hiragana("へ", "he", 140f),
    Hiragana("ほ", "ho", 145f),
    Hiragana("ま", "ma", 150f),
    Hiragana("み", "mi", 155f),
    Hiragana("む", "mu", 160f),
    Hiragana("め", "me", 165f),
    Hiragana("も", "mo", 170f),
    Hiragana("や", "ya", 175f),
    Hiragana("", "", 175f),
    Hiragana("ゆ", "yu", 180f),
    Hiragana("", "", 175f),
    Hiragana("よ", "yo", 185f),
    Hiragana("ら", "ra", 190f),
    Hiragana("り", "ri", 195f),
    Hiragana("る", "ru", 200f),
    Hiragana("れ", "re", 205f),
    Hiragana("ろ", "ro", 210f),
    Hiragana("わ", "wa", 215f),
    Hiragana("", "", 175f),
    Hiragana("", "", 175f),
    Hiragana("", "", 175f),
    Hiragana("を", "wo", 220f),
    Hiragana("ん", "n", 225f)
)

data class Hiragana(val hiragana: String, val romaji: String, val timestamp: Float)

data class Katakana(val katakana: String, val romaji: String, val timestamp: Float)

