package com.example.torii.screens.words

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.Modifier
import com.example.torii.card.CategoryCard
import com.example.torii.model.Category
import com.example.torii.ui.theme.BeVietnamPro
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.torii.ui.theme.Feather

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(navController: NavController) {

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
        Category("Music", 55, "\uD83C\uDFB5"), // 🎵
        Category("Art", 40, "\uD83C\uDFA8"), // 🎨
        Category("Books", 35, "\uD83D\uDCDA"), // 📚
        Category("Health", 75, "\uD83E\uDDE0"), // 🧠
        Category("Fashion", 60, "\uD83D\uDC57"), // 👗
        Category("Science", 70, "\uD83E\uDDEA"), // 🧪
        Category("History", 45, "\uD83D\uDCDC"), // 📜
        Category("Movies", 50, "\uD83C\uDFAC"), // 🎬
        Category("Gaming", 85, "\uD83D\uDD79\uFE0F"), // 🕹️
        Category("Transport", 55, "\uD83D\uDE8C"), // 🚌
        Category("Weather", 40, "\uD83C\uDF26\uFE0F"), // ☂️
        Category("Finance", 65, "\uD83D\uDCB0"), // 💰
        Category("Education", 60, "\uD83C\uDF93"), // 🎓
        Category("Home", 50, "\uD83C\uDFE0"), // 🏠
        Category("Gardening", 45, "\uD83C\uDF31"), // 🌱
        Category("Pets", 70, "\uD83D\uDC36"), // 🐶
        Category("Holidays", 55, "\uD83C\uDF84"), // 🎄
        Category("Photography", 40, "\uD83D\uDCF8"), // 📸
        Category("Cooking", 75, "\uD83C\uDF73"), // 🍳
        Category("Fitness", 65, "\uD83C\uDFCB\uFE0F"), // 🏋️
        Category("Shopping", 60, "\uD83D\uDECD\uFE0F"), // 🛍️
        Category("Human", 50, "\uD83D\uDC64"), // 👨‍
        Category("General", 30, "\uD83D\uDC68")
    )
    var searchQuery by remember { mutableStateOf("") }


    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("All Categories", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color.White)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                placeholder = { Text("Find category...", fontFamily = Feather, fontSize = 18.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
                singleLine = true,
                textStyle = TextStyle(fontFamily = Feather, fontSize = 18.sp),
            )
            val filteredCategories = categories.filter {
                it.name.contains(searchQuery, ignoreCase = true)
            }
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), // Chia thành 2 cột
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
            ) {
                items(filteredCategories) { category ->
                    CategoryCard(navController, category)
                }
            }
        }
    }
}

