package com.example.torii.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.torii.model.Kanji
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import coil.compose.AsyncImage
import coil.decode.GifDecoder
import coil.request.ImageRequest
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito

@Composable
fun KanjiCard(kanji: Kanji) {

    var showDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .padding(vertical = 5.dp, horizontal = 2.dp)
            .height(180.dp)
            .clickable { showDialog = true }
            .fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        border = BorderStroke(1.dp, Color.LightGray),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(kanji.character, fontSize = 50.sp, fontWeight = FontWeight.Bold)

                Column(horizontalAlignment = Alignment.End) {
                    Box(
                        modifier = Modifier
                            .background(getJlptColor(kanji.jlptLevel), shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = kanji.jlptLevel,
                            style = TextStyle(
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.Medium,
                                fontFamily = Feather
                            ),
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(kanji.meaning, fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro, fontSize = 18.sp)
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "On: ${kanji.onyomi.joinToString(", ")}",
                fontFamily = BeVietnamPro,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                "Kun: ${kanji.kunyomi.joinToString(", ")}",
                fontFamily = BeVietnamPro,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text("Strokes: ${kanji.strokes}", fontFamily = BeVietnamPro)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Close", fontSize = 16.sp, color = Color.Black)
                }
            },
            containerColor = Color.White,
            title = {
                Text("Kanji: ${kanji.character}", fontWeight = FontWeight.Bold)
            },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    val gifUrl = "https://raw.githubusercontent.com/jcsirot/kanji.gif/refs/heads/master/kanji/gif/150x150/${kanji.character}.gif"

                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(gifUrl)
                            .decoderFactory(GifDecoder.Factory())
                            .build(),
                        contentDescription = "Kanji Stroke Order",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }
            }
        )
    }
}

fun getJlptColor(level: String): Color {
    return when (level) {
        "N1" -> Color(0xFFEC407A) // Đỏ nhạt
        "N2" -> Color(0xFFFF7043) // Vàng
        "N3" -> Color(0xFFFFCA28) // Xanh nhạt
        "N4" -> Color(0xFF66BB6A) // Xanh lá nhạt
        "N5" -> Color(0xFF42A5F5) // Tím nhạt
        else -> Color.LightGray
    }
}
