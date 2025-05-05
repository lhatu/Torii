package com.example.torii.screens.study

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.torii.card.VocabularyNotebookCard
import com.example.torii.model.Notebook
import com.example.torii.model.NotebookVocabulary
import com.example.torii.model.Vocabulary
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.NotebookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotebookDetailScreen(
    navController: NavHostController,
    notebookId: String
) {
    val notebookViewModel: NotebookViewModel = viewModel()
    val selectedNotebook by notebookViewModel.selectedNotebook.collectAsState()
    val vocabularyList by notebookViewModel.currentNotebookVocabulary.collectAsState()
    val notebookDescription = selectedNotebook?.description ?: ""

    var showAddDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }


    LaunchedEffect(notebookId) {
        notebookViewModel.selectNotebook(notebookId)
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                TopAppBar(
                    title = {
                        Text(
                            text = selectedNotebook?.title ?: "Notebook",
                            fontWeight = Bold,
                            fontFamily = Feather
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    actions = {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete Notebook"
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black
                    )
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = Color(0xFF43A047)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Vocabulary",
                    tint = Color.White
                )
            }
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround,
                ) {
                    Button(
                        onClick = {
                            navController.navigate("flashcard/$notebookId")
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(150.dp)
                            .height(45.dp)
                    ) {
                        Text("Flashcards", fontFamily = Feather, fontSize = 18.sp, color = Color.White)
                    }

                    Button(
                        onClick = { navController.navigate("notebook/$notebookId/quiz") },
                        shape = RoundedCornerShape(50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2196F3)
                        ),
                        modifier = Modifier
                            .padding(vertical = 8.dp)
                            .width(150.dp)
                            .height(45.dp)
                    ) {
                        Text("Quiz", fontFamily = Feather, fontSize = 18.sp, color = Color.White)
                    }
                }

                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Spacer(modifier = Modifier.height(10.dp))

                    if (vocabularyList.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "No vocabulary added yet.\nPress the + button to add new words.",
                                fontFamily = BeVietnamPro,
                                fontSize = 16.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center
                            )
                        }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2), // 👈 2 cột
                            modifier = Modifier
                                .wrapContentHeight(),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(vocabularyList) { vocabItem ->
                                val vocabulary = NotebookVocabulary(
                                    expression = vocabItem.expression,
                                    reading = vocabItem.reading,
                                    meaning = vocabItem.meaning,
                                )

                                VocabularyNotebookCard(
                                    notebook = vocabulary,
                                    onClick = { /* Handle card click */ }
                                )
                            }
                        }
                    }
                }
            }


            if (showAddDialog) {
                AddVocabularyDialog(
                    onDismiss = { showAddDialog = false },
                    onAddVocabulary = { expression, reading, meaning, example ->
                        notebookViewModel.addVocabularyToNotebook(
                            notebookId = notebookId,
                            expression = expression,
                            reading = reading,
                            meaning = meaning
                        )
                        showAddDialog = false
                    }
                )
            }

            if (showDeleteDialog) {
                DeleteNotebookDialog(
                    onDismiss = { showDeleteDialog = false },
                    onConfirm = {
                        notebookViewModel.deleteNotebook(notebookId)
                        showDeleteDialog = false
                        navController.popBackStack()
                    }
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVocabularyDialog(
    onDismiss: () -> Unit,
    onAddVocabulary: (expression: String, reading: String, meaning: String, example: String) -> Unit
) {
    var expression by remember { mutableStateOf("") }
    var reading by remember { mutableStateOf("") }
    var meaning by remember { mutableStateOf("") }
    var example by remember { mutableStateOf("") }
    var isExpressionError by remember { mutableStateOf(false) }
    var isMeaningError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add new words",
                fontFamily = Feather,
                fontWeight = Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = expression,
                    onValueChange = {
                        expression = it
                        isExpressionError = it.isEmpty()
                    },
                    placeholder = { Text("Word: 赤い, 秋,...", fontSize = 16.sp, fontFamily = NotoSansJP) },
                    isError = isExpressionError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Nunito
                    ),
                    singleLine = true,
                )

                if (isExpressionError) {
                    Text(
                        text = "Expression cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = reading,
                    onValueChange = { reading = it },
                    placeholder = { Text("Reading: あかい, あき,...", fontSize = 16.sp, fontFamily = NotoSansJP) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Nunito
                    ),
                    singleLine = true,
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = meaning,
                    onValueChange = {
                        meaning = it
                        isMeaningError = it.isEmpty()
                    },
                    placeholder = { Text("Meaning: Red, Autumn,...", fontSize = 16.sp, fontFamily = Nunito) },
                    isError = isMeaningError,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = Nunito
                    ),
                    singleLine = true,
                )

                if (isMeaningError) {
                    Text(
                        text = "Meaning cannot be empty",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (expression.isNotEmpty() && meaning.isNotEmpty()) {
                        onAddVocabulary(expression, reading, meaning, example)
                    } else {
                        isExpressionError = expression.isEmpty()
                        isMeaningError = meaning.isEmpty()
                    }
                },
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047),
                ),
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Text("Add", fontFamily = Feather, fontSize = 17.sp)
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

@Composable
fun DeleteNotebookDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Delete Notebook",
                fontFamily = Feather,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = "Are you sure you want to delete this notebook? This action cannot be undone and all vocabulary in this notebook will be lost.",
                fontFamily = Nunito,
                fontSize = 16.sp,
                textAlign = TextAlign.Justify,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD32F2F),
                ),
                modifier = Modifier
                    .padding(start = 8.dp)
            ) {
                Text("Delete", fontFamily = Feather, fontSize = 17.sp)
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