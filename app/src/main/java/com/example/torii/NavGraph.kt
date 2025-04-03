package com.example.torii

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.torii.auth.GoogleAuthRepository
import com.example.torii.repository.AuthRepository
import com.example.torii.screens.main.HomeScreen
import com.example.torii.screens.main.LearningScreen
import com.example.torii.screens.LoginScreen
import com.example.torii.screens.RegisterScreen
import com.example.torii.screens.main.TranslateScreen
import com.example.torii.screens.main.CommunityScreen
import com.example.torii.screens.words.CategoryScreen
import com.example.torii.screens.words.VocabularyScreen

@Composable
fun AppNavGraph(navController: NavHostController) {
    val navController = rememberNavController()
    val authRepo = AuthRepository() // Nếu AuthRepository có constructor
    val context = LocalContext.current
    val googleAuthRepo = GoogleAuthRepository(context)

    NavHost(navController = navController, startDestination = "splash") {
        composable("login") { LoginScreen(navController, authRepo, googleAuthRepo) }
        composable("register") { RegisterScreen(navController, authRepo, googleAuthRepo) }
        composable("splash") { SplashScreen(navController) }
        composable("greeting") { GreetingScreen(navController) }
        composable("home") { HomeScreen(
            navController,
            authRepo
        ) }
        composable("translate") { TranslateScreen(navController) }
        composable("learning") { LearningScreen(navController) }
        composable("community") { CommunityScreen(navController) }
        composable("vocabulary") { VocabularyScreen(navController) }
        composable("category") { CategoryScreen(navController) }
    }
}
