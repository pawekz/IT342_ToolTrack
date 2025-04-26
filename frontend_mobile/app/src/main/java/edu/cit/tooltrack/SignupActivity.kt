package edu.cit.tooltrack

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import edu.cit.tooltrack.api.RegistrationRequest
import edu.cit.tooltrack.api.ToolTrackApi
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SignupActivity : ComponentActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    private val toolTrackApi = ToolTrackApi.create()

    // Track loading state for UI
    private val isLoading = mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Register for Google Sign-In activity result
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    task.getResult(ApiException::class.java)
                    // Signed in successfully
                    Toast.makeText(this, "Google Sign Up successful", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google Sign Up failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
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
                SignupScreen(
                    isLoading = isLoading.value,
                    onSignupClick = { firstName, lastName, email, password, confirmPassword ->
                        if (validateInputs(firstName, lastName, email, password, confirmPassword)) {
                            registerUser(firstName, lastName, email, password)
                        }
                    },
                    onGoogleSignUpClick = {
                        val signInIntent = googleSignInClient.signInIntent
                        googleSignInLauncher.launch(signInIntent)
                    },
                    onLoginClick = {
                        finish() // Go back to LoginActivity
                    }
                )
            }
        }
    }

    private fun registerUser(firstName: String, lastName: String, email: String, password: String) {
        // Create registration request
        val request = RegistrationRequest(
            first_name = firstName,
            last_name = lastName,
            email = email,
            password_hash = password,
            isGoogle = false,
        )
        //logcat
        Log.d("API_REQUEST", "Sending registration: $request")

        // Show loading indicator
        isLoading.value = true

        // Make API call with timeout
        lifecycleScope.launch {
            try {
                // Set a 30-second timeout for the API call
                val response = withTimeoutOrNull(30000) { // 30 seconds timeout
                    toolTrackApi.registerUser(request)
                }

                if (response == null) {
                    // Timeout occurred
                    isLoading.value = false
                    Log.e("API_ERROR", "Registration request timed out after 30 seconds")
                    Toast.makeText(
                        this@SignupActivity,
                        "Registration timed out. Please try again later.",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("API_SUCCESS", "Registration successful: $responseBody")

                    Toast.makeText(
                        this@SignupActivity,
                        "Successfully Registered, redirecting to Dashboard",
                        Toast.LENGTH_SHORT
                    ).show()

                    lifecycleScope.launch {
                        kotlinx.coroutines.delay(2000) // 2 seconds delay
                        isLoading.value = false
                        navigateToMainActivity()
                    }
                } else {
                    isLoading.value = false
                    val errorBody = response.errorBody()?.string() ?: "Unknown error"
                    val errorMessage = if (errorBody.contains("Registration failed")) {
                        "Registration failed. Please try again with a different email."
                    } else {
                        "Registration failed: $errorBody"
                    }

                    Log.e("API_ERROR", "Registration failed: $errorBody")
                    Toast.makeText(
                        this@SignupActivity,
                        errorMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                isLoading.value = false
                Log.e("API_EXCEPTION", "Error during registration", e)
                Toast.makeText(
                    this@SignupActivity,
                    "Error: ${e.message ?: "Unknown error"}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun validateInputs(firstName: String, lastName: String, email: String, password: String, confirmPassword: String): Boolean {
        if (firstName.isEmpty()) {
            Toast.makeText(this, "Please enter your first name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (lastName.isEmpty()) {
            Toast.makeText(this, "Please enter your last name", Toast.LENGTH_SHORT).show()
            return false
        }

        if (email.trim().lowercase().isEmpty()) {
            Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter a password", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password.length < 8) {
            Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
            return false
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finishAffinity() // Close all activities in the stack
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSignupScreen() {
    ToolTrackTheme {
        SignupScreen(
            isLoading = false,
            onSignupClick = { _, _, _, _, _ -> },
            onGoogleSignUpClick = {},
            onLoginClick = {}
        )
    }
}

@Composable
fun SignupScreen(
    isLoading: Boolean = false,
    onSignupClick: (firstName: String, lastName: String, email: String, password: String, confirmPassword: String) -> Unit,

    onGoogleSignUpClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
    // Background with Linear Gradient
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
            Spacer(modifier = Modifier.height(20.dp))
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
                        text = stringResource(id = R.string.signup),
                        fontSize = 24.sp,
                        fontFamily = FontFamily(Font(R.font.inter_bold))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name Field
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // First Name Field
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(
                                topStart = 14.dp,
                                bottomStart = 14.dp,
                                topEnd = 0.dp,
                                bottomEnd = 0.dp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF000000),
                                unfocusedBorderColor = Color(0xFF909090),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedLabelColor = Color(0xFF2EA69E),
                                unfocusedLabelColor = Color(0xFF909090)
                            ),

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )

                        // Last Name Field
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .height(68.dp),
                            shape = RoundedCornerShape(
                                topStart = 0.dp,
                                bottomStart = 0.dp,
                                topEnd = 14.dp,
                                bottomEnd = 14.dp
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF000000),
                                unfocusedBorderColor = Color(0xFF909090),
                                focusedTextColor = Color.Black,
                                unfocusedTextColor = Color.Black,
                                cursorColor = Color.Black,
                                focusedLabelColor = Color(0xFF2EA69E),
                                unfocusedLabelColor = Color(0xFF909090)
                            ),

                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Next
                            )
                        )
                    }


                    Spacer(modifier = Modifier.height(16.dp))

                    // Email Field
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(stringResource(id = R.string.email)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF000000),
                            unfocusedBorderColor = Color(0xFF909090),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color(0xFF2EA69E),
                            unfocusedLabelColor = Color(0xFF909090)
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
                            imeAction = ImeAction.Next,
                            capitalization = KeyboardCapitalization.None
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
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF000000),
                            unfocusedBorderColor = Color(0xFF909090),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color(0xFF2EA69E),
                            unfocusedLabelColor = Color(0xFF909090)
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
                            imeAction = ImeAction.Next
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Confirm Password Field
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text(stringResource(id = R.string.confirm_password)) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF000000),
                            unfocusedBorderColor = Color(0xFF909090),
                            focusedTextColor = Color.Black,
                            unfocusedTextColor = Color.Black,
                            cursorColor = Color.Black,
                            focusedLabelColor = Color(0xFF2EA69E),
                            unfocusedLabelColor = Color(0xFF909090)
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
                            IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                                Icon(
                                    painter = painterResource(
                                        id = if (confirmPasswordVisible) android.R.drawable.ic_menu_view
                                        else android.R.drawable.ic_menu_view
                                    ),
                                    contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                                )
                            }
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Signup Button with loading indicator
                    Button(
                        onClick = { onSignupClick(firstName, lastName, email, password, confirmPassword) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp)
                            .size(width = 360.dp, height = 47.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF2EA69E),
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        } else {
                            Text(
                                text = stringResource(id = R.string.signup),
                                fontSize = 16.sp,
                                fontFamily = FontFamily(Font(R.font.inter))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Google Sign Up Button
                    Button(
                        onClick = onGoogleSignUpClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .padding(horizontal = 0.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(14.dp),
                        enabled = !isLoading
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.android_neutral_signup_google),
                            contentDescription = "Sign up with Google",
                            modifier = Modifier
                                .height(80.dp)
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // Login Prompt
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = stringResource(id = R.string.already_have_account))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = stringResource(id = R.string.login),
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 16.sp,
                            fontFamily = FontFamily(Font(R.font.inter)),
                            modifier = Modifier.clickable { onLoginClick() }
                        )
                    }
                }
            }
        }
    }
}
}
