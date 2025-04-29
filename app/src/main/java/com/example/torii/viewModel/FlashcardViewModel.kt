package com.example.torii.viewModel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.model.Flashcard
import com.example.torii.model.NotebookVocabulary
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class FlashcardViewModel : ViewModel() {
    private val _flashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val flashcards: StateFlow<List<Flashcard>> = _flashcards

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex

    private val _isFlipped = MutableStateFlow(false)
    val isFlipped: StateFlow<Boolean> = _isFlipped

    fun loadFlashcards(notebookId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            db.collection("notebookVocabulary")
                .whereEqualTo("notebookId", notebookId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val list = snapshot.documents.mapNotNull {
                        it.toObject(NotebookVocabulary::class.java)?.let { vocab ->
                            Flashcard(
                                id = vocab.id,
                                expression = vocab.expression,
                                reading = vocab.reading,
                                meaning = vocab.meaning
                            )
                        }
                    }
                    _flashcards.value = list
                    _currentIndex.value = 0
                }
        }
    }

    fun flipCard() {
        _isFlipped.value = !_isFlipped.value
    }

    fun nextCard() {
        if (_currentIndex.value < _flashcards.value.lastIndex) {
            _currentIndex.value++
            _isFlipped.value = false
        }
    }

    fun previousCard() {
        if (_currentIndex.value > 0) {
            _currentIndex.value--
            _isFlipped.value = false
        }
    }

    fun shuffleFlashcards() {
        _flashcards.value = _flashcards.value.shuffled()
        _currentIndex.value = 0
    }
}