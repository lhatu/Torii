package com.example.torii.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
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
import com.example.torii.ui.theme.Feather
import com.example.torii.ui.theme.Nunito
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.regex.Pattern

@OptIn(ExperimentalMaterial3Api::class)
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
        Text(text = "Sign up", fontWeight = FontWeight.Bold, fontFamily = Feather, fontSize = 30.sp)

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Sign up and let start learning Japanese", fontFamily = Nunito, color = Color.Gray, fontSize = 17.sp)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = Nunito),
            placeholder = { Text("Full name", fontFamily = Nunito, fontSize = 16.sp, color = Color.Gray) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Person,
                    contentDescription = "Name Icon",
                    tint = Color(0xFF1CB0F6)
                )
            },
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = Color(0xFFFAF6F6), // Nền xám nhạt
                focusedBorderColor = Color(0xFF1CB0F6),
                unfocusedBorderColor = Color.LightGray,
            ),
        )
        if (showErrors && !isUsernameValid) {
            Text(
                text = "Tên không được để trống",
                color = MaterialTheme.colorScheme.error,
                fontFamily = Nunito,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = Nunito),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            placeholder = { Text("Email", fontFamily = Nunito, fontSize = 16.sp, color = Color.Gray) },
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
            Text(
                text = "Email không hợp lệ",
                color = MaterialTheme.colorScheme.error,
                fontFamily = Nunito,
                fontSize = 14.sp,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            singleLine = true,
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = Nunito),
            placeholder = { Text("Password", fontFamily = Nunito, fontSize = 16.sp, color = Color.Gray) },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(25.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            singleLine = true,
            placeholder = { Text("Re-enter Password", fontFamily = Nunito, fontSize = 16.sp, color = Color.Gray) },
            textStyle = TextStyle(fontSize = 16.sp, fontFamily = Nunito),
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
        if (showErrors  && !isPasswordValid) {
            Text(
                text = "Mật khẩu phải có ít nhất 8 ký tự",
                color = MaterialTheme.colorScheme.error,
                fontSize = 14.sp,
                fontFamily = Nunito,
                modifier = Modifier.padding(start = 8.dp, top = 8.dp)
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
                            Toast.makeText(context, "Sign up successful", Toast.LENGTH_SHORT).show()
                            navController.navigate("home") { popUpTo("register") { inclusive = true } }
                        } else {
                            //Toast.makeText(context, "Đăng ký thất bại", Toast.LENGTH_SHORT).show()
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
                Text(text = "SIGN UP", fontFamily = Feather, fontSize = 16.sp)
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(
                onClick = { navController.navigate("login") },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally),
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Black
                )
            ) {
                Text("Already have an account? ", fontFamily = Feather, fontSize = 15.sp)
                Text("Sign in", fontFamily = Feather, fontSize = 15.sp, color = Color(0xFF1CB0F6))
            }

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