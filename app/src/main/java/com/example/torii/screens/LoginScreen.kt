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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
                        Toast.makeText(context, "Đăng nhập Google thành công", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        withContext(Dispatchers.Main) {
                            isLoading = false
                        }
                        Toast.makeText(context, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show()
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
        Text(text = "Welcome Back!", fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro, fontSize = 28.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Enter the email address you used to register and your password", fontFamily = BeVietnamPro, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Email", fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = BeVietnamPro),
            placeholder = { Text("Enter your email", fontFamily = BeVietnamPro, fontSize = 15.sp) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        if (showErrors && !isEmailValid) {
            isLoading = false
            Text(
                text = "Vui lòng nhập email",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = BeVietnamPro,
                modifier = Modifier.padding(start = 8.dp, top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Password", fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = BeVietnamPro),
            placeholder = { Text("Enter your password", fontFamily = BeVietnamPro, fontSize = 15.sp) },
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Toggle Password Visibility")
                }
            }
        )
        if (showErrors && !isPasswordValid) {
            isLoading = false
            Text(
                text = "Vui lòng nhập mật khẩu",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = BeVietnamPro,
                modifier = Modifier.padding(start = 8.dp, top = 10.dp)
            )
        }
        if (errorMessage.value != null) {
            isLoading = false
            Text(
                text = errorMessage.value!!,
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = BeVietnamPro,
                modifier = Modifier.padding(start = 8.dp, top = 10.dp)
            )
        }
        TextButton(
            onClick = { navController.navigate("#") },
            modifier = Modifier
                .align(Alignment.End)
                .padding(0.dp)
        ) {
            Text("Forgot password?", fontFamily = BeVietnamPro)
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                                Toast.makeText(context, "Đăng nhập thành công", Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                isLoading = false
                                errorMessage.value = "Sai email hoặc mật khẩu"
                            }
                        }
                    }
                }
            },
            modifier = Modifier
                .height(50.dp)
                .fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp) // Kích thước nhỏ hơn
                )
            } else {
                Text(text = "Đăng nhập", fontFamily = BeVietnamPro, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Or continue with", fontFamily = BeVietnamPro, color = Color.Gray,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 10.dp)
                    .align(Alignment.CenterHorizontally)
            )
            GoogleSignInButton(
                onClick = {
                    coroutineScope.launch {
                        googleAuthRepo.signOut()
                        val signInIntent = googleAuthRepo.getSignInIntent()
                        googleSignInLauncher.launch(signInIntent)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextButton(
            onClick = { navController.navigate("register") },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
        ) {
            Text("Chưa có tài khoản? Đăng ký ngay", fontFamily = BeVietnamPro)
        }

    }
}

@Composable
fun GoogleSignInButton(onClick: () -> Unit) {

    Box(
        modifier = Modifier
            .size(45.dp) // Kích thước tổng thể của nút
            .clip(CircleShape) // Bo tròn
            .background(Color.White) // Nền trắng cho vòng tròn
            .border(1.dp, Color.Gray, CircleShape) // Viền xám nhẹ
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.google),
            contentDescription = "Google Sign-In",
            modifier = Modifier.size(30.dp) // Kích thước logo nhỏ hơn trong vòng tròn
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
