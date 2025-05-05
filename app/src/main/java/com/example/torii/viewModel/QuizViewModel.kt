package com.example.torii.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.model.NotebookVocabulary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

sealed class QuizState {
    object Loading : QuizState()
    data class Ready(
        val currentQuestion: QuizQuestion,
        val currentIndex: Int,
        val totalQuestions: Int,
        val score: Int,
        val selectedAnswer: String? = null, // Thêm trạng thái lựa chọn
        val isAnswerCorrect: Boolean? = null // Thêm trạng thái đúng/sai
    ) : QuizState()
    data class Finished(val totalQuestions: Int, val correctAnswers: Int) : QuizState()
    data class Error(val message: String) : QuizState()
}

data class QuizQuestion(
    val vocabulary: NotebookVocabulary,
    val options: List<String>,
    val correctAnswer: String
)

class QuizViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _quizState = MutableStateFlow<QuizState>(QuizState.Loading)
    val quizState: StateFlow<QuizState> = _quizState

    private lateinit var vocabularies: List<NotebookVocabulary>
    private var currentQuestions: List<QuizQuestion> = emptyList()
    private var currentIndex = 0
    private var score = 0

    fun loadVocabularies(notebookId: String) {
        viewModelScope.launch {
            try {
                _quizState.value = QuizState.Loading

                val snapshot = firestore.collection("notebookVocabulary")
                    .whereEqualTo("notebookId", notebookId)
                    .get()
                    .await()

                vocabularies = snapshot.toObjects(NotebookVocabulary::class.java)

                if (vocabularies.isEmpty()) {
                    _quizState.value = QuizState.Error("No vocabularies found in this notebook")
                    return@launch
                }

                prepareQuestions()
                showNextQuestion()
            } catch (e: Exception) {
                _quizState.value = QuizState.Error("Failed to load vocabularies: ${e.message}")
            }
        }
    }

    private fun prepareQuestions() {
        currentQuestions = vocabularies.map { vocab ->
            // Generate 3 random wrong answers plus the correct one
            val wrongAnswers = vocabularies
                .filter { it.meaning != vocab.meaning }
                .shuffled()
                .take(3)
                .map { it.meaning }

            val allOptions = (wrongAnswers + vocab.meaning).shuffled()

            QuizQuestion(
                vocabulary = vocab,
                options = allOptions,
                correctAnswer = vocab.meaning
            )
        }.shuffled() // Shuffle the questions
    }

    fun submitAnswer(selectedAnswer: String) {
        val currentState = _quizState.value
        if (currentState !is QuizState.Ready) return

        // Cập nhật trạng thái với lựa chọn và kết quả
        val isCorrect = selectedAnswer == currentState.currentQuestion.correctAnswer
        _quizState.value = currentState.copy(
            selectedAnswer = selectedAnswer,
            isAnswerCorrect = isCorrect
        )

        if (isCorrect) {
            score++
        }

        viewModelScope.launch {
            // Delay để hiển thị màu đúng/sai trước khi chuyển câu hỏi
            delay(1000) // 1 giây delay

            if (currentIndex + 1 < currentQuestions.size) {
                currentIndex++
                showNextQuestion()
            } else {
                _quizState.value = QuizState.Finished(
                    totalQuestions = currentQuestions.size,
                    correctAnswers = score
                )
            }
        }
    }

    private fun showNextQuestion() {

        _quizState.value = QuizState.Ready(
            currentQuestion = currentQuestions[currentIndex],
            currentIndex = currentIndex,
            totalQuestions = currentQuestions.size,
            score = score,
            selectedAnswer = null,
            isAnswerCorrect = null
        )
    }

    fun restartQuiz() {
        currentIndex = 0
        score = 0
        prepareQuestions()
        showNextQuestion()
    }
}