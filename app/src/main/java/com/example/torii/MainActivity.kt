package com.example.torii

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.torii.auth.GoogleAuthRepository
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.viewmodel.TextToSpeechViewModel
import kotlinx.coroutines.launch

class MainActivity() : ComponentActivity() {

    private lateinit var googleAuthRepo: GoogleAuthRepository
    private lateinit var navController: NavHostController
    private lateinit var textToSpeechViewModel: TextToSpeechViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        googleAuthRepo = GoogleAuthRepository(this)

        textToSpeechViewModel = ViewModelProvider(this).get(TextToSpeechViewModel::class.java)

        setContent {
            navController = rememberNavController() // ✅ Khởi tạo đúng cách
            MyApp(navController)
        }
    }
}

@Composable
fun MyApp(navController: NavHostController) {
    AppNavGraph(navController)
}

@Composable
fun SplashScreen(navController: NavHostController) {
    var scale by remember { mutableStateOf(1f) }

    LaunchedEffect(true) {
        scale = 1.2f
        delay(2000) // Hiển thị trong 2 giây
        navController.navigate("greeting") {
            popUpTo("splash") { inclusive = true }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Ảnh nền
        Image(
            painter = painterResource(id = R.drawable.splash_bg), // Thay bằng ảnh của bạn
            contentDescription = "Splash Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Logo + Loading Indicator
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_bg), // Logo app nếu có
                contentDescription = "App Logo",
                modifier = Modifier.size(500.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            CircularProgressIndicator(color = Color.Black) // Hiệu ứng xoay chờ
        }
    }
}

@Composable
fun GreetingScreen(navController: NavHostController) {
    val pages = listOf(
        GreetingItem(R.drawable.vocab, "Discover New Vocab",
            "Search for manually store new words you want to learn with Torii, or choose  from our recommended list."),
        GreetingItem(R.drawable.vocab, "Luyện phát âm",
            "Phát âm chuẩn với công nghệ nhận diện giọng nói AI."),
        GreetingItem(R.drawable.vocab, "Kiểm tra trình độ",
            "Làm bài test để đánh giá và theo dõi tiến bộ của bạn.")
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp)
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        // Nút Skip
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                navController.navigate("login")
            }) {
                Text(
                    text = "Skip",
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontFamily = BeVietnamPro,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(10.dp)
                )
            }
        }

//        Spacer(modifier = Modifier.height(10.dp))

        // Hiển thị Card
        HorizontalPager(state = pagerState) { page ->
            GreetingCard(pages[page])
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Pagination Dots
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pages.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == pagerState.currentPage) 10.dp else 6.dp)
                        .clip(CircleShape)
                        .background(if (index == pagerState.currentPage) Color.Black else Color.Gray)
                        .padding(4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Nút "Tiếp theo" hoặc "Vào ứng dụng"
        val coroutineScope = rememberCoroutineScope()
        Button(
            onClick = {
                if (pagerState.currentPage == pages.lastIndex) {
                    navController.navigate("login")
                } else {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(pagerState.currentPage + 1)
                    }
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            Text(
                text = if (pagerState.currentPage == pages.lastIndex) "Bắt đầu ngay" else "Tiếp theo",
                fontSize = 18.sp,
                fontFamily = BeVietnamPro,
            )
        }
    }
}

@Composable
fun GreetingCard(item: GreetingItem) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(0.8f)
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = item.imageRes),
                contentDescription = item.title,
                modifier = Modifier.height(300.dp),
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = item.title,
                fontSize = 24.sp,
                fontFamily = BeVietnamPro,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Left,
                modifier = Modifier.padding(16.dp, 5.dp),

            )
            Text(
                text = item.description,
                fontSize = 16.sp,
                fontFamily = BeVietnamPro,
                textAlign = TextAlign.Justify,
                modifier = Modifier.padding(16.dp, 5.dp)
            )
        }
    }
}

data class GreetingItem(val imageRes: Int, val title: String, val description: String)

@Preview(showBackground = true)
@Composable
fun PreviewSplashScreen() {
    SplashScreen(navController = rememberNavController())
}

@Preview(showBackground = true)
@Composable
fun PreviewGreetingScreen() {
    GreetingScreen(navController = rememberNavController())
}