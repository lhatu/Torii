package com.example.torii

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.torii.auth.GoogleAuthRepository
import com.example.torii.repository.AuthRepository
import com.example.torii.screens.main.HomeScreen
import com.example.torii.screens.main.LearningScreen
import com.example.torii.screens.LoginScreen
import com.example.torii.screens.RegisterScreen
import com.example.torii.screens.community.UserProfileScreen
import com.example.torii.screens.home.NotificationScreen
import com.example.torii.screens.home.SettingsScreen
import com.example.torii.screens.home.articles.ArticleDetailScreen
import com.example.torii.screens.home.articles.ArticlesScreen
import com.example.torii.screens.main.CommunityScreen
import com.example.torii.screens.search.KanjiScreen
import com.example.torii.screens.home.video.VideoScreen
import com.example.torii.screens.home.words.CategoryScreen
import com.example.torii.screens.home.words.VocabularyScreen
import com.example.torii.screens.main.SearchScreen
import com.example.torii.screens.search.GrammarScreen
import com.example.torii.screens.study.FlashcardScreen
import com.example.torii.screens.study.JapaneseAlphabetScreen
import com.example.torii.screens.study.LessonDetailScreen
import com.example.torii.screens.study.NotebookDetailScreen
import com.example.torii.screens.study.QuizScreen

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavGraph(navController: NavHostController) {
    val navController = rememberNavController()
    val authRepo = AuthRepository()
    val context = LocalContext.current
    val googleAuthRepo = GoogleAuthRepository(context)

    val currentUser = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser

    NavHost(navController = navController, startDestination = if (currentUser != null) "home" else "splash") {
        composable("login") { LoginScreen(navController, authRepo, googleAuthRepo) }
        composable("register") { RegisterScreen(navController, authRepo, googleAuthRepo) }
        composable("splash") { SplashScreen(navController) }
        composable("greeting") { GreetingScreen(navController) }
        composable("home") { HomeScreen(
            navController,
            authRepo
        ) }
        composable("search") { SearchScreen(navController) }
        composable("learning") { LearningScreen(navController) }
        composable("community") { CommunityScreen(navController) }
        composable("category") { CategoryScreen(navController) }
        composable("vocabulary/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: ""
            VocabularyScreen(navController, category)
        }
        composable("articles") { ArticlesScreen(navController) }
        composable(
            route = "article_detail/{title}/{publishDate}/{content}/{imageUrl}/{audioUrl}",
            arguments = listOf(
                navArgument("title") { type = NavType.StringType },
                navArgument("publishDate") { type = NavType.StringType },
                navArgument("content") { type = NavType.StringType },
                navArgument("imageUrl") { type = NavType.StringType },
                navArgument("audioUrl") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val title = backStackEntry.arguments?.getString("title") ?: ""
            val publishDate = backStackEntry.arguments?.getString("publishDate") ?: ""
            val content = backStackEntry.arguments?.getString("content") ?: ""
            val imageUrl = backStackEntry.arguments?.getString("imageUrl") ?: ""
            val audioUrl = backStackEntry.arguments?.getString("audioUrl") ?: ""
            ArticleDetailScreen(title, publishDate, content, imageUrl, audioUrl, navController)
        }
        composable("video/{videoId}") { backStackEntry ->
            val videoId = backStackEntry.arguments?.getString("videoId") ?: return@composable
            VideoScreen(navController, videoId = videoId)
        }
        composable("videos") { VideoScreen(navController) }
        composable("kanji") { KanjiScreen(navController) }
        composable("grammar") { GrammarScreen(navController) }

        // Notebook routes
        composable(
            route = "notebook_detail/{notebookId}",
            arguments = listOf(
                navArgument("notebookId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val notebookId = backStackEntry.arguments?.getString("notebookId") ?: ""
            NotebookDetailScreen(navController, notebookId)
        }
        composable("flashcard/{notebookId}") { backStackEntry ->
            val notebookId = backStackEntry.arguments?.getString("notebookId") ?: ""
            FlashcardScreen(navController, notebookId)
        }

        composable(
            route = "notebook/{notebookId}/quiz",
            arguments = listOf(navArgument("notebookId") { type = NavType.StringType })
        ) { backStackEntry ->
            val notebookId = backStackEntry.arguments?.getString("notebookId") ?: return@composable
            QuizScreen(
                notebookId = notebookId,
                onBack = { navController.popBackStack() }
            )
        }
        composable("alphabet/{selectTab}") { backStackEntry ->
            val selectTab = backStackEntry.arguments?.getString("selectTab") ?: "hiragana"
            JapaneseAlphabetScreen(
                navController = navController,
                selectTab = selectTab
            )
        }
        // Trong NavGraph của bạn
        composable(
            route = "lessons/{lessonId}",
            arguments = listOf(navArgument("lessonId") { type = NavType.StringType })
        ) { backStackEntry ->
            val lessonId = backStackEntry.arguments?.getString("lessonId") ?: ""
            LessonDetailScreen(lessonId = lessonId)
        }
        composable("profile") { UserProfileScreen(navController) }
        composable("settings") {
            SettingsScreen(
                navController = navController,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo("home") { inclusive = true }
                    }
                }
            )
        }
        composable("notification") { NotificationScreen(navController) }
    }
}
