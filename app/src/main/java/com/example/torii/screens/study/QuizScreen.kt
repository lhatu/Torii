package com.example.torii.screens.study

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.NotoSansJP
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.QuizState
import com.example.torii.viewModel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    notebookId: String,
    onBack: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val quizState by viewModel.quizState.collectAsState()

    LaunchedEffect(notebookId) {
        viewModel.loadVocabularies(notebookId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quiz", fontWeight = FontWeight.Bold, fontFamily = Feather) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            when (val state = quizState) {
                is QuizState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Color(0xFF43A047)
                    )
                }
                is QuizState.Ready -> {
                    QuestionContent(
                        state = state,
                        onSubmitAnswer = viewModel::submitAnswer
                    )
                }
                is QuizState.Finished -> {
                    QuizResult(
                        totalQuestions = state.totalQuestions,
                        correctAnswers = state.correctAnswers,
                        onRestart = viewModel::restartQuiz,
                        onBack = onBack
                    )
                }
                is QuizState.Error -> {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onBack) {
                            Text("Go Back")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuestionContent(
    state: QuizState.Ready,
    onSubmitAnswer: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = state.currentIndex.toFloat() / state.totalQuestions.toFloat(),
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFF43A047)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Score and progress text
            Text(
                text = "Question ${state.currentIndex + 1}/${state.totalQuestions}    •    Score: ${state.score}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Feather
            )
        }

        // Vocabulary expression and reading
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = state.currentQuestion.vocabulary.expression,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = NotoSansJP
            )

            if (state.currentQuestion.vocabulary.reading.isNotEmpty()) {
                Text(
                    text = state.currentQuestion.vocabulary.reading,
                    fontSize = 18.sp,
                    color = Color.Gray,
                    fontFamily = NotoSansJP
                )
            }
        }

        // Options grid - 2 rows, 2 columns
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(25.dp)
        ) {
            val rows = state.currentQuestion.options.chunked(2)

            rows.forEach { rowOptions ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    rowOptions.forEach { option ->
                        val isSelected = state.selectedAnswer == option
                        val isCorrectAnswer = option == state.currentQuestion.correctAnswer

                        // Animation cho màu nền
                        val animatedBackgroundColor by animateColorAsState(
                            targetValue = when {
                                isSelected && state.isAnswerCorrect == true ->
                                    Color.Green.copy(alpha = 0.2f)
                                isSelected && state.isAnswerCorrect == false ->
                                    Color.Red.copy(alpha = 0.2f)
                                !isSelected && state.isAnswerCorrect != null && isCorrectAnswer ->
                                    Color.Green.copy(alpha = 0.2f)
                                else -> MaterialTheme.colorScheme.surface
                            },
                            animationSpec = tween(durationMillis = 500)
                        )

                        // Animation cho màu viền
                        val animatedBorderColor by animateColorAsState(
                            targetValue = when {
                                isSelected && state.isAnswerCorrect == true -> Color.Green
                                isSelected && state.isAnswerCorrect == false -> Color.Red
                                !isSelected && state.isAnswerCorrect != null && isCorrectAnswer ->
                                    Color.Green
                                else -> MaterialTheme.colorScheme.outline
                            },
                            animationSpec = tween(durationMillis = 500)
                        )

                        // Animation cho độ dày viền
                        val animatedBorderWidth by animateDpAsState(
                            targetValue = when {
                                isSelected || (state.isAnswerCorrect != null && isCorrectAnswer) ->
                                    3.dp
                                else -> 2.dp
                            },
                            animationSpec = tween(durationMillis = 500)
                        )

                        // Animation cho scale khi chọn
                        val animatedScale by animateFloatAsState(
                            targetValue = if (isSelected) 1.05f else 1f,
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioMediumBouncy,
                                stiffness = Spring.StiffnessLow
                            )
                        )

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f)
                                .graphicsLayer {
                                    scaleX = animatedScale
                                    scaleY = animatedScale
                                }
                                .border(
                                    width = animatedBorderWidth,
                                    color = animatedBorderColor,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .background(
                                    color = animatedBackgroundColor,
                                    shape = MaterialTheme.shapes.medium
                                )
                                .clickable(
                                    enabled = state.selectedAnswer == null,
                                    onClick = { onSubmitAnswer(option) }
                                )
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Thêm animation cho nội dung text
                            val animatedTextAlpha by animateFloatAsState(
                                targetValue = if (state.selectedAnswer == null || isSelected ||
                                    (state.isAnswerCorrect != null && isCorrectAnswer)) 1f else 0.5f,
                                animationSpec = tween(durationMillis = 300)
                            )

                            Text(
                                text = option,
                                textAlign = TextAlign.Center,
                                fontFamily = Nunito,
                                fontSize = 20.sp,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .alpha(animatedTextAlpha)
                            )
                        }
                    }
                }
            }
        }

        // Spacer to push content up
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun QuizResult(
    totalQuestions: Int,
    correctAnswers: Int,
    onRestart: () -> Unit,
    onBack: () -> Unit
) {
    val percentage = correctAnswers.toFloat() / totalQuestions.toFloat()
    val color = when {
        percentage >= 0.8f -> Color.Green
        percentage >= 0.5f -> Color.Yellow
        else -> Color.Red
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Quiz Completed!",
            fontSize = 28.sp,
            fontFamily = Feather,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(40.dp))

        CircularProgressIndicator(
            progress = percentage,
            modifier = Modifier.size(120.dp),
            strokeWidth = 8.dp,
            color = color
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "${(percentage * 100).toInt()}%",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = Feather,
            modifier = Modifier.padding(top = 16.dp),
        )

        Text(
            text = "$correctAnswers out of $totalQuestions correct",
            fontSize = 22.sp,
            fontFamily = Nunito,
            modifier = Modifier.padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onBack,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF2196F3)
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .wrapContentWidth()
                    .height(45.dp)
            ) {
                Text("Back to Notebook", fontFamily = Feather, fontSize = 15.sp, color = Color.White)

            }

            Button(
                onClick = onRestart,
                shape = RoundedCornerShape(15.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF43A047)
                ),
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .wrapContentWidth()
                    .height(45.dp)
            ) {
                Text("Try Again", fontFamily = Feather, fontSize = 15.sp, color = Color.White)
            }
        }
    }
}