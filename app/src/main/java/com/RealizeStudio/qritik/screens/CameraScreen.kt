package com.RealizeStudio.qritik.screens

import android.R.attr.bitmap
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Media
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.viewModel.CameraViewModel
import com.RealizeStudio.qritik.viewModel.PermissionViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    navController: NavController,
    permissionViewModel: PermissionViewModel = hiltViewModel(),
    cameraViewModel: CameraViewModel = hiltViewModel()
) {
    
    val context = LocalContext.current
    val cameraPermissionGranted by permissionViewModel.cameraPermissionGranted.collectAsState()
    var showSettingsDialog by remember { mutableStateOf(false) }
    var lensFacing by remember { mutableStateOf(CameraSelector.LENS_FACING_BACK) }

    // Kamera ve flash durumu
    var cameraInstance: Camera? by remember { mutableStateOf(null) }
    var isFlashOn by remember { mutableStateOf(false) }





    val selectedImageUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri.value = uri
        uri?.let {
            cameraViewModel.analyzeImageFromUri(
                context = context,
                imageUri = it,
                onQRCodeDetected = { qrData, imagePath, codeType, dateTime ->
                    navController.navigate(
                        "ScannerResult/${Uri.encode(qrData)}/${Uri.encode(imagePath)}/${Uri.encode(codeType)}/${Uri.encode(dateTime)}"
                    )
                },
                onError = { error ->
                    Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                }
            )
        }
    }



    // İzin isteme launcher'ı
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionViewModel.updateCameraPermission(true)
            Toast.makeText(context, "Kamera izni verildi", Toast.LENGTH_SHORT).show()
        } else {
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
        modifier = Modifier.fillMaxSize()
    ) {
        if (cameraPermissionGranted) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .padding(horizontal = 16.dp)
            ) {
                // Geri butonu
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(top = 44.dp)
                        .align(Alignment.Start)
                        .clickable { navController.navigate("AppScreen") }
                        .size(32.dp)
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Kamera alanı - Key ile yeniden başlatma
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    CameraQRScanner(
                        onQRCodeDetected = { qrCode ->
                            // QR kod bulunduğunda herhangi bir ek işlem yapma
                            // Navigasyon CameraQRScanner içinde yapılıyor
                        },
                        onError = { error ->
                            Toast.makeText(context, error, Toast.LENGTH_LONG).show()
                        },
                        navController = navController,
                        onCameraReady = { camera ->
                            cameraInstance = camera
                        },
                        lensFacing = lensFacing,
                        imageUri = selectedImageUri.value
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Alt butonlar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 92.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Flash butonu
                    IconButton(
                        onClick = {
                            cameraInstance?.let { camera ->
                                try {
                                    isFlashOn = !isFlashOn
                                    cameraViewModel.toggleFlash(camera, isFlashOn)
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Flash kontrolü başarısız: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Toast.makeText(context, "Kamera henüz hazır değil", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (isFlashOn) R.drawable.flash_on_icon else R.drawable.flash_off_icon
                            ),
                            contentDescription = if (isFlashOn) "Flash Kapat" else "Flash Aç",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }


                    // Galeri butonu
                    IconButton(
                        onClick = {

                            imagePickerLauncher.launch("image/*")


                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.galleriy),
                            contentDescription = "Galeri Aç",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        } else {
            // Kamera izni verilmemişse
            PermissionDeniedContent(
                onRequestPermission = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                },
                onOpenSettings = {
                    showSettingsDialog = true
                }
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