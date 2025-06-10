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
import com.example.torii.notification.showCommentNotification
import com.example.torii.notification.showLikeNotification
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

    var isLoading by mutableStateOf(false)

    private val _userPosts = mutableStateOf<List<Post>>(emptyList())
    val userPosts: State<List<Post>> = _userPosts

    private val cloudinary = Cloudinary(
        ObjectUtils.asMap(
            "cloud_name", "",
            "api_key", "",
            "api_secret", ""
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
        isLoading = true
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
        isLoading = false
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

    fun toggleLike(post: Post, context: Context) {
        val docRef = db.collection("posts").document(post.postId)
        val isLiked = post.likedBy.contains(currentUserId)

        val updatedLikes = if (isLiked) {
            FieldValue.arrayRemove(currentUserId)
        } else {
            showLikeNotification(context, post.userName)
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

    fun addCommentToPost(postId: String, text: String, context: Context, onSuccess: () -> Unit) {
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

                // ✅ Hiển thị thông báo
                db.collection("posts").document(postId).get().addOnSuccessListener { snapshot ->
                    val ownerName = snapshot.getString("userName") ?: "người dùng"
                    showCommentNotification(context, ownerName, text)
                }
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

    fun deletePost(post: Post) {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading = true
            try {
                post.imageUrl?.let { imageUrl ->
                    val publicId = extractPublicIdFromUrl(imageUrl)
                    if (publicId != null) {
                        cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap())
                    }
                }

                db.collection("posts").document(post.postId).delete()
            } catch (e: Exception) {
                Log.e("CommunityViewModel", "Xoá bài viết thất bại: ${e.message}")
            }
            isLoading = false
        }
    }

    fun extractPublicIdFromUrl(url: String): String? {
        val lastPart = url.substringAfterLast('/')

        return lastPart.substringBeforeLast('.')
    }

    fun editPost(post: Post, updatedText: String) {
        val postRef = db.collection("posts").document(post.postId)
        postRef.update("text", updatedText)
            .addOnSuccessListener {
                Log.d("EditPost", "Post updated successfully")
            }
            .addOnFailureListener { e ->
                Log.e("EditPost", "Failed to update post", e)
            }
    }

    fun loadPostsByUser(userId: String) {
        db.collection("posts")
            .whereEqualTo("userId", userId)
            .orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("CommunityVM", "Lỗi khi lấy bài viết người dùng", e)
                    return@addSnapshotListener
                }
                val posts = snapshot?.documents?.mapNotNull { it.toObject(Post::class.java) } ?: emptyList()
                _userPosts.value = posts
            }
    }

    fun updateUserName(newName: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId)
            .update("name", newName)
            .addOnSuccessListener {
                // Cập nhật tên trong bài viết
                db.collection("posts")
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener { snapshot ->
                        for (doc in snapshot.documents) {
                            doc.reference.update("userName", newName)
                        }
                    }
            }
    }
}

