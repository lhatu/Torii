package com.example.torii.model

import androidx.compose.ui.graphics.vector.ImageVector

data class BottomNavItem(val route: String, val label: String, val selectedIcon: ImageVector, val unselectedIcon: ImageVector)
