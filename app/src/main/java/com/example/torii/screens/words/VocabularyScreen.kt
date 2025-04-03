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
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.torii.model.NewWord
import com.example.torii.ui.theme.BeVietnamPro
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import com.example.torii.card.NewWordCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VocabularyScreen(navController: NavController) {

    val options = listOf("All", "Animal", "Food", "Technology", "Travel")
    var expanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(options.get(0)) }

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
                title = { Text("Vocabulary List", fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro) },
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
                .background(Color(0xFFF2F6FF))
                .padding(horizontal = 16.dp)
        ) {
            // Dropdown để lọc theo chủ đề
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded },
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    value = selectedCategory,
                    onValueChange = { selectedCategory = it },
                    readOnly = true,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    textStyle = TextStyle(fontFamily = BeVietnamPro, fontSize = 16.sp),
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
                                    fontFamily = BeVietnamPro,
                                    fontSize = 16.sp
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
            Spacer(modifier = Modifier.height(10.dp))

//          Hiển thị danh sách các từ vựng đã lọc
            LazyColumn {
                items(filteredWords) { word ->
                    NewWordCard(word)
                    Spacer(modifier = Modifier.height(10.dp))
                }
            }
        }
    }
}


