package com.example.torii.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


open class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()


    suspend fun signUpWithEmail(email: String, password: String, name: String): Boolean {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user
            user?.let {
                val userData = hashMapOf(
                    "uid" to user.uid,
                    "name" to name,
                    "email" to email,
                    "authProvider" to "email"
                )
                db.collection("users").document(user.uid).set(userData).await()
            }
            true
        } catch (e: Exception) {
            Log.e("SignUp", "Lỗi khi đăng ký: ${e.message}")
            false
        }
    }

    suspend fun loginWithEmail(email: String, password: String): Boolean {
        return try {
            auth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentUser(): FirebaseUser {
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            return currentUser
        } else {
            throw IllegalStateException("User is not authenticated")
        }
    }

}