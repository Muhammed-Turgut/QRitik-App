package com.RealizeStudio.qritik.screens

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.viewModel.PermissionViewModel
import android.content.Intent
import android.net.Uri
import android.provider.Settings


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    permissionViewModel: PermissionViewModel = viewModel()
) {
    val context = LocalContext.current
    val cameraPermissionGranted by permissionViewModel.cameraPermissionGranted.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }

    // İzin isteme launcher'ı
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // İzin verildi, ViewModel'i güncelle
            permissionViewModel.updateCameraPermission(true)
            Toast.makeText(context, "Kamera izni verildi", Toast.LENGTH_SHORT).show()
        } else {
            // İzin reddedildi - ayarlar dialogunu göster
            showSettingsDialog = true
        }
    }

    // Kamera izni kontrolü
    LaunchedEffect(Unit) {
        permissionViewModel.checkPermissions(PermissionViewModel.REQUIRED_PERMISSIONS)
    }

    // Ayarlar dialogu
    if (showSettingsDialog) {
        AlertDialog(
            onDismissRequest = { showSettingsDialog = false },
            title = {
                Text(
                    text = "Kamera İzni Gerekli",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = "Kamera izni reddedildi. QR kod okuyabilmek için uygulama ayarlarından kamera iznini manuel olarak vermeniz gerekiyor.",
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showSettingsDialog = false
                        // Uygulama ayarlarını aç
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", context.packageName, null)
                        }
                        context.startActivity(intent)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9818D6)
                    )
                ) {
                    Text("Ayarlara Git", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showSettingsDialog = false }
                ) {
                    Text("İptal", color = Color.Gray)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // Üst toolbar
        TopAppBar(
            title = {
                Text(
                    text = "QR/Barkod Okuyucu",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = { navController.popBackStack() }
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.White
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Black
            )
        )

        // Kamera veya izin uyarısı
        if (cameraPermissionGranted) {
            // Kamera kullanımı
            CameraQRScanner(
                onQRCodeDetected = { qrCode ->
                    // QR kod tespit edildiğinde yapılacak işlemler
                    Toast.makeText(context, "QR Kod: $qrCode", Toast.LENGTH_LONG).show()

                    // Burada istediğiniz işlemi yapabilirsiniz:
                    // - Veritabanına kaydetme
                    // - Başka ekrana yönlendirme
                    // - API çağrısı yapma vb.
                },
                onError = { error ->
                    Toast.makeText(context, "Hata: $error", Toast.LENGTH_SHORT).show()
                },
                navController
            )
        } else {
            // İzin uyarısı
            PermissionDeniedContent(
                onRequestPermission = {
                    // Gerçek izin isteme işlemi
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                onOpenSettings = {
                    // Ayarlar dialogunu göster
                    showSettingsDialog = true
                }
            )
        }

        // Alt bilgi
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Black.copy(alpha = 0.8f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "QR kod veya barkodu kare içine getirin",
                color = Color.White,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
fun PermissionDeniedContent(
    onRequestPermission: () -> Unit,
    onOpenSettings: () -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "📷",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = "Kamera İzni Gerekli",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Text(
                    text = "QR kod okuyabilmek için kamera iznine ihtiyacımız var.",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Ana izin butonu
                Button(
                    onClick = onRequestPermission,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF9818D6)
                    )
                ) {
                    Text(
                        text = "İzin Ver",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }

                // Ayarlar butonu
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(
                    onClick = onOpenSettings,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Manuel Ayarlar",
                        color = Color(0xFF9818D6),
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}