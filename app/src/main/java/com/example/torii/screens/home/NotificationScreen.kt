package com.example.torii.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.torii.model.NotificationItem
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(navController : NavController) {
    val notifications = listOf(
        NotificationItem(
            id = 1,
            userImageUrl = "https://lh3.googleusercontent.com/a/ACg8ocLsp8bWf0fOihwOwwgUNSqZ-vKKXkqDFaHUWxe5IUamFkmh1w=s96-c",
            content = "Ngân Phan đã thêm một bình luận bài viết của bạn.",
            time = "2 phút trước"
        ),
        NotificationItem(
            id = 2,
            userImageUrl = "https://lh3.googleusercontent.com/a/ACg8ocL7wKMDC_gFN8V2H7gKDxBmIfGfs7D8pFYZC1fNZsxUsddnG08=s96-c",
            content = "Lê Hữu Anh Tú đã bấm thích bài viết của bạn.",
            time = "1 giờ trước"
        ),
        NotificationItem(
            id = 3,
            userImageUrl = "https://lh3.googleusercontent.com/a/ACg8ocL7wKMDC_gFN8V2H7gKDxBmIfGfs7D8pFYZC1fNZsxUsddnG08=s96-c",
            content = "Lê Hữu Anh Tú đã bấm thích bài viết của bạn.",
            time = "Hôm qua"
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications", fontWeight = FontWeight.Bold, fontFamily = Feather) },
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
                .padding(padding)
                .fillMaxSize(),
            contentPadding = PaddingValues(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(notifications) { notification ->
                NotificationItemView(notification)
            }
        }
    }
}

@Composable
fun NotificationItemView(notification: NotificationItem) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        tonalElevation = 2.dp,
        shadowElevation = 2.dp,
        color = Color.White
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row {
                AsyncImage(
                    model = notification.userImageUrl,
                    contentDescription = "User Image",
                    modifier = Modifier
                        .size(45.dp)
                        .clip(CircleShape)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = notification.content,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = BeVietnamPro
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = notification.time,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontFamily = BeVietnamPro
                    )
                }
            }
        }
    }
}