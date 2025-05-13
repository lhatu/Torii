package com.example.torii.screens.main

import android.text.format.DateUtils
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.example.torii.ui.theme.Feather
import com.example.torii.viewModel.CommunityViewModel
import com.example.torii.model.Post
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Nunito
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    navController: NavHostController,
    viewModel: CommunityViewModel = viewModel()
) {
    val context = LocalContext.current
    val posts by remember { viewModel.posts }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            viewModel.selectedImageUri = uri
        }
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
            ) {
                CenterAlignedTopAppBar(
                    title = { Text("Community", fontFamily = Feather) },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        titleContentColor = Color.Black
                    )
                )
            }
        },
        bottomBar = { BottomNavigationBar(navController) },
        content = { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                // Post input box
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            OutlinedTextField(
                                value = viewModel.newPostText,
                                onValueChange = { viewModel.newPostText = it },
                                placeholder = { Text("Bạn đang nghĩ gì?", fontFamily = Nunito) },
                                modifier = Modifier.fillMaxWidth(),
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            if (viewModel.selectedImageUri == null) {
                                                launcher.launch("image/*")
                                            }
                                        }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Image,
                                            contentDescription = "Chọn ảnh"
                                        )
                                    }
                                },
                                textStyle = TextStyle(
                                    fontSize = 18.sp,
                                    fontFamily = Nunito
                                ),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center,
                            ) {
                                viewModel.selectedImageUri?.let { uri ->
                                    Image(
                                        painter = rememberAsyncImagePainter(uri),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(8.dp)
                                            .size(400.dp),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    if (viewModel.selectedImageUri != null) {
                                        viewModel.uploadImageToCloudinary(
                                            context = context,
                                            imageUri = viewModel.selectedImageUri!!,
                                            onSuccess = { imageUrl ->
                                                viewModel.addPost(context)
                                            },
                                            onError = { error ->
                                                Toast.makeText(context, "Tải ảnh lỗi: $error", Toast.LENGTH_SHORT).show()
                                            }
                                        )
                                    } else {
                                        viewModel.addPost(context)
                                    }
                                },
                                modifier = Modifier.align(Alignment.End),
                                colors = ButtonDefaults.buttonColors(Color(0xFF43A047))
                            ) {
                                Text("Đăng bài", fontFamily = Feather, fontSize = 16.sp)
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth()  // Make the line fill the width of the parent
                                .padding(vertical = 8.dp),  // Add some vertical padding for spacing
                            thickness = 1.dp, // Line thickness
                            color = Color.LightGray  // Line color
                        )
                    }
                }

                // Post list
                items(posts) { post ->
                    PostItem(post)
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostItem(post: Post, viewModel: CommunityViewModel = viewModel()) {

    val user = FirebaseAuth.getInstance().currentUser
    val currentUserId = user?.uid
    val isLiked = post.likedBy.contains(currentUserId)

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    var newComment by remember { mutableStateOf("") }

    val comments = viewModel.commentsMap[post.postId] ?: emptyList()

    var posterAvatar by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(post.userId) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users")
            .document(post.userId)
            .get()
            .addOnSuccessListener { document ->
                posterAvatar = document.getString("photoUrl") // or "photoUrl" tùy tên field bạn lưu
            }
    }

    if (showSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSheet = false },
            sheetState = sheetState
        ) {
            Column(modifier = Modifier.fillMaxHeight(0.8f)) {
                Text(
                    text = "Comments",
                    fontFamily = Feather,
                    fontSize = 20.sp,
                    modifier = Modifier.padding(12.dp)
                )
                Divider()

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(comments) { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            if (comment.userAvatar.isNotEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(comment.userAvatar),
                                    contentDescription = "Avatar",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "Default Avatar",
                                    modifier = Modifier.size(40.dp),
                                    tint = Color.Gray
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Column {
                                Text(comment.userName, fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro, fontSize = 15.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(comment.content, fontFamily = Nunito, fontSize = 18.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = getRelativeTime(comment.timestamp),
                                    fontSize = 14.sp,
                                    color = Color.Gray,
                                    fontFamily = Nunito
                                )
                            }
                        }
                    }
                }

                Divider()
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(12.dp, bottom = 30.dp)
                        .fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = newComment,
                        onValueChange = { newComment = it },
                        placeholder = { Text("Write your comments...", fontFamily = Nunito) },
                        modifier = Modifier.weight(1f),
                        textStyle = TextStyle(
                            fontSize = 18.sp,
                            fontFamily = Nunito
                        ),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.addCommentToPost(post.postId, newComment) {
                                newComment = ""
                                viewModel.loadComments(post.postId)
                            }
                        },
                        modifier = Modifier.padding(end = 8.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF43A047))
                    ) {
                        Text("Send", fontFamily = Feather, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(12.dp)
            ) {
                if (!posterAvatar.isNullOrEmpty()) {
                    Image(
                        painter = rememberAsyncImagePainter(posterAvatar),
                        contentDescription = "Avatar",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Default Avatar",
                        modifier = Modifier.size(40.dp),
                        tint = Color.Gray
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(text = post.userName, fontFamily = BeVietnamPro, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "${getRelativeTime(post.timestamp)}  •  ${post.tag}",
                        fontFamily = Nunito,
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }

            }
            Text(
                text = post.text,
                fontFamily = Nunito,
                fontSize = 18.sp,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            post.imageUrl?.let { url ->
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(url),
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1f)
                )
            }

            // Reactions Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "${post.likedBy.size} likes", fontSize = 15.sp, fontFamily = Nunito)
                Row {
                    Text( text = "${viewModel.commentCountMap[post.postId] ?: 0} comments", fontSize = 15.sp, fontFamily = Nunito)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = "0 shares", fontSize = 15.sp, fontFamily = Nunito)
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        viewModel.toggleLike(post)
                    },
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = if (isLiked) Color.Red else Color.Black
                    )
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = "Thích",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(" Like", fontFamily = Nunito, fontSize = 16.sp)
                }
                TextButton(
                    onClick = {
                        viewModel.loadComments(post.postId)
                        showSheet = true
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Comment,
                        contentDescription = "Thích",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(" Comment", fontFamily = Nunito, fontSize = 16.sp)
                }
                TextButton(
                    onClick = { /* TODO: handle share */ },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Black)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Share,
                        contentDescription = "Thích",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(" Share", fontFamily = Nunito, fontSize = 16.sp)
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()  // Make the line fill the width of the parent
                    .padding(vertical = 8.dp),  // Add some vertical padding for spacing
                thickness = 1.dp, // Line thickness
                color = Color.LightGray  // Line color
            )
        }
    }
}

fun getRelativeTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    val seconds = diff / 1000
    val minutes = seconds / 60
    val hours = minutes / 60
    val days = hours / 24

    return when {
        seconds < 60 -> "Just now"
        minutes < 60 -> "$minutes minutes ago"
        hours < 24 -> "$hours hours ago"
        days < 7 -> "$days days ago"
        else -> {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            sdf.format(Date(timestamp))
        }
    }
}

