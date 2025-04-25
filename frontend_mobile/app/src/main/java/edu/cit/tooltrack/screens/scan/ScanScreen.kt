package edu.cit.tooltrack.screens.scan

import android.Manifest
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScanner
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning
import edu.cit.tooltrack.ui.theme.ToolTrackTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun ScanScreen(navController: NavHostController) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var scanResult by remember { mutableStateOf<String?>(null) }
    var hasPermission by remember { mutableStateOf(false) }
    var isScanning by remember { mutableStateOf(false) }

    // Setup scanner options
    val options = remember {
        GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .enableAutoZoom()
            .build()
    }

    // Get scanner
    val scanner = remember { GmsBarcodeScanning.getClient(context, options) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (isGranted && !isScanning && scanResult == null) {
            // Start scanning automatically when permission is granted
            startScanning(scope, context, scanner, navController) { result ->
                scanResult = result
                isScanning = false
            }
            isScanning = true
        } else if (!isGranted) {
            Toast.makeText(
                context,
                "Camera permission is required to scan QR codes",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.QrCode,
                contentDescription = "QR Code Scanner",
                modifier = Modifier.height(100.dp),
                tint = Color(0xFF2EA69E)
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                scanResult != null -> {
                    Text(
                        text = "Scanned Result:",
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = scanResult!!,
                        color = Color.Black,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                isScanning -> {
                    Text(
                        text = "Scanner is active...",
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Position QR code in front of camera",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
                else -> {
                    Text(
                        text = "Preparing Scanner...",
                        fontSize = 24.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Scanner will start automatically",
                        color = Color.Gray,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Only show "Scan Again" button after successful scan
            if (scanResult != null) {
                Button(
                    onClick = {
                        if (hasPermission && !isScanning) {
                            startScanning(scope, context, scanner, navController) { result ->
                                scanResult = result
                                isScanning = false
                            }
                            isScanning = true
                        } else if (!hasPermission) {
                            permissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2EA69E),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.padding(horizontal = 32.dp)
                ) {
                    Text(
                        text = "Scan Again",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        }
    }
}

// Moved outside the ScanScreen composable as a top-level function
fun startScanning(
    scope: CoroutineScope,
    context: Context,
    scanner: GmsBarcodeScanner,
    navController: NavHostController,
    onResult: (String) -> Unit
) {
    scope.launch {
        try {
            scanner.startScan()
                .addOnSuccessListener { barcode ->
                    barcode.rawValue?.let { value ->
                        onResult(value)
                    }
                }
                .addOnFailureListener { e ->
                    if (e.message?.contains("16") == true) {
                        // User canceled the scan - navigate to home
                        Toast.makeText(context, "Scan canceled", Toast.LENGTH_SHORT).show()
                        navController.navigate("home") {
                            // Pop up to home and clear back stack
                            popUpTo("home") { inclusive = true }
                        }
                    } else {
                        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnCanceledListener {
                    Toast.makeText(context, "Scan canceled", Toast.LENGTH_SHORT).show()
                    // Navigate to home when canceled
                    navController.navigate("home") {
                        // Pop up to home and clear back stack
                        popUpTo("home") { inclusive = true }
                    }
                }
        } catch (e: Exception) {
            Toast.makeText(context, "Error launching scanner: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

// Moved to top-level as required for Preview
@Preview(showBackground = true)
@Composable
fun PreviewScanScreen() {
    ToolTrackTheme(dynamicColor = false, darkTheme = false) {
        val previewNavController = rememberNavController()
        ScanScreen(previewNavController)
    }
}