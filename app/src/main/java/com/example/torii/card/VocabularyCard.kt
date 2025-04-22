package com.example.torii.card

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.outlined.VolumeUp
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
import com.example.torii.model.Vocabulary
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.viewModel.TextToSpeechViewModel

@Composable
fun NewWordCard(word: Vocabulary) {

    val context = LocalContext.current

    // Lấy view model TextToSpeech
    val textToSpeechViewModel: TextToSpeechViewModel = viewModel()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White), // Màu nền nhạt
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Hình ảnh, Chủ đề, Từ vựng và IPA nằm cùng một row
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                // Hình ảnh minh họa
                AsyncImage(
                    model = word.imgUrl,
                    contentDescription = "Image for ${word.expression}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(15.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                // Nội dung chính (Chủ đề, Từ vựng, IPA)
                Column(modifier = Modifier.weight(1f)) {
                    // Chủ đề (Category)
                    Row (
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(getJlptColor(word.tags), shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = word.tags,
                                style = TextStyle(
                                    fontSize = 12.sp,
                                    color = Color.White,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = Feather
                                ),
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF66BB6A), shape = RoundedCornerShape(20.dp))
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = word.category.toString(),
                                style = TextStyle(fontSize = 12.sp, color = Color.White, fontWeight = FontWeight.Medium, fontFamily = Feather),
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(5.dp))

                    // Từ vựng và Phiên âm
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.expression,
                            style = TextStyle(fontSize = 25.sp, fontWeight = FontWeight.Bold, fontFamily = NotoSansJP),
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = word.reading,
                            style = TextStyle(fontSize = 14.sp, color = Color.Gray, fontFamily = BeVietnamPro),
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }

                // Nút phát âm với nền hình tròn
                Box(
                    modifier = Modifier
                        .size(40.dp)  // Kích thước của nút
                        .clip(CircleShape)  // Hình dạng tròn
                        .background(Color(0xFFEFEFEF))  // Màu nền (màu xám nhạt)
                        .clickable(onClick = {
                            textToSpeechViewModel.speak(word.expression)
                        })  // Xử lý sự kiện click
                        .padding(8.dp),  // Khoảng cách giữa icon và viền
                ) {
                    Icon(
                        imageVector = Icons.Outlined.VolumeUp,
                        contentDescription = "Phát âm",
                        tint = Color.Black,  // Màu của icon
                        modifier = Modifier.fillMaxSize(),  // Đảm bảo icon nằm vừa trong vòng tròn
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

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
                    text = word.exSentence.toString(),
                    style = TextStyle(fontSize = 15.sp, fontStyle = FontStyle.Normal, color = Color.Gray, fontFamily = BeVietnamPro),
                )
            }
        }
    }
}