package com.example.torii.screens.main

import LessonViewModel
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.torii.card.NotebookCard
import com.example.torii.model.Notebook
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.NotebookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LearningScreen(navController: NavHostController) {
    val notebookViewModel: NotebookViewModel = viewModel()
    val notebooks by notebookViewModel.notebooks.collectAsState()

    val lessonViewModel: LessonViewModel = viewModel()
    val lessons by lessonViewModel.lessons.collectAsState()

    val conversationLessons by remember(lessons) {
        mutableStateOf(lessons.filter { it.id.contains("conversation", ignoreCase = true) })
    }
    val grammarLessons by remember(lessons) {
        mutableStateOf(lessons.filter { it.id.contains("grammar", ignoreCase = true) })
    }
    val kanjiLessons by remember(lessons) {
        mutableStateOf(lessons.filter { it.id.contains("kanji", ignoreCase = true) })
    }

    val context = LocalContext.current

    var showCreateDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White) // Màu nền áp dụng cho cả padding
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("Study", fontFamily = Feather) },
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
        bottomBar = { BottomNavigationBar(navController) },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.White)
                    .padding(horizontal = 16.dp)
            ) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Notebooks",
                            fontFamily = Feather,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Text(
                            text = "Create",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 18.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = Bold,
                            modifier = Modifier
                                .clickable { showCreateDialog = true }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .heightIn(max = 800.dp)
                            .fillMaxWidth(),

                        contentPadding = PaddingValues(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(notebooks) { notebook ->
                            NotebookCard(
                                notebook = notebook,
                                onClick = {
                                    navController.navigate("notebook_detail/${notebook.id}")
                                },
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Learn Japanese Alphabet",
                            fontFamily = Feather,
                            fontSize = 25.sp,
                            fontWeight = Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(cardList.size) { index ->
                            val cardData = cardList[index]
                            Card(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(230.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate("alphabet/${cardData.route}")
                                    },
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = cardData.imageUrl,
                                        contentDescription = cardData.title,
                                        modifier = Modifier
                                            .height(130.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = cardData.title,
                                            style = TextStyle(fontSize = 17.sp, fontWeight = Bold),
                                            lineHeight = 20.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = cardData.duration,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                            Text(
                                                text = cardData.date,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Daily Conversations",
                            fontFamily = Feather,
                            fontSize = 23.sp,
                            fontWeight = Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 18.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = Bold,
                            modifier = Modifier
                                .clickable { showCreateDialog = true }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(conversationLessons) { lesson ->
                            Card(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(250.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate("lessons/${lesson.id}")
                                    },
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = lesson.imageUrl,
                                        contentDescription = lesson.title,
                                        modifier = Modifier
                                            .height(130.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = lesson.title,
                                            style = TextStyle(fontSize = 17.sp, fontWeight = Bold),
                                            lineHeight = 22.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = lesson.duration,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                            Text(
                                                text = lesson.datePost,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Japanese Grammar",
                            fontFamily = Feather,
                            fontSize = 23.sp,
                            fontWeight = Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 18.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = Bold,
                            modifier = Modifier
                                .clickable { showCreateDialog = true }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(grammarLessons) { lesson ->
                            Card(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(250.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate("lessons/${lesson.id}")
                                    },
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = lesson.imageUrl,
                                        contentDescription = lesson.title,
                                        modifier = Modifier
                                            .height(130.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = lesson.title,
                                            style = TextStyle(fontSize = 17.sp, fontWeight = Bold),
                                            lineHeight = 22.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = lesson.duration,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                            Text(
                                                text = lesson.datePost,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Japanese Kanji",
                            fontFamily = Feather,
                            fontSize = 23.sp,
                            fontWeight = Bold,
                            modifier = Modifier.padding(bottom = 10.dp)
                        )

                        Text(
                            text = "See All",
                            color = Color(0xFF007BFF), // Màu xanh nước biển
                            fontSize = 18.sp, // Nhỏ hơn tiêu đề
                            fontFamily = Feather,
                            fontWeight = Bold,
                            modifier = Modifier
                                .clickable { showCreateDialog = true }
                                .padding(4.dp) // Khoảng cách để dễ nhấn
                        )
                    }
                }

                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(kanjiLessons) { lesson ->
                            Card(
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(250.dp)
                                    .padding(8.dp)
                                    .clickable {
                                        navController.navigate("lessons/${lesson.id}")
                                    },
                                shape = RoundedCornerShape(10.dp),
                                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column {
                                    AsyncImage(
                                        model = lesson.imageUrl,
                                        contentDescription = lesson.title,
                                        modifier = Modifier
                                            .height(130.dp)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp)),
                                        contentScale = ContentScale.Crop
                                    )
                                    Column(
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(8.dp),
                                        verticalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = lesson.title,
                                            style = TextStyle(fontSize = 17.sp, fontWeight = Bold),
                                            lineHeight = 22.sp,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = lesson.duration,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                            Text(
                                                text = lesson.datePost,
                                                style = TextStyle(
                                                    fontSize = 14.sp,
                                                    color = Color.Gray,
                                                    fontFamily = Nunito
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (showCreateDialog) {
                CreateNotebookDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreateNotebook = { title, description ->
                        notebookViewModel.createNotebook(title, description)
                        showCreateDialog = false
                    }
                )
            }
        }
    )
}

@Composable
fun CreateNotebookDialog(
    onDismiss: () -> Unit,
    onCreateNotebook: (title: String, description: String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isTitleError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Create new Notebook",
                fontFamily = Feather,
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = {
                        title = it
                        isTitleError = it.isEmpty()
                    },
                    placeholder = { Text("Title: JLPT N5...", fontSize = 16.sp, fontFamily = Nunito) },
                    isError = isTitleError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Nunito
                    ),
                    singleLine = true,
                )

                if (isTitleError) {
                    Text(
                        text = "Title cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    placeholder = { Text("Description (Optional)", fontSize = 16.sp, fontFamily = Nunito) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Nunito
                    ),
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotEmpty()) {
                        onCreateNotebook(title, description)
                    } else {
                        isTitleError = true
                    }
                },
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047),
                ),
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Text("Create", fontFamily = Feather, fontSize = 17.sp)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD9D9D9),
                ),
            ) {
                Text("Cancel", fontFamily = Feather, fontSize = 17.sp, color = Color.Black)
            }
        }
    )
}

private val cardList = listOf(
    CardData(
        imageUrl = "https://i.ytimg.com/vi/6p9Il_j0zjc/maxresdefault.jpg",
        title = "Japanese Alphabet Hiragana",
        duration = "1:04:31",
        date = "May 08, 2025",
        route = "hiragana"
    ),
    CardData(
        imageUrl = "https://i.ytimg.com/vi/s6DKRgtVLGA/maxresdefault.jpg",
        title = "Japanese Alphabet Katakana",
        duration = "1:10:20",
        date = "May 09, 2025",
        route = "katakana"
    )
)

data class CardData(
    val imageUrl: String,
    val title: String,
    val duration: String,
    val date: String,
    val route: String
)