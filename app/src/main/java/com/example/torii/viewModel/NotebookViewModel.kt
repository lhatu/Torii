package com.example.torii.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.model.Notebook
import com.example.torii.model.NotebookVocabulary
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class NotebookViewModel(application: Application) : AndroidViewModel(application) {

    private val _notebooks = MutableStateFlow<List<Notebook>>(emptyList())
    val notebooks: StateFlow<List<Notebook>> = _notebooks

    private val _currentNotebookVocabulary = MutableStateFlow<List<NotebookVocabulary>>(emptyList())
    val currentNotebookVocabulary: StateFlow<List<NotebookVocabulary>> = _currentNotebookVocabulary

    private val _selectedNotebook = MutableStateFlow<Notebook?>(null)
    val selectedNotebook: StateFlow<Notebook?> = _selectedNotebook

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        loadNotebooks()
    }

    fun loadNotebooks() {
        val userId = auth.currentUser?.uid ?: return

        db.collection("notebooks")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Error getting notebooks.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val notebookList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(Notebook::class.java)?.copy(id = doc.id)
                    }
                    _notebooks.value = notebookList.sortedByDescending { it.createdAt }
                }
            }
    }

    fun createNotebook(title: String, description: String) {
        val userId = auth.currentUser?.uid ?: return

        val newNotebook = Notebook(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            createdAt = System.currentTimeMillis(),
            wordCount = 0,
            userId = userId
        )

        db.collection("notebooks")
            .document(newNotebook.id)
            .set(newNotebook)
            .addOnSuccessListener {
                Log.d("Firestore", "Notebook created successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error creating notebook", e)
            }
    }

    fun selectNotebook(notebookId: String) {
        _selectedNotebook.value = _notebooks.value.find { it.id == notebookId }
        loadNotebookVocabulary(notebookId)
    }

    fun loadNotebookVocabulary(notebookId: String) {
        db.collection("notebookVocabulary")
            .whereEqualTo("notebookId", notebookId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("Firestore", "Error getting vocabulary.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val vocabularyList = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(NotebookVocabulary::class.java)?.copy(id = doc.id)
                    }
                    _currentNotebookVocabulary.value = vocabularyList.sortedByDescending { it.addedAt }
                }
            }
    }

    fun addVocabularyToNotebook(
        notebookId: String,
        expression: String,
        reading: String,
        meaning: String
    ) {
        val userId = auth.currentUser?.uid ?: return

        // Tạo vocabulary mới
        val newVocabulary = NotebookVocabulary(
            id = UUID.randomUUID().toString(),
            notebookId = notebookId,
            expression = expression,
            reading = reading,
            meaning = meaning,
            addedAt = System.currentTimeMillis()
        )

        // Thêm từ vựng vào collection
        db.collection("notebookVocabulary")
            .document(newVocabulary.id)
            .set(newVocabulary)
            .addOnSuccessListener {
                // Cập nhật số lượng từ trong notebook
                val notebookRef = db.collection("notebooks").document(notebookId)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(notebookRef)
                    val currentCount = snapshot.getLong("wordCount") ?: 0
                    transaction.update(notebookRef, "wordCount", currentCount + 1)
                }
                Log.d("Firestore", "Vocabulary added successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error adding vocabulary", e)
            }
    }

    fun deleteNotebook(notebookId: String) {
        // Xóa notebook
        db.collection("notebooks")
            .document(notebookId)
            .delete()
            .addOnSuccessListener {
                // Xóa tất cả từ vựng trong notebook
                db.collection("notebookVocabulary")
                    .whereEqualTo("notebookId", notebookId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val batch = db.batch()
                        snapshot.documents.forEach { doc ->
                            batch.delete(doc.reference)
                        }
                        batch.commit()
                        Log.d("Firestore", "Notebook and all vocabulary deleted")
                    }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting notebook", e)
            }
    }

    fun deleteVocabulary(vocabularyId: String, notebookId: String) {
        db.collection("notebookVocabulary")
            .document(vocabularyId)
            .delete()
            .addOnSuccessListener {
                // Cập nhật số lượng từ trong notebook
                val notebookRef = db.collection("notebooks").document(notebookId)
                db.runTransaction { transaction ->
                    val snapshot = transaction.get(notebookRef)
                    val currentCount = snapshot.getLong("wordCount") ?: 0
                    if (currentCount > 0) {
                        transaction.update(notebookRef, "wordCount", currentCount - 1)
                    }
                }
                Log.d("Firestore", "Vocabulary deleted successfully")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Error deleting vocabulary", e)
            }
    }
}