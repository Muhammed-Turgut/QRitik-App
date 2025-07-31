package com.RealizeStudio.qritik.screens

import android.R.id.shareText
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScannerResultScreen(
    navController: NavController,
    imagePath: String?=null,
    qrCodeData: String,
    codeType: String? = null,
    dateTime: String? = null,
    scannerResultScreenViewModel: ScannerResultScreenViewModel = viewModel(),
    saveViewModel: SaveViewModel
) {

    BackHandler {/*telefonun  geri tuşu ile önceki ekrana dömeyi engler.*/}
    val context = LocalContext.current
    val decodedData = Uri.decode(qrCodeData)

    android.util.Log.d("QRDebug", "Raw QR Data: '$qrCodeData'")
    android.util.Log.d("QRDebug", "Decoded QR Data: '$decodedData'")
    android.util.Log.d("QRDebug", "Is WiFi QR: ${scannerResultScreenViewModel.isWifiQR(decodedData)}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Üst toolbar
        TopAppBar(modifier = Modifier.shadow(2.dp, shape = RectangleShape, ambientColor = Color.Gray),
            title = {
                Text(
                    text = "Tarama Sonucu",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            navigationIcon = {
                IconButton(
                    onClick = {navController.navigate("AppScreen") {
                        popUpTo("ScannerResult") {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }}
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Geri",
                        tint = Color.Black
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White
            )
        )

        // İçerik
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, bottom = 8.dp, top = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // Sonuç kartı
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(modifier = Modifier.padding(start = 16.dp,end = 16.dp)) {
                    Text(
                        text = "Okunan Veri:",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center) {

                        Image(painter = painterResource(R.drawable.url),
                            contentDescription = "",
                            modifier = Modifier.size(24.dp))

                        Column(modifier = Modifier.fillMaxWidth().padding(start = 8.dp)) {
                            Text(
                                text = "$codeType",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                            )
                            Text(
                                text = "$dateTime",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.Gray,
                            )
                        }
                    }

                    Text(
                        text = decodedData,
                        fontSize = 16.sp,
                        color = Color.Black,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp), // opsiyonel iç boşluk
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top
            ) {
                Image(
                    painter = painterResource(R.drawable.save_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(42.dp, 60.dp)
                        .clickable( indication = null, // Ripple'ı kapatır
                            interactionSource = remember { MutableInteractionSource() }) {
                            saveViewModel.save("${codeType}","${qrCodeData}","${dateTime}")
                        }
                )

                if (scannerResultScreenViewModel.isUrl(decodedData)) {
                    Image(
                        painter = painterResource(R.drawable.open_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(42.dp, 60.dp)
                            .clickable( indication = null, // Ripple'ı kapatır
                                interactionSource = remember { MutableInteractionSource() }) {
                                scannerResultScreenViewModel.openUrl(context, decodedData)
                            }
                    )
                }

                Image(
                    painter = painterResource(R.drawable.copy_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(42.dp, 60.dp)
                        .clickable ( indication = null, // Ripple'ı kapatır
                            interactionSource = remember { MutableInteractionSource() }){
                            scannerResultScreenViewModel.copyToClipboard(context, decodedData)
                        }
                )

                Image(
                    painter = painterResource(R.drawable.share_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(42.dp, 60.dp)
                        .clickable( indication = null, // Ripple'ı kapatır
                            interactionSource = remember { MutableInteractionSource() }) {
                            scannerResultScreenViewModel.shareText(context, decodedData)
                        }
                )
                // WiFi QR kodu için bağlan butonu
                if (scannerResultScreenViewModel.isWifiQR(decodedData)) {
                    Image(
                        painter = painterResource(R.drawable.wifi_icon), // WiFi ikonu ekleyin
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp, 60.dp)
                            .clickable( indication = null,
                                interactionSource = remember { MutableInteractionSource() }) {
                                println("${decodedData}")
                                scannerResultScreenViewModel.connectToWifi(context, decodedData)
                            }
                    )
                }
            }


            // Resmi göster
            imagePath?.let { path ->
                val bitmap = BitmapFactory.decodeFile(path)
                bitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Kod Resmi",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f) // Kare yapar
                            .padding(16.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFE0E0E0), // Açık gri (beyazla uyumlu)
                                shape = RoundedCornerShape(4.dp)
                            )
                            .clip(RoundedCornerShape(4.dp)) // Kenarları kes
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(4.dp),
                                ambientColor = Color(0xFFDBDADA),
                                spotColor = Color.Gray
                            ),
                        contentScale = ContentScale.Crop
                    )
                }
            }


        }
    }
}


