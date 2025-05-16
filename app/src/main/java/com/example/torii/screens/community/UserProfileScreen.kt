package com.example.torii.screens.community

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Repeat
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.torii.card.WelcomeCard
import com.example.torii.model.Post
import com.example.torii.screens.main.EditPostDialog
import com.example.torii.screens.main.getRelativeTime
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.example.torii.viewModel.CommunityViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UserProfileScreen(navController: NavController, viewModel: CommunityViewModel = viewModel()) {
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""
    val userName = currentUser?.displayName ?: "Null"
    val userEmail = currentUser?.email ?: "null"
    val userImageUrl = currentUser?.photoUrl?.toString() ?: ""
    val posts = viewModel.userPosts

    var showDialog by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(userName) }

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadPostsByUser(userId)
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Đổi tên", fontFamily = BeVietnamPro, fontSize = 22.sp, fontWeight = Bold) },
            text = {
                OutlinedTextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Tên mới", fontFamily = Nunito, fontSize = 14.sp) },
                    textStyle = TextStyle(
                        fontSize = 18.sp,
                        fontFamily = BeVietnamPro
                    ),
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updateUserName(newName)
                    showDialog = false
                }) {
                    Text("Lưu", fontFamily = Nunito, color = Color.Black, fontSize = 16.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Hủy", fontFamily = Nunito, color = Color.Black, fontSize = 16.sp)
                }
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // Header người dùng


        LazyColumn(modifier = Modifier.fillMaxSize()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = userImageUrl,
                        contentDescription = "Ảnh đại diện",
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = userName,
                                fontFamily = BeVietnamPro,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Sửa tên",
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickable { showDialog = true }
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = userEmail,
                            fontFamily = BeVietnamPro,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                val streak = remember { (1..50).random() }         // Ngẫu nhiên từ 1 đến 50 ngày
                val xp = remember { (100..1000).random() }         // Ngẫu nhiên từ 100 đến 1000 XP
                val rank = "Silver"

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Column() {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("🔥", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$streak",
                                fontFamily = Feather,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "Day streak",
                                fontFamily = Nunito,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Column() {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("⭐", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$xp",
                                fontFamily = Feather,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "Total XP",
                                fontFamily = Nunito,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }

                    Column() {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("\uD83D\uDEE1\uFE0F", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$rank",
                                fontFamily = Feather,
                                fontSize = 20.sp,
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row {
                            Text(
                                text = "Rank",
                                fontFamily = Nunito,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Divider()
            }

            items(posts.value) { post ->
                PostCard(post)
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostCard(post: Post, viewModel: CommunityViewModel = viewModel()) {

    val user = FirebaseAuth.getInstance().currentUser
    val currentUserId = user?.uid
    val isLiked = post.likedBy.contains(currentUserId)

    val context = LocalContext.current

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showSheet by remember { mutableStateOf(false) }

    var newComment by remember { mutableStateOf("") }

    val comments = viewModel.commentsMap[post.postId] ?: emptyList()

    var posterAvatar by remember { mutableStateOf<String?>(null) }

    var showDeleteDialog by remember { mutableStateOf(false) }

    var showEditDialog by remember { mutableStateOf(false) }

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
                            viewModel.addCommentToPost(post.postId, newComment, context) {
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            text = getRelativeTime(post.timestamp),
                            fontFamily = Nunito,
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                }
                if (currentUserId == post.userId) {
                    var expanded by remember { mutableStateOf(false) }

                    Box(modifier = Modifier.wrapContentSize(Alignment.TopEnd)) {
                        IconButton(onClick = { expanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Tùy chọn")
                        }

                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                            modifier = Modifier.background(Color.White)
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit Post", fontFamily = BeVietnamPro, fontWeight = Bold) },
                                onClick = {
                                    expanded = false
                                    showEditDialog = true
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Delete Post", color = Color.Red, fontFamily = BeVietnamPro, fontWeight = Bold) },
                                onClick = {
                                    expanded = false
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }
                    if (showDeleteDialog) {

                        AlertDialog(
                            onDismissRequest = { showDeleteDialog = false },
                            title = { Text("Delete this post?", fontFamily = BeVietnamPro, fontSize = 22.sp, fontWeight = Bold) },
                            text = { Text("Are you sure you want to delete this post? If you delete it, you will not be able to view it again.",
                                fontFamily = Nunito, fontSize = 17.sp, lineHeight = 25.sp) },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showDeleteDialog = false
                                        viewModel.deletePost(post)
                                    }
                                ) {
                                    Text("Delete", color = Color.Red, fontFamily = Nunito, fontSize = 16.sp)
                                }
                            },
                            dismissButton = {
                                TextButton(onClick = { showDeleteDialog = false }) {
                                    Text("Cancel", fontFamily = Nunito, color = Color.Gray, fontSize = 16.sp)
                                }
                            }
                        )
                    }
                    if (showEditDialog) {
                        EditPostDialog(
                            initialText = post.text,
                            onDismiss = { showEditDialog = false },
                            onConfirm = { updatedText ->
                                viewModel.editPost(post, updatedText)
                                showEditDialog = false
                            }
                        )
                    }

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
                        viewModel.toggleLike(post, context)
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
                        imageVector = Icons.Outlined.Repeat,
                        contentDescription = "Thích",
                        modifier = Modifier.size(25.dp)
                    )
                    Text(" Repost", fontFamily = Nunito, fontSize = 16.sp)
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




