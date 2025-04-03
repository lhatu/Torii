package com.example.torii.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.torii.model.NewWord
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.viewmodel.TextToSpeechViewModel

@Composable
fun NewWordCard(word: NewWord) {

    val context = LocalContext.current

    // Lấy view model TextToSpeech
    val textToSpeechViewModel: TextToSpeechViewModel = viewModel()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Màu nền nhạt
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Hình ảnh, Chủ đề, Từ vựng và IPA nằm cùng một row
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Hình ảnh minh họa
                AsyncImage(
                    model = word.imageUrl,
                    contentDescription = "Image for ${word.word}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(15.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Nội dung chính (Chủ đề, Từ vựng, IPA)
                Column(modifier = Modifier.weight(1f)) {
                    // Chủ đề (Category)
                    Box(
                        modifier = Modifier
                            .background(Color(0xFFFFE0E0), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = word.category,
                            style = TextStyle(fontSize = 12.sp, color = Color.DarkGray, fontWeight = FontWeight.Medium, fontFamily = BeVietnamPro)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Từ vựng và Phiên âm
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.word,
                            style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro),
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "/${word.ipa}/",
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray),
                        )
                    }
                }

                // Nút phát âm với nền hình tròn
                Box(
                    modifier = Modifier
                        .size(40.dp)  // Kích thước của nút
                        .clip(CircleShape)  // Hình dạng tròn
                        .background(Color(0xFFD7E3FC))  // Màu nền (màu xám nhạt)
                        .clickable(onClick = {
                            textToSpeechViewModel.speak(word.word)
                        })  // Xử lý sự kiện click
                        .padding(8.dp)  // Khoảng cách giữa icon và viền
                ) {
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Phát âm",
                        tint = Color.Black,  // Màu của icon
                        modifier = Modifier.fillMaxSize()  // Đảm bảo icon nằm vừa trong vòng tròn
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            // Nghĩa của từ và Ví dụ sử dụng nằm trong một row khác
            Column {
                // Nghĩa của từ
                Text(
                    text = word.meaning,
                    style = TextStyle(fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Medium, fontFamily = BeVietnamPro),
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Ví dụ sử dụng
                Text(
                    text = word.example,
                    style = TextStyle(fontSize = 15.sp, fontStyle = FontStyle.Italic, color = Color.Gray, fontFamily = BeVietnamPro),
                )
            }
        }
    }
}