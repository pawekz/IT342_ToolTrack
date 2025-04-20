package edu.cit.tooltrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import edu.cit.tooltrack.api.LoginRequest
import edu.cit.tooltrack.api.ToolTrackApi
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val toolTrackApi = ToolTrackApi.create()

    private val isLoading = mutableStateOf(false)

    // Custom snackbar states
    private val showSnackbar = mutableStateOf(false)
    private val snackbarMessage = mutableStateOf("")
    private val snackbarType = mutableStateOf(SnackbarType.INFO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Register for Google Sign-In activity result
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    val account = task.getResult(ApiException::class.java)
                    // Signed in successfully
                    showSnackbar("Google Sign In successful", SnackbarType.SUCCESS)
                    navigateToMainActivity()
                } catch (e: ApiException) {
                    showSnackbar("Google Sign In failed: ${e.statusCode}", SnackbarType.ERROR)
                }
            }
        }

        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        setContent {
            ToolTrackTheme {
                LoginScreen(
                    isLoading = isLoading.value,
                    showSnackbar = showSnackbar.value,
                    snackbarMessage = snackbarMessage.value,
                    snackbarType = snackbarType.value,
                    onSnackbarDismiss = { showSnackbar.value = false },
                    onLoginClick = { email, password ->
                        if (validateInputs(email, password)) {
                            loginUser(email, password)
                        }
                    },
                    onGoogleSignInClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    onSignupClick = {
                        startActivity(Intent(this, SignupActivity::class.java))
                    },
                    onForgotPasswordClick = {
                        showSnackbar("Forgot password feature coming soon", SnackbarType.INFO)
                    }
                )
            }
        }
    }

    private fun validateInputs(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            showSnackbar("Please enter your email", SnackbarType.INFO)
            return false
        }

        if (password.isEmpty()) {
            showSnackbar("Please enter your password", SnackbarType.INFO)
            return false
        }

        return true
    }

    private fun showSnackbar(message: String, type: SnackbarType) {
        snackbarMessage.value = message
        snackbarType.value = type
        showSnackbar.value = true
    }

    private fun loginUser(email: String, password: String) {
        val request = LoginRequest(email = email, password = password)

        // Show loading indicator
        isLoading.value = true

        lifecycleScope.launch {
            try {
                val response = toolTrackApi.loginUser(request)
                isLoading.value = false

                if (response.isSuccessful) {
                    val token = response.body()
                    Log.d("API_SUCCESS", "Login successful: JWT token received")

                    // Show success message with green styling
                    showSnackbar("Login successful, redirecting to Dashboard", SnackbarType.SUCCESS)
                    delay(1500) // Brief delay to show success message
                    navigateToMainActivity()
                } else {
                    Log.e("API_ERROR", "Login failed: ${response.code()}")
                    // Show error message with softer styling
                    showSnackbar("Username or Password is incorrect", SnackbarType.ERROR)
                }
            } catch (e: Exception) {
                isLoading.value = false
                Log.e("API_EXCEPTION", "Error during login", e)
                showSnackbar("Error: ${e.message ?: "Unknown error"}", SnackbarType.ERROR)
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

enum class SnackbarType { SUCCESS, ERROR, INFO }

@Composable
fun CustomSnackbar(
    message: String,
    type: SnackbarType,
    onDismiss: () -> Unit
) {
    val backgroundColor = when (type) {
        SnackbarType.SUCCESS -> Color(0xFF4CAF50)  // Green
        SnackbarType.ERROR -> Color(0xFF546E7A)    // Blue-gray (less alarming)
        SnackbarType.INFO -> Color(0xFF2196F3)     // Blue
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            color = backgroundColor,
            shape = RoundedCornerShape(8.dp),
            shadowElevation = 6.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message,
                    color = Color.White,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewLoginScreen() {
    ToolTrackTheme {
        LoginScreen(
            onLoginClick = { _, _ -> },
            onGoogleSignInClick = {},
            onSignupClick = {},
            onForgotPasswordClick = {}
        )
    }
}

@Composable
fun LoginScreen(
    isLoading: Boolean = false,
    showSnackbar: Boolean = false,
    snackbarMessage: String = "",
    snackbarType: SnackbarType = SnackbarType.INFO,
    onSnackbarDismiss: () -> Unit = {},
    onLoginClick: (email: String, password: String) -> Unit = { _, _ -> },
    onGoogleSignInClick: () -> Unit = {},
    onSignupClick: () -> Unit = {},
    onForgotPasswordClick: () -> Unit = {}
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Main content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color(0xFFBFE4E0), Color(0xFF00FFE3))
                    )
                )
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                // Logo
                Spacer(modifier = Modifier.height(32.dp))
                Image(
                    painter = painterResource(id = R.mipmap.tooltrack_logo_foreground),
                    contentDescription = stringResource(id = R.string.app_name),
                    modifier = Modifier.size(180.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Card with White Background
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 0.dp), // Padding to ensure space on sides
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    shape = RoundedCornerShape(
                        topStart = 32.dp,
                        topEnd = 32.dp,
                        bottomStart = 0.dp,
                        bottomEnd = 0.dp
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // App Name
                        Text(
                            text = stringResource(id = R.string.login),
                            fontSize = 28.sp,
                            fontFamily = FontFamily(Font(R.font.inter_bold))
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Email Field
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(id = R.string.email)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF000000),
                                unfocusedBorderColor = Color(0xFF909090)
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_dialog_email),
                                    contentDescription = null,
                                    modifier = Modifier.padding(20.dp),
                                    tint = Color(0xFF909090)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password Field
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(id = R.string.password)) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            shape = RoundedCornerShape(16.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF000000),
                                unfocusedBorderColor = Color(0xFF909090)
                            ),
                            leadingIcon = {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_dialog_lock),
                                    contentDescription = null,
                                    modifier = Modifier.padding(20.dp),
                                    tint = Color(0xFF909090)
                                )
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            ),
                            trailingIcon = {
                                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                    Icon(
                                        painter = painterResource(
                                            id = if (passwordVisible) android.R.drawable.ic_menu_view
                                            else android.R.drawable.ic_menu_view
                                        ),
                                        contentDescription = if (passwordVisible) "Hide password" else "Show password"
                                    )
                                }
                            }
                        )

                        // Forgot Password
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = stringResource(id = R.string.forgot_password),
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.clickable { onForgotPasswordClick() }
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Login Button
                        Button(
                            onClick = { onLoginClick(email, password) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 0.dp)
                                .size(width = 370.dp, height = 47.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF2EA69E),
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(16.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            } else {
                                Text(
                                    text = stringResource(id = R.string.signin),
                                    fontSize = 16.sp,
                                    fontFamily = FontFamily(Font(R.font.inter))
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Google Sign In Button
                        Button(
                            onClick = onGoogleSignInClick,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .padding(horizontal = 0.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.android_neutral_signin_google),
                                contentDescription = stringResource(id = R.string.login_with_google),
                                modifier = Modifier
                                    .height(80.dp)
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        // Sign Up Prompt
                        Row(
                            modifier = Modifier.padding(bottom = 2.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = stringResource(id = R.string.dont_have_account))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = stringResource(id = R.string.signup),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.inter)),
                                modifier = Modifier.clickable { onSignupClick() }
                            )
                        }
                    }
                }
            }
        }

        // Overlay the snackbar at the bottom
        if (showSnackbar) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                CustomSnackbar(
                    message = snackbarMessage,
                    type = snackbarType,
                    onDismiss = onSnackbarDismiss
                )
            }
        }
    }
}