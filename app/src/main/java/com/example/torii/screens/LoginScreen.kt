package com.example.torii.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.torii.repository.AuthRepository
import com.example.torii.R
import com.example.torii.auth.GoogleAuthRepository
import com.example.torii.ui.theme.BeVietnamPro
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController, authRepo: AuthRepository, googleAuthRepo: GoogleAuthRepository) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val errorMessage = remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Trạng thái hiển thị lỗi
    var showErrors by remember { mutableStateOf(false) }

    // Kiểm tra chỉ cần nhập, không yêu cầu định dạng
    val isEmailValid = email.isNotBlank()
    val isPasswordValid = password.isNotBlank()

    // Launcher để nhận kết quả từ Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            //Toast.makeText(context, "Lỗi Google Sign-In: ${e.message}", Toast.LENGTH_SHORT).show()
            null
        }
        isLoading = true
        if (account != null && account.idToken != null) {
            // ✅ Dùng LaunchedEffect để gọi Firebase đăng nhập
            coroutineScope.launch {
                try {
                    val success = googleAuthRepo.firebaseAuthWithGoogle(account.idToken!!)
                    if (success) {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    } else {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(context, "Lỗi Firebase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.kaonashi),
                contentDescription = "Facebook Sign-In",
                modifier = Modifier.size(170.dp) // Kích thước logo nhỏ hơn trong vòng tròn
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Welcome Back!", fontWeight = FontWeight.Bold, fontFamily = Feather, fontSize = 30.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Enter the email address you used to register and your password", fontFamily = Nunito, color = Color.Gray, fontSize = 17.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = Nunito),
            placeholder = { Text("Email", fontFamily = Nunito, fontSize = 16.sp, color = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Email,
                    contentDescription = "Email Icon",
                    tint = Color(0xFF1CB0F6)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFFAF6F6), // Nền xám nhạt
                focusedBorderColor = Color(0xFF1CB0F6),
                unfocusedBorderColor = Color.LightGray,
            ),

        )
        if (showErrors && !isEmailValid) {
            isLoading = false
            Text(
                text = "Please enter your email",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = Nunito,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = Nunito),
            placeholder = { Text("Password", fontFamily = Nunito, fontSize = 16.sp, color = Color.Gray) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Outlined.Visibility else Icons.Outlined.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = image,
                        contentDescription = "Toggle Password Visibility",
                        tint = Color(0xFF1CB0F6)
                    )
                }
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Lock,
                    contentDescription = "Password Icon",
                    tint = Color(0xFF1CB0F6)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFFAF6F6), // Nền xám nhạt
                focusedBorderColor = Color(0xFF1CB0F6),
                unfocusedBorderColor = Color.LightGray,
            ),
        )
        if (showErrors && !isPasswordValid) {
            isLoading = false
            Text(
                text = "Please enter your password",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = Nunito,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }
        if (errorMessage.value != null) {
            isLoading = false
            Text(
                text = errorMessage.value!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = Nunito,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }
        TextButton(
            onClick = { navController.navigate("#") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(0.dp),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.Black
            )
        ) {
            Text("Forgot password?", fontFamily = Feather, fontSize = 15.sp)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                showErrors = true // Khi nhấn nút, hiển thị lỗi nếu có
                if (isEmailValid && isPasswordValid) {
                    coroutineScope.launch {
                        isLoading = true
                        val success = authRepo.loginWithEmail(email, password)
                        if (success) {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                navController.navigate("home")
//                                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                errorMessage.value = "Incorrect email or password"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(15.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1CB0F6),
            ),
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp) // Kích thước nhỏ hơn
                )
            } else {
                Text(text = "LOG IN", fontFamily = Feather, fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally),
            colors = ButtonDefaults.textButtonColors(
                contentColor = Color.Black
            )
        ) {
            Text("Don't have an account? ", fontFamily = Feather, fontSize = 15.sp)
            Text("Sign up", fontFamily = Feather, fontSize = 15.sp, color = Color(0xFF1CB0F6))
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Or continue with", fontFamily = Feather, color = Color.Gray, fontSize = 15.sp,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 15.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GoogleSignInButton(
                    onClick = {
                        coroutineScope.launch {
                            googleAuthRepo.signOut()
                            val signInIntent = googleAuthRepo.getSignInIntent()
                            googleSignInLauncher.launch(signInIntent)
                        }
                    }
                )
                FacbookSignInButton(
                    onClick = {
                        coroutineScope.launch {
                            Toast.makeText(context, "The function is under development", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun FacbookSignInButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .width(150.dp)
            .size(45.dp) // Kích thước tổng thể của nút
            .clip(CircleShape) // Bo tròn
            .background(Color.White) // Nền trắng cho vòng tròn
            .border(1.dp, Color.LightGray, CircleShape) // Viền xám nhẹ
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,

        ) {
        Image(
            painter = painterResource(id = R.drawable.facebook),
            contentDescription = "Facebook Sign-In",
            modifier = Modifier.size(30.dp) // Kích thước logo nhỏ hơn trong vòng tròn
        )
        Text(
            text = "FACEBOOK",
            fontFamily = Feather,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 8.dp),
            color = Color(0xFF1CB0F6)
        )
    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {

    Row(
        modifier = Modifier
            .width(150.dp)
            .size(45.dp) // Kích thước tổng thể của nút
            .clip(CircleShape) // Bo tròn
            .background(Color.White) // Nền trắng cho vòng tròn
            .border(1.dp, Color.LightGray, CircleShape) // Viền xám nhẹ
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,

    ) {
        Image(
            painter = painterResource(id = R.drawable.google),
            contentDescription = "Google Sign-In",
            modifier = Modifier.size(30.dp) // Kích thước logo nhỏ hơn trong vòng tròn
        )
        Text(
            text = "GOOGLE",
            fontFamily = Feather,
            fontSize = 15.sp,
            modifier = Modifier.padding(start = 8.dp),
            color = Color(0xFF1CB0F6)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    val fakeAuthRepo = object : AuthRepository() {
        suspend fun registerUser(email: String, password: String): Boolean {
            return true // Giả lập thành công để Preview không bị lỗi
        }
    }

    LoginScreen(
        navController = rememberNavController(),
        authRepo = fakeAuthRepo,
        googleAuthRepo = GoogleAuthRepository(LocalContext.current)
    )
}
