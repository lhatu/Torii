package com.example.torii.card

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.torii.model.Article
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito

@Composable
fun ReadingCard(navController: NavController, article: Article) {
    Card(
        modifier = Modifier
            .width(260.dp)
            .height(300.dp) // Đặt chiều cao cố định cho tất cả Card
            .padding(8.dp)
            .clickable {
                navController.navigate("article_detail/${Uri.encode(article.title)}/${Uri.encode(article.publishDate)}" +
                        "/${Uri.encode(article.content)}/${Uri.encode(article.imageUrl)}/${Uri.encode(article.audioUrl)}")
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
    ) {
        Column {
            AsyncImage(
                model = article.imageUrl,
                contentDescription = article.title,
                modifier = Modifier
                    .height(130.dp) // Đặt chiều cao cố định cho ảnh
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = article.title,
                    style = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Bold),
                    maxLines = 3, // Giới hạn số dòng
                    overflow = TextOverflow.Ellipsis // Thêm dấu "..." nếu quá dài
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = article.content.toString(),
                    style = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium, fontFamily = BeVietnamPro),
                    color = Color.Black,
                    maxLines = 3, // Giới hạn số dòng
                    overflow = TextOverflow.Ellipsis // Thêm dấu "..." nếu quá dài
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = article.publishDate,
                    style = TextStyle(fontSize = 14.sp, color = Color.Gray, fontFamily = Nunito),
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}