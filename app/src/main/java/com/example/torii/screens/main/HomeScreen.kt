package com.example.torii.screens.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.School
import androidx.compose.material.icons.outlined.Translate
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.torii.card.CategoryCard
import com.example.torii.card.NewWordCard
import com.example.torii.card.ReadingCard
import com.example.torii.card.VideoCard
import com.example.torii.card.WelcomeCard
import com.example.torii.model.BottomNavItem
import com.example.torii.model.Category
import com.example.torii.ui.theme.Feather
import com.example.torii.viewModel.ArticleViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.livedata.observeAsState
import com.example.torii.viewModel.VideoViewModel
import com.example.torii.viewModel.VocabularyViewModel
import androidx.compose.material3.NavigationBarItemDefaults
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController, authRepo: AuthRepository,
               viewModel: ArticleViewModel = viewModel(), viewModel1: VocabularyViewModel = viewModel(), videoViewModel: VideoViewModel = viewModel()) {

    val user = authRepo.getCurrentUser()
    val articles = viewModel.articles.collectAsState()
    val vocabList = viewModel1.vocabList.observeAsState(emptyList())
    val videos = videoViewModel.videoList

    LaunchedEffect(Unit) {
        if (vocabList.value.isEmpty()) {
            viewModel1.loadAllVocabulary {
                viewModel1.loadRandomFive()
            }
        } else {
            viewModel1.loadRandomFive()
        }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Màu nền áp dụng cho cả padding
            ) {
                TopAppBar(
                    title = { Text("Torii", fontWeight = FontWeight.Bold, fontFamily = Feather) },
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
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Spacer(modifier = Modifier.height(10.dp))

                    val userName = user.displayName
                    val streak = remember { (1..50).random() }         // Ngẫu nhiên từ 1 đến 50 ngày
                    val xp = remember { (100..1000).random() }         // Ngẫu nhiên từ 100 đến 1000 XP
                    val rank = "Silver"

                    WelcomeCard(userName = userName.toString(), streakDays = streak, xpPoints = xp, rank = rank)

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
                            style = TextStyle(fontFamily = Feather, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { navController.navigate("category") }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                item {
                    val categories = listOf(
                        Category("Animal", 120, "\uD83D\uDC18"), // 🐘
                        Category("Food", 95, "\uD83C\uDF55"), // 🍕
                        Category("Technology", 80, "\uD83D\uDDA5\uFE0F"), // 🖥️
                        Category("Travel", 60, "✈\uFE0F"), // ✈️
                        Category("Nature", 45, "\uD83C\uDF3F"), // 🌿
                        Category("Space", 30, "\uD83C\uDF0C"), // 🌌
                        Category("Geography", 50, "\uD83C\uDF0D"), // 🌍
                        Category("Jobs", 70, "\uD83D\uDCBC"), // 💼
                        Category("Sports", 65, "\uD83C\uDFC0"), // 🏀
                    )
                    LazyRow (
                        Modifier.height(150.dp)
                    ) {
                        items(categories) { category ->
                            CategoryCard(navController, category)
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))
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
                            text = "Vocabulary",
                            style = TextStyle(fontFamily = Feather, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { navController.navigate("vocabulary/${"All"}") }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                    Spacer(modifier = Modifier.height(5.dp))
                }

                // Danh sách từ vựng
                items(vocabList.value) { vocab ->
                    NewWordCard(vocab)
                }

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
                            style = TextStyle(fontFamily = Feather, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { navController.navigate("articles") }
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
                        items(articles.value) { article ->
                            ReadingCard(navController, article)
                        }
                    }
                }

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
                            text = "Watch video",
                            style = TextStyle(fontFamily = Feather, fontSize = 25.sp, fontWeight = FontWeight.Bold)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    navController.navigate("videos")
                                }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(videos.value) { video ->
                            VideoCard(navController, video)
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
        BottomNavItem("home", "Home", Icons.Default.Home, Icons.Outlined.Home),
        BottomNavItem("search", "Search", Icons.Default.Translate, Icons.Outlined.Translate),
        BottomNavItem("learning", "Study", Icons.Default.School, Icons.Outlined.School),
        BottomNavItem("community", "Community", Icons.Default.People, Icons.Outlined.People)
    )

    NavigationBar(
        containerColor = Color(0x8BF5F5F5),
    ) {
        val currentRoute = navController.currentDestination?.route
        items.forEach { item ->
            val selected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = if (selected) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, fontFamily = Nunito) },
                selected = selected,
                onClick = {
                    if (!selected) {
                        navController.navigate(item.route) {
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xB4B3E5FC),
                ),
            )
        }
    }
}


