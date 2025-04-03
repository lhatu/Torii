package com.example.torii.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.torii.repository.AuthRepository
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.card.NewWordCard
import com.example.torii.card.ReadingCard
import com.example.torii.card.VideoCard
import com.example.torii.model.BottomNavItem
import com.example.torii.model.NewWord
import com.example.torii.model.ReadingArticle
import com.example.torii.model.VideoLesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, authRepo: AuthRepository) {

    val user = authRepo.getCurrentUser()

    var search by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFE3F2FD)) // Màu nền áp dụng cho cả padding
            ) {
                TopAppBar(
                    title = { Text("Torii", fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro) },
                    modifier = Modifier.padding(end = 12.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Để màu nền của Box hiển thị
                        titleContentColor = Color.Black // Màu chữ tiêu đề
                    ),
                    actions = {
                        BadgedBox(
                            badge = {
                                val notificationCount = 3
                                if (notificationCount > 0) { // Chỉ hiển thị khi có thông báo
                                    Badge(
                                        modifier = Modifier.offset(x = (-16).dp, y = 7.dp),
                                    ) { Text(notificationCount.toString()) }
                                }
                            }
                        ) {
                            IconButton(onClick = { /* Xử lý mở thông báo */ }) {
                                Icon(Icons.Default.Notifications, contentDescription = "Thông báo")
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp)) // Tạo khoảng cách giữa các icon

                        IconButton(onClick = { /* Xử lý cài đặt */ }) {
                            Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
                        }

                        Spacer(modifier = Modifier.width(15.dp)) // Tạo khoảng cách trước avatar

                        user?.photoUrl?.let { avatarUrl ->
                            AsyncImage(
                                model = avatarUrl,
                                contentDescription = "Avatar",
                                modifier = Modifier
                                    .size(35.dp)
                                    .clip(CircleShape)
                            )
                        }
                    }
                )

            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        content = { paddingValues ->
            LazyColumn( // Đổi từ Column sang LazyColumn để toàn màn hình có thể cuộn
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF2F6FF))
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    // Thanh tìm kiếm
                    OutlinedTextField(
                        value = search,
                        onValueChange = { search = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        placeholder = { Text("Search", fontFamily = BeVietnamPro, fontSize = 18.sp) },
                        textStyle = TextStyle(fontSize = 18.sp, fontFamily = BeVietnamPro),
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
                        shape = RoundedCornerShape(30.dp),
                    )

                    Spacer(modifier = Modifier.height(20.dp))
                }

                item {
                    // Category - Từ vựng theo chủ đề
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Category",
                            style = TextStyle(fontFamily = BeVietnamPro, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = BeVietnamPro,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { navController.navigate("category") }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                }

                item {
                    val pastelColors = listOf(
                        Color(0xFFFFE5E5), // Hồng nhạt
                        Color(0xFFB3E5FC), // Xanh dương nhạt
                        Color(0xFFFFF9C4), // Vàng kem
                        Color(0xFFBBDEFB), // Xanh trời nhẹ
                        Color(0xFFE1BEE7), // Tím nhạt
                        Color(0xFFC8E6C9), // Xanh lá pastel
                        Color(0xFFFFCCBC), // Cam nhẹ
                    )
                    LazyRow {
                        itemsIndexed(listOf("\uD83D\uDC18 Animal", "\uD83C\uDF55 Food", "\uD83D\uDDA5\uFE0F Technology", "✈\uFE0F Travel")) { index, category ->
                            Card(
                                modifier = Modifier
                                    .padding(8.dp, 0.dp)
                                    .height(47.dp)
                                    .clickable { /* Xử lý click */ },
                                shape = RoundedCornerShape(30.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = pastelColors[index % pastelColors.size]),
                            ) {
                                Text(
                                    text = category,
                                    modifier = Modifier.padding(16.dp, 12.dp),
                                    style = TextStyle(fontFamily = BeVietnamPro, fontSize = 18.sp),
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(25.dp))
                }

                item {
                    // New Words - Từ vựng mới
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New words",
                            style = TextStyle(fontFamily = BeVietnamPro, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = BeVietnamPro,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { navController.navigate("vocabulary") }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

                val words = listOf(
                    NewWord("Elephant", "Animal", "ˈɛlɪfənt", "a very large grey mammal", "The elephant is the largest land animal.", "https://cdn.mos.cms.futurecdn.net/TVR7E3Kuzg2iRhKkjZPeWk-1200-80.jpg"),
                    NewWord("Pizza", "Food", "ˈpiːtsə", "a large circle of flat bread baked with cheese", "I love eating pizza on weekends.", "https://dictionary.cambridge.org/images/thumb/pizza_noun_001_12184.jpg?version=6.0.48"),
                    NewWord("Smartphone", "Technology", "ˈsmɑːrt.foʊn", "a mobile phone that can be used", "He just bought a new smartphone.", "https://dictionary.cambridge.org/images/thumb/smartp_noun_002_34391.jpg?version=6.0.48")
                )

                // Danh sách từ vựng
                items(words) { word ->
                    NewWordCard(word)
                }

                val articles = listOf(
                    ReadingArticle("The Secret of Elephants", "https://cdn.mos.cms.futurecdn.net/TVR7E3Kuzg2iRhKkjZPeWk-1200-80.jpg", "March 25, 2025"),
                    ReadingArticle("The History of Pizza", "https://example.com/pizza.jpg", "April 1, 2025"),
                    ReadingArticle("Smartphones and Society", "https://example.com/phone.jpg", "March 20, 2025")
                )

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    // Tiêu đề bài đọc
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reading articles",
                            style = TextStyle(fontFamily = BeVietnamPro, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = BeVietnamPro,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { /* Xử lý sự kiện nhấn See All */ }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Danh sách bài đọc
                    LazyRow (
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(articles) { article ->
                            ReadingCard(article)
                        }
                    }
                }

                val videoLessons = listOf(
                    VideoLesson("Learn English Basics", "10:30", "March 25, 2025", "https://img.youtube.com/vi/dQw4w9WgXcQ/0.jpg"),
                    VideoLesson("Common English Phrases", "15:45", "March 25, 2025", "https://img.youtube.com/vi/3JZ_D3ELwOQ/0.jpg"),
                    VideoLesson("English Listening Practice", "8:20", "March 25, 2025", "https://img.youtube.com/vi/2Vv-BfVoq4g/0.jpg")
                )
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                    // Tiêu đề bài đọc
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Watch Video Lessons",
                            style = TextStyle(fontFamily = BeVietnamPro, fontSize = 23.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = BeVietnamPro,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { /* Xử lý sự kiện nhấn See All */ }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(videoLessons) { video ->
                            VideoCard(video)
                        }
                    }
                }
            }
        }

    )
}

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        BottomNavItem("home", "Trang chủ", Icons.Default.Home),
        BottomNavItem("translate", "Dịch", Icons.Default.Translate),
        BottomNavItem("learning", "Học tập", Icons.Default.School),
        BottomNavItem("community", "Cộng đồng", Icons.Default.People)
    )

    NavigationBar {
        val currentRoute = navController.currentDestination?.route
        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = { navController.navigate(item.route) }
            )
        }
    }
}


