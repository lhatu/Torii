package com.example.torii.screens.articles

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.ArticleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticlesScreen(navController: NavController, viewModel: ArticleViewModel = viewModel()) {

    val articles = viewModel.articles.collectAsState()

    val isLoading = viewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Reading Articles", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            if (isLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(horizontal = 10.dp, vertical = 0.dp)
                ) {
                    items(articles.value) { article ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(185.dp) // Đặt chiều cao cố định cho tất cả Card
                                .padding(8.dp)
                                .clickable {
                                    navController.navigate("article_detail/${Uri.encode(article.title)}/${Uri.encode(article.publishDate)}" +
                                            "/${Uri.encode(article.content)}/${Uri.encode(article.imageUrl)}/${Uri.encode(article.audioUrl)}")
                                },
                            shape = RoundedCornerShape(10.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                        ) {
                            Row(modifier = Modifier.padding(12.dp)) {
                                AsyncImage(
                                    model = article.imageUrl,
                                    contentDescription = "Article Image",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp)),
                                    contentScale = ContentScale.Crop
                                )

                                Spacer(modifier = Modifier.width(12.dp))

                                // Nội dung bên phải
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = article.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        maxLines = 2, // Giới hạn số dòng
                                        overflow = TextOverflow.Ellipsis // Thêm dấu "..." nếu quá dài
                                    )

                                    Spacer(modifier = Modifier.height(4.dp))

                                    Text(
                                        text = article.content ?: "",
                                        fontSize = 14.sp,
                                        fontFamily = BeVietnamPro,
                                        maxLines = 3, // Giới hạn số dòng
                                        overflow = TextOverflow.Ellipsis // Thêm dấu "..." nếu quá dài
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))

                                    Text(
                                        text = article.publishDate,
                                        fontSize = 14.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.align(Alignment.End),
                                        fontFamily = Nunito
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
