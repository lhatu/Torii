package com.example.torii.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SearchViewModel (application: Application) : AndroidViewModel(application) {

    private val _searchHistory = MutableStateFlow<List<String>>(emptyList())
    val searchHistory: StateFlow<List<String>> = _searchHistory

    init {
        loadSearchHistory()
    }

    fun loadSearchHistory() {
        val currentUser = FirebaseAuth.getInstance().currentUser ?: return
        val db = Firebase.firestore

        db.collection("users")
            .document(currentUser.uid)
            .collection("searchHistory")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { result ->
                val history = result.documents.mapNotNull { it.getString("query") }
                _searchHistory.value = history
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Failed to load history", e)
            }
    }

    fun clearSearchHistory(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()

            db.collection("users")
                .document(userId)
                .collection("searchHistory")
                .get()
                .addOnSuccessListener { snapshot ->
                    val batch = db.batch()
                    snapshot.documents.forEach { doc ->
                        batch.delete(doc.reference)
                    }
                    batch.commit().addOnSuccessListener {
                        _searchHistory.value = emptyList()
                    }.addOnFailureListener { e ->
                        Log.e("Firestore", "Error clearing search history", e)
                    }
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error getting search history", e)
                }
        }
    }


    fun saveSearchHistory(userId: String, query: String) {
        val db = Firebase.firestore
        val historyItem = hashMapOf(
            "query" to query,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("users")
            .document(userId)
            .collection("searchHistory")
            .add(historyItem)
            .addOnSuccessListener {
                Log.d("Firestore", "Search saved")
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error saving search", e)
            }
    }
}