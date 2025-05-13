package com.example.torii.viewModel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.torii.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import com.cloudinary.Cloudinary
import com.cloudinary.utils.ObjectUtils
import com.example.torii.model.Comment
import com.google.firebase.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore

class CommunityViewModel : ViewModel() {

    private val _posts = mutableStateOf<List<Post>>(emptyList())
    var posts: State<List<Post>> = _posts

    var newPostText by mutableStateOf("")
    var selectedImageUri by mutableStateOf<Uri?>(null)

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

    val commentsMap = mutableStateMapOf<String, List<Comment>>()

    var commentCountMap by mutableStateOf<Map<String, Int>>(emptyMap())

    val timestamp = System.currentTimeMillis()

    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", "dml7vsbx7",
            "api_key", "115634565249752",
            "api_secret", "sBxxXvCAeXJD7z5tx1hu0DYgzto"
        )
    )

    init {
        loadPosts()
        loadCommentCountForAllPosts()
    }

    fun addPost(context: Context) {
        val user = auth.currentUser ?: return
        val text = newPostText.trim()
        if (text.isBlank()) return

        val postId = UUID.randomUUID().toString()
        val tag = "General"
        val datePosted = SimpleDateFormat("HH:mm dd-MM-yyyy", Locale.getDefault()).format(Date())

        db.collection("users").document(user.uid).get()
            .addOnSuccessListener { snapshot ->
                val userName = snapshot.getString("name") ?: "Unknown"

                if (selectedImageUri != null) {
                    // Upload ảnh lên Cloudinary trước
                    uploadImageToCloudinary(selectedImageUri!!, context,
                        onSuccess = { imageUrl ->
                            savePostToFirestore(postId, user.uid, userName, tag, datePosted, text, imageUrl, timestamp)
                            resetPostForm()
                        },
                        onError = { error ->
                            Log.e("PostUpload", "Upload thất bại: $error")
                        }
                    )
                } else {
                    savePostToFirestore(postId, user.uid, userName, tag, datePosted, text, null, timestamp)
                    resetPostForm()
                }
            }
    }

    private fun resetPostForm() {
        newPostText = ""
        selectedImageUri = null
    }

    private fun savePostToFirestore(
        postId: String,
        userId: String,
        userName: String,
        tag: String,
        datePosted: String,
        text: String,
        imageUrl: String?,
        timestamp: Long
    ) {
        val post = Post(postId, userId, userName, tag, datePosted, text, imageUrl, emptyList(), emptyList(), timestamp)
        db.collection("posts").document(postId).set(post)
    }

    private fun loadPosts() {
        db.collection("posts")
            .orderBy("datePosted", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshots, error ->
                if (error != null || snapshots == null) return@addSnapshotListener
                _posts.value = snapshots.documents.mapNotNull { it.toObject(Post::class.java) }
            }
    }

    fun uploadImageToCloudinary(
        imageUri: Uri,
        context: Context,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val file = File.createTempFile("upload", ".jpg", context.cacheDir)
                inputStream?.use { input ->
                    file.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }

                val uploadResult = cloudinary.uploader().upload(file, ObjectUtils.emptyMap())
                val url = uploadResult["secure_url"] as? String
                if (url != null) {
                    onSuccess(url)
                } else {
                    onError("Không nhận được URL từ Cloudinary")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Lỗi không xác định")
            }
        }
    }

    fun toggleLike(post: Post) {
        val docRef = db.collection("posts").document(post.postId)
        val isLiked = post.likedBy.contains(currentUserId)

        val updatedLikes = if (isLiked) {
            FieldValue.arrayRemove(currentUserId)
        } else {
            FieldValue.arrayUnion(currentUserId)
        }

        docRef.update("likedBy", updatedLikes)
    }

    fun loadComments(postId: String) {
        db.collection("posts")
            .document(postId)
            .collection("comments")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null || snapshot == null) return@addSnapshotListener
                val commentList = snapshot.documents.mapNotNull { it.toObject(Comment::class.java) }
                commentsMap[postId] = commentList
            }
    }

    fun addCommentToPost(postId: String, text: String, onSuccess: () -> Unit) {
        val user = FirebaseAuth.getInstance().currentUser ?: return
        val comment = Comment(
            userId = user.uid,
            userName = user.displayName ?: "Ẩn danh",
            content = text,
            timestamp = System.currentTimeMillis(),
            userAvatar = user.photoUrl?.toString() ?: "" // ✅ Thêm avatar
        )

        db.collection("posts").document(postId)
            .collection("comments")
            .add(comment)
            .addOnSuccessListener {
                onSuccess()
                loadComments(postId)
            }
            .addOnFailureListener {
                Log.e("CommunityViewModel", "Thêm bình luận thất bại: ${it.message}")
            }
    }

    fun loadCommentCountForAllPosts() {
        db.collection("posts")
            .get()
            .addOnSuccessListener { snapshot ->
                snapshot.documents.forEach { doc ->
                    val postId = doc.id
                    db.collection("posts").document(postId).collection("comments")
                        .get()
                        .addOnSuccessListener { commentsSnapshot ->
                            commentCountMap = commentCountMap.toMutableMap().apply {
                                put(postId, commentsSnapshot.size())
                            }
                        }
                }
            }
    }
}

