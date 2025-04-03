package com.example.torii.screens.main

import androidx.navigation.NavHostController
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(navController: NavHostController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Học tập") },
                actions = {
                    IconButton(onClick = { /* Cài đặt logic */ }) {
                        Icon(Icons.Default.Settings, contentDescription = "Cài đặt")
                    }
                    IconButton(onClick = { /* Avatar người dùng logic */ }) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Avatar")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar(navController) },
        content = { paddingValues -> // Truyền paddingValues vào phần nội dung
            Column(
                modifier = Modifier
                    .padding(paddingValues) // Áp dụng padding vào nội dung
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            ) {
                Text(
                    text = "Trang Học tập",
                    fontSize = 24.sp
                )
            }
        }
    )
}