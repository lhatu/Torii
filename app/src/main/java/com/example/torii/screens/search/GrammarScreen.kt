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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.torii.card.GrammarCard
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.GrammarViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GrammarScreen(navController: NavController, viewModel: GrammarViewModel = viewModel()) {
    val grammarList by viewModel.filteredGrammarList.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedLevel by remember { mutableStateOf("All") }
    var isExpanded by remember { mutableStateOf(false) }
    val jlptLevels = listOf("All", "N5", "N4", "N3", "N2", "N1")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Grammar", fontWeight = FontWeight.Bold, fontFamily = Feather) },
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
            Row(
                modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ExposedDropdownMenuBox(
                    expanded = isExpanded,
                    onExpandedChange = { isExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedLevel,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isExpanded) },
                        modifier = Modifier.width(100.dp).menuAnchor(),
                        textStyle = TextStyle(fontFamily = Feather, fontSize = 16.sp),
                    )

                    ExposedDropdownMenu(
                        expanded = isExpanded,
                        onDismissRequest = { isExpanded = false }
                    ) {
                        jlptLevels.forEach { level ->
                            DropdownMenuItem(
                                text = { Text(level, fontFamily = Feather, fontSize = 16.sp) },
                                onClick = {
                                    selectedLevel = level
                                    isExpanded = false
                                    viewModel.filterByJlpt(level)
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        viewModel.searchGrammar(it)
                    },
                    placeholder = { Text("いつも, always", fontFamily = Nunito, fontSize = 16.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    modifier = Modifier.width(250.dp),
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = Nunito, fontSize = 18.sp),
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(grammarList) { grammar ->
                    GrammarCard(grammar)
                }
            }
        }
    }
}
