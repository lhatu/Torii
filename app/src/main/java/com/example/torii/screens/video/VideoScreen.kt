package com.example.torii.screens.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.VideoViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoScreen(navController: NavController, viewModel: VideoViewModel = viewModel()) {

    val videoList = viewModel.videoList.value

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Videos", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 10.dp)
        ) {
            items(videoList) { video ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp) // Đồng nhất kích thước
                        .padding(8.dp)
                        .clickable {
                            viewModel.currentVideoId = video.videoId
                            viewModel.currentVideo = video
                            navController.navigate("video/${video.videoId}")
                        },
                    shape = RoundedCornerShape(10.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                ) {
                    Column {
                        AsyncImage(
                            model = video.thumbnailUrl,
                            contentDescription = video.title,
                            modifier = Modifier
                                .height(180.dp) // Định kích thước ảnh
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = video.title,
                                style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = video.duration,
                                    style = TextStyle(fontSize = 15.sp, color = Color.Gray, fontFamily = Nunito)
                                )
                                Text(
                                    text = video.datePosted,
                                    style = TextStyle(fontSize = 15.sp, color = Color.Gray, fontFamily = Nunito)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

