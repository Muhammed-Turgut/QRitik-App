package com.RealizeStudio.qritik.screens

import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerResultScreen(
    navController: NavController,
    imagePath: String? = null,
    qrCodeData: String,
    codeType: String? = null,
    dateTime: String? = null,
    scannerResultScreenViewModel: ScannerResultScreenViewModel = hiltViewModel(),
    saveViewModel: SaveViewModel = hiltViewModel()
) {
    BackHandler { /* prevent going back with hardware button to avoid navigation issues */ }

    val context = LocalContext.current
    val decodedData = Uri.decode(qrCodeData)
    val scrollState = rememberScrollState()

    val savedMsg = stringResource(id = R.string.record_saved_successfully)
    val favoritedMsg = stringResource(id = R.string.added_to_favorites)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(id = R.string.scan_result),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            navController.navigate("AppScreen") {
                                popUpTo("ScannerResult") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(id = R.string.back),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                ),
                modifier = Modifier.border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Scan Detail Card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 2.dp,
                shadowElevation = 4.dp,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f)
                )
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.read_data),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF9818D6),
                        )
                        Text(
                            text = dateTime ?: "",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9818D6).copy(alpha = 0.08f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.url),
                                contentDescription = "",
                                tint = Color(0xFF9818D6),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = getQrTypeDisplayName(type = codeType ?: "METIN"),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Bounded Glassmorphic Data Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = decodedData,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.fillMaxWidth(),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Actions Circle Button Grid Row
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 1.dp,
                border = androidx.compose.foundation.BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.04f)
                )
            ) {
                var isFavorited by remember { mutableStateOf(false) }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Save Action Button
                    ResultActionBtn(
                        painter = painterResource(R.drawable.save_icon),
                        label = stringResource(id = R.string.records)
                    ) {
                        saveViewModel.save(codeType ?: "METIN", qrCodeData, dateTime ?: "", isCreated = false)
                        Toast.makeText(context, savedMsg, Toast.LENGTH_SHORT).show()
                    }

                    // Favorite Action Button
                    ResultActionBtn(
                        painter = rememberVectorPainter(if (isFavorited) Icons.Default.Favorite else Icons.Default.FavoriteBorder),
                        label = stringResource(id = R.string.add_to_favorites)
                    ) {
                        if (!isFavorited) {
                            saveViewModel.save(codeType ?: "METIN", qrCodeData, dateTime ?: "", isCreated = false, isFavorite = true)
                            isFavorited = true
                            Toast.makeText(context, favoritedMsg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    // Open URL Action Button (Conditional)
                    if (scannerResultScreenViewModel.isUrl(decodedData)) {
                        ResultActionBtn(
                            painter = painterResource(R.drawable.open_icon),
                            label = stringResource(id = R.string.open)
                        ) {
                            scannerResultScreenViewModel.openUrl(context, decodedData)
                        }
                    }

                    // Copy Action Button
                    ResultActionBtn(
                        painter = painterResource(R.drawable.copy_icon),
                        label = stringResource(id = R.string.copy)
                    ) {
                        scannerResultScreenViewModel.copyToClipboard(context, decodedData)
                    }

                    // Share Action Button
                    ResultActionBtn(
                        painter = painterResource(R.drawable.share_icon),
                        label = stringResource(id = R.string.share)
                    ) {
                        if (!imagePath.isNullOrEmpty()) {
                            scannerResultScreenViewModel.shareImageFile(context, imagePath)
                        } else {
                            val generatedBmp = scannerResultScreenViewModel.generateQrCode(decodedData)
                            scannerResultScreenViewModel.shareBitmap(context, generatedBmp)
                        }
                    }

                    // Wifi Action Button (Conditional)
                    if (scannerResultScreenViewModel.isWifiQR(decodedData)) {
                        ResultActionBtn(
                            painter = painterResource(R.drawable.wifi_icon),
                            label = stringResource(id = R.string.wifi)
                        ) {
                            scannerResultScreenViewModel.connectToWifi(context, decodedData)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Display Scanned Code Image
            imagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                bitmap?.let {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(20.dp),
                        shape = RoundedCornerShape(24.dp),
                        tonalElevation = 4.dp,
                        shadowElevation = 6.dp,
                        border = androidx.compose.foundation.BorderStroke(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f)
                        )
                    ) {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentDescription = stringResource(id = R.string.qr_code_image),
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ResultActionBtn(
    painter: androidx.compose.ui.graphics.painter.Painter,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(46.dp)
                .clip(CircleShape)
                .background(Color(0xFF9818D6).copy(alpha = 0.05f))
                .border(
                    width = 1.dp,
                    color = Color(0xFF9818D6).copy(alpha = 0.15f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painter,
                contentDescription = label,
                tint = Color(0xFF9818D6),
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF9818D6).copy(alpha = 0.7f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
