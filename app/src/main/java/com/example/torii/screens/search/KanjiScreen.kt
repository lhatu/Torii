package com.example.torii.screens.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torii.card.KanjiCard
import com.example.torii.viewModel.KanjiViewModel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.torii.card.NewWordCard
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiScreen(navController: NavController, viewModel: KanjiViewModel = viewModel()) {
    val kanjiList by viewModel.filteredKanjiList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("All") }
    var isExpanded by remember { mutableStateOf(false) }
    val jlptLevels = listOf("All", "N5", "N4", "N3", "N2", "N1")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Kanji", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("search") }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Row chứa Dropdown và Search
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Dropdown chọn level JLPT
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLevel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier
                            .width(100.dp)
                            .menuAnchor(),
                        textStyle = TextStyle(fontFamily = Feather, fontSize = 16.sp),
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        jlptLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(
                                    level,
                                    fontFamily = Feather,
                                    fontSize = 16.sp,
                                    maxLines = 1,
                                ) },
                                onClick = {
                                    selectedLevel = level
                                    isExpanded = false
                                    // Gọi hàm filter theo level ở ViewModel
                                    viewModel.filterByJlptLevel(level)
                                }
                            )
                        }
                    }
                }

                // Thanh tìm kiếm
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchKanji(it)
                    },
                    placeholder = { Text("金, キン, かね, gold", fontFamily = Nunito, fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .fillMaxWidth(),
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = Nunito, fontSize = 18.sp),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Lưới hiển thị kanji
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(kanjiList) { kanji ->
                    KanjiCard(kanji)
                }
            }
        }
    }
}
