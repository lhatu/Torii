package com.example.torii.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import com.example.torii.auth.GoogleAuthRepository
import com.example.torii.ui.theme.BeVietnamPro
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

@Composable
fun RegisterScreen(navController: NavController, authRepo: AuthRepository, googleAuthRepo: GoogleAuthRepository) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Trạng thái hiển thị lỗi
    var showErrors by remember { mutableStateOf(false) }

    // Biểu thức chính quy kiểm tra email hợp lệ
    val emailPattern = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$"
    )

    // Kiểm tra điều kiện hợp lệ
    val isUsernameValid = name.isNotBlank()
    val isEmailValid = emailPattern.matcher(email).matches()
    val isPasswordValid = password.length >= 8

    // Launcher để nhận kết quả từ Google Sign-In
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = try {
            task.getResult(ApiException::class.java)
        } catch (e: ApiException) {
            Toast.makeText(context, "Lỗi Google Sign-In: ${e.message}", Toast.LENGTH_SHORT).show()
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
        Text(text = "Sign up", fontWeight = FontWeight.Bold, fontFamily = BeVietnamPro, fontSize = 28.sp)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Sign up and let start learning Japanese", fontFamily = BeVietnamPro, color = Color.Gray)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Your name", fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = BeVietnamPro),
            placeholder = { Text("Enter your full name", fontFamily = BeVietnamPro, fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        if (showErrors && !isUsernameValid) {
            Text(
                text = "Tên không được để trống",
                color = MaterialTheme.colorScheme.error,
                fontFamily = BeVietnamPro,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp, top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Email", fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = BeVietnamPro),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = { Text("Enter your email", fontFamily = BeVietnamPro, fontSize = 15.sp) },
            modifier = Modifier.fillMaxWidth()
        )
        if (showErrors && !isEmailValid) {
            Text(
                text = "Email không hợp lệ",
                color = MaterialTheme.colorScheme.error,
                fontFamily = BeVietnamPro,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp, top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Text(text = "Password", fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = BeVietnamPro),
            placeholder = { Text("Enter your password", fontFamily = BeVietnamPro, fontSize = 15.sp) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Hiển thị mật khẩu")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))

        // Ô nhập lại mật khẩu
        Text(text = "Confirm your password", fontFamily = BeVietnamPro, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            singleLine = true,
            placeholder = { Text("Enter your password", fontFamily = BeVietnamPro, fontSize = 15.sp) },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = BeVietnamPro),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = "Hiển thị mật khẩu")
                }
            }
        )
        if (showErrors  && !isPasswordValid) {
            Text(
                text = "Mật khẩu phải có ít nhất 8 ký tự",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = BeVietnamPro,
                modifier = Modifier.padding(start = 8.dp, top = 10.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        Button(
            onClick = {
                showErrors = true // Khi nhấn nút, hiển thị lỗi nếu có
                if (isUsernameValid && isEmailValid && isPasswordValid) {
                    coroutineScope.launch {
                        isLoading = true
                        val success = authRepo.signUpWithEmail(email, password, name)
                        if (success) {
                            isLoading = false
                            Toast.makeText(context, "Đăng ký thành công", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") { popUpTo("register") { inclusive = true } }
                        } else {
                            Toast.makeText(context, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
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
                Text(text = "Đăng ký", fontFamily = BeVietnamPro, fontSize = 16.sp)
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
            onClick = { navController.navigate("login") },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Đã có tài khoản? Đăng nhập ngay", fontFamily = BeVietnamPro)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    val fakeAuthRepo = object : AuthRepository() {
        suspend fun registerUser(email: String, password: String): Boolean {
            return true // Giả lập thành công để Preview không bị lỗi
        }
    }

    RegisterScreen(navController = rememberNavController(), authRepo = fakeAuthRepo, googleAuthRepo = GoogleAuthRepository(LocalContext.current))
}