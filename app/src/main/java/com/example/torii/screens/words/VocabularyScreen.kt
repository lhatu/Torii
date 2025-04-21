package com.example.torii.screens.words

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.torii.model.NewWord
import androidx.compose.foundation.lazy.*
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torii.card.NewWordCard
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.VocabularyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(navController: NavController, category: String, viewModel: VocabularyViewModel = viewModel()) {

    val options = listOf("All", "Animal", "Food", "Technology", "Travel")
    var expanded by remember { mutableStateOf(false) }
    val category = category
    var selectedCategory by remember { mutableStateOf(category) }
    var searchText by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadAllVocabulary()
    }
    val vocabList = viewModel.vocabList.observeAsState(emptyList())

    val words = listOf(
        NewWord("Elephant", "Animal", "ˈɛlɪfənt", "a very large grey mammal", "The elephant is the largest land animal.", "https://cdn.mos.cms.futurecdn.net/TVR7E3Kuzg2iRhKkjZPeWk-1200-80.jpg"),
        NewWord("Pizza", "Food", "ˈpiːtsə", "a large circle of flat bread baked with cheese", "I love eating pizza on weekends.", "https://dictionary.cambridge.org/images/thumb/pizza_noun_001_12184.jpg?version=6.0.48"),
        NewWord("Smartphone", "Technology", "ˈsmɑːrt.foʊn", "a mobile phone that can be used", "He just bought a new smartphone.", "https://image.vietstock.vn/2023/05/26/dien-thoai.jpg"),
        NewWord("Lion", "Animal", "ˈlaɪ.ən", "a large wild cat", "The lion is the king of the jungle.", "https://cdn.britannica.com/29/150929-050-547070A1/lion-Kenya-Masai-Mara-National-Reserve.jpg"),
        NewWord("Burger", "Food", "ˈbɜːr.ɡər", "a round patty of ground beef", "I ordered a cheeseburger for lunch.", "https://mcdonalds.vn/uploads/2018/food/burgers/xmcroyaldlx_bb.png.pagespeed.ic.kULCpBffVo.png"),
        NewWord("Laptop", "Technology", "ˈlæp.tɒp", "a portable computer", "She works on her laptop all day.", "https://laptops.vn/wp-content/uploads/2025/02/laptop-cu-2.jpg"),
        NewWord("Airplane", "Travel", "ˈer.pleɪn", "a vehicle that flies", "We took an airplane to Paris.", "https://img.freepik.com/premium-photo/plane-was-flying-airport_136375-714.jpg?w=360")
    )

    // Lọc từ vựng theo category được chọn
    val filteredWords = remember(selectedCategory) {
        if (selectedCategory == "All") {
            words
        } else {
            words.filter { it.category == selectedCategory }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Vocabulary", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
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
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                ) {
                    OutlinedTextField(
                        modifier = Modifier
                            .menuAnchor()
                            .width(110.dp),
                        value = selectedCategory,
                        onValueChange = { selectedCategory = it },
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        textStyle = TextStyle(fontFamily = Feather, fontSize = 16.sp),
                        maxLines = 1
                    )
                    // Filter options based on text field value
                    if (true) {
                        ExposedDropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            options.forEach { selectionOption ->
                                DropdownMenuItem(
                                    text = { Text(
                                        selectionOption,
                                        fontFamily = Feather,
                                        fontSize = 16.sp,
                                        maxLines = 1,
                                    ) },
                                    onClick = {
                                        selectedCategory = selectionOption
                                        expanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                                )
                            }
                        }
                    }
                }

                // 🔍 Ô tìm kiếm
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    modifier = Modifier
                        .width(240.dp),
                    placeholder = { Text("Find words...", fontFamily = Nunito, fontSize = 18.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Tìm kiếm") },
                    singleLine = true,
                    textStyle = TextStyle(fontFamily = Nunito, fontSize = 18.sp),
                )
            }

            // Lọc danh sách từ vựng theo searchText và category
            val searchWord = vocabList.value.filter { word ->
                (selectedCategory == "All" || word.category == selectedCategory) && // Nếu selectedCategory là "All", không lọc theo category
                        (searchText.isEmpty() || word.meaning.contains(searchText, ignoreCase = true) ||
                                word.meaning.contains(searchText, ignoreCase = true)) // Lọc theo searchText
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Hiển thị danh sách các từ vựng đã lọc
            LazyColumn {
                items(searchWord) { word ->
                    NewWordCard(word)
                }
            }
        }
    }
}


