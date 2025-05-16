package com.example.torii

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.torii.auth.GoogleAuthRepository
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.TextToSpeechViewModel
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class MainActivity() : ComponentActivity() {

    private lateinit var googleAuthRepo: GoogleAuthRepository
    private lateinit var navController: NavHostController
    private lateinit var textToSpeechViewModel: TextToSpeechViewModel

    @RequiresApi(Build.VERSION_CODES.O)
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MyApp(navController: NavHostController) {
    AppNavGraph(navController)
}

@Composable
fun SplashScreen(navController: NavHostController) {
    var scale by remember { mutableStateOf(1f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(true) {
        scale = 1.2f
        delay(2000) // Hiển thị trong 2 giây
        coroutineScope.launch {
            navController.navigate("greeting") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        // Ảnh nền
        Image(
            painter = painterResource(id = R.drawable.logo), // Thay bằng ảnh của bạn
            contentDescription = "App Logo",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(250.dp)
        )
    }
}

@Composable
fun GreetingScreen(navController: NavHostController) {

    Column(
        modifier = Modifier.fillMaxSize().padding(12.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(40.dp))

        Image(
            painter = painterResource(id = R.drawable.greeting),
            contentDescription = "Ảnh chào mừng",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(350.dp)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Learn Japanese easily and effectively with Torii!",
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Nunito,
            textAlign = TextAlign.Center,
            color = Color.DarkGray,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(60.dp))

        // Nút "Tiếp theo" hoặc "Vào ứng dụng"
        val coroutineScope = rememberCoroutineScope()

        Button(
            onClick = {
                coroutineScope.launch {
                    navController.navigate("register")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF58CC02),
            ),
        ) {
            Text(
                text = "GET STARTED",
                fontSize = 16.sp,
                fontFamily = Feather,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                coroutineScope.launch {
                    navController.navigate("login")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.White, // Nền trắng
                contentColor = Color(0xFF58CC02)
            ),
            border = BorderStroke(2.dp, Color.LightGray),
        ) {
            Text(
                text = "I ALREADY HAVE AN ACCOUNT",
                fontSize = 16.sp,
                fontFamily = Feather,
            )
        }
    }
}