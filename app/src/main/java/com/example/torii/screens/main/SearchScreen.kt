package com.example.torii.screens.main

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.torii.card.GrammarCard
import com.example.torii.card.KanjiCard
import com.example.torii.model.Grammar
import com.example.torii.model.Kanji
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavController) {

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Kanji") }

    // Fake data
    val history = listOf("水", "行く", "〜ている", "勉強する")
    val popularKanjis = listOf(
        Kanji("水", listOf("スイ"), listOf("みず"), 4, "Water", "N5", "水を飲みます。"),
        Kanji("火", listOf("カ"), listOf("ひ"), 4, "Fire", "N5", "火が強いです。"),
        Kanji("読", listOf("ドク", "トク", "トウ"), listOf("よ.む", "-よ.み"), 14, "Read", "N5", "木の下で休みます。"),
        Kanji("中", listOf("チュウ"), listOf("なか", "うち"), 4, "Inside", "N5", "お金を払います。")
    )
    val popularGrammar = listOf(
        Grammar(
            phrase = "ことがある/こともある",
            structure = "V/Vない + ことがある/こともある",
            meaning = "There are times when",
            explanation = "『ことがある/こともある』indicates that something may not happen frequently, but occasionally does occur.",
            jlptLevel = "N5",
            example = "大学を卒業した今でも、クラスメイトと会うことがあります。"
        ),
        Grammar(
            phrase = "だけましだ",
            structure = "V/Aい/Aな/N + だけましだ",
            meaning = "At least it's better than...",
            explanation = "The phrase 『だけましだ』 is used to express a situation that may not be very good, but it's still manageable, and there's still a bit of luck or relief.",
            jlptLevel = "N5",
            example = "給料は少ないけれど、仕事があるだけましだ。"
        )
    )

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Màu nền áp dụng cho cả padding
            ) {
                TopAppBar(
                    title = { Text("Search", fontWeight = Bold, fontFamily = Feather) },
                    modifier = Modifier.padding(end = 12.dp),
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent, // Để màu nền của Box hiển thị
                        titleContentColor = Color.Black // Màu chữ tiêu đề
                    ),
                    actions = {
                        // Action
                    }
                )

            }
        },
        bottomBar = {
            BottomNavigationBar(navController)
        },
        content = { paddingValues ->
            LazyColumn(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
            )
            {
                item {
                    // Kanji / Grammar Toggle
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        ChoiceChip("Kanji", selectedCategory == "Kanji") { selectedCategory = "Kanji" }
                        ChoiceChip("Grammar", selectedCategory == "Grammar") { selectedCategory = "Grammar" }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Search Field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            if (selectedCategory == "Kanji")
                                Text("金, キン, かね, gold", fontSize = 16.sp, fontFamily = Nunito)
                            else (Text("いつも, always", fontSize = 16.sp, fontFamily = Nunito)) },
                        modifier = Modifier
                            .fillMaxWidth(),
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search Icon"
                            )
                        },
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Nunito
                        ),
                        singleLine = true,
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    // History
                    Text("Search History", fontWeight = Bold, fontFamily = Feather, fontSize = 23.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(history, key = {it}) {item ->
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFFE8F5E9), shape = RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(text = item, fontWeight = Bold, fontSize = 16.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    // Kanji
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Kanji", fontWeight = Bold, fontFamily = Feather, fontSize = 23.sp)

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 16.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = Bold,
                            modifier = Modifier
                                .clickable { navController.navigate("kanji") }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 520.dp)
                    ) {
                        items(popularKanjis) { kanji ->
                            KanjiCard(kanji)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Grammar
                    Text("Grammar", fontWeight = Bold, fontFamily = Feather, fontSize = 23.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        popularGrammar.forEach { grammar ->
                            GrammarCard(grammar)
                        }
                    }
                }
            }
        }
    )


}

@Composable
fun ChoiceChip(label: String, selected: Boolean, onSelected: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(50.dp))
            .background(if (selected) Color(0xFF58CC02) else Color.White)
            .clickable { onSelected() }
            .width(120.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            color = if (selected) Color.White else Color.Black,
            fontFamily = Nunito,
            fontWeight = Bold,
            fontSize = 18.sp,
        )
    }
}


