package com.RealizeStudio.qritik.screens

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.ui.theme.Secondary
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix

@Composable
fun CreateScreen(viewModel: ScannerResultScreenViewModel = viewModel(),saveViewModel: SaveViewModel){
    var text = remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val selectQRType = remember { mutableStateOf(-1) }
    val selectQRTypeString = remember { mutableStateOf("") }

    when(selectQRType.value){
        0 ->  selectQRTypeString.value ="METIN"
        1 -> selectQRTypeString.value = "URL"
        2 -> selectQRTypeString.value = "E_POSTA"
        3 -> selectQRTypeString.value = "TELEFON"
        4 -> selectQRTypeString.value = "SMS"
        5 -> selectQRTypeString.value = "WIFI"
        6 -> selectQRTypeString.value = "Ürün"
        else -> selectQRTypeString.value =  "BILINMEYEN"
    }

    val selectedIndex = remember { mutableIntStateOf(-1) } // Hiçbiri seçilmemişse -1


    Column(modifier = Modifier
        .fillMaxSize()
        .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally) {

        Text(text = "QR Oluştur",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Secondary,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 16.dp),
            textAlign = TextAlign.Start)


        Spacer(modifier = Modifier.padding(top = 16.dp))


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            icons.forEachIndexed { index, iconResId ->
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = if (selectQRType.value == index) Color(0xFFFFDAD9) else Color.Transparent,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            width = if (selectQRType.value == index) 2.dp else 1.dp,
                            color = if (selectQRType.value == index) Color(0xFFFF5151) else Color.LightGray,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable { selectQRType.value = index },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(iconResId),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }


        QRCustomTextField(text)

        QRConverterButton(onClick = {
            qrBitmap = viewModel.generateQrCode(text.value)
        })

        Spacer(modifier = Modifier.height(16.dp))

        qrBitmap?.let {
            Column(modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){

                Image(bitmap = it.asImageBitmap(), contentDescription = "QR Code")

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp), // opsiyonel iç boşluk
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(R.drawable.save_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(end = 10.dp)
                            .size(42.dp, 60.dp)
                            .clickable( indication = null, // Ripple'ı kapatır
                                interactionSource = remember { MutableInteractionSource() }) {
                                saveViewModel.save("${selectQRTypeString.value}","${text.value}","${viewModel.getCurrentDateTime()}")
                            }
                    )

                    if (viewModel.isUrl(text.value)) {
                        Image(
                            painter = painterResource(R.drawable.open_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(42.dp, 60.dp)
                                .clickable( indication = null, // Ripple'ı kapatır
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    viewModel.openUrl(context, text.value)
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
                                viewModel.copyToClipboard(context, text.value)
                            }
                    )

                    Image(
                        painter = painterResource(R.drawable.share_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .size(42.dp, 60.dp)
                            .padding(end = 10.dp)
                            .clickable( indication = null, // Ripple'ı kapatır
                                interactionSource = remember { MutableInteractionSource() }) {
                                viewModel.shareText(context, text.value)
                            }
                    )
                    // WiFi QR kodu için bağlan butonu
                    if (viewModel.isWifiQR(text.value)) {
                        Image(
                            painter = painterResource(R.drawable.wifi_icon), // WiFi ikonu ekleyin
                            contentDescription = null,
                            modifier = Modifier
                                .size(42.dp, 60.dp)
                                .clickable( indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    Toast.makeText(context, "WiFi butonu çalışıyor!", Toast.LENGTH_SHORT).show()
                                    viewModel.connectToWifi(context, text.value)
                                }
                        )
                    }
                }
            }

        }


    }
}

@Composable
fun QRCustomTextField(textState: MutableState<String>) {

    TextField(
        value = textState.value,
        onValueChange = { textState.value = it },
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
        placeholder = {
            Text(
                text = "Lütfen linki buraya girin...",
                color = Color.Gray,
                fontSize = 18.sp
            )
        },
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Color(0xFFFF5151),
                shape = RoundedCornerShape(12.dp) // ✔️ Kenarları yuvarlat
            ),
        shape = RoundedCornerShape(4.dp), // ✔️ İç şekli de yuvarlat
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent,
            focusedTextColor = Color.Black,
            focusedIndicatorColor = Color.Transparent, //En altaki çizgi
            unfocusedIndicatorColor = Color.Transparent //En altaki çizgi

        )
    )
}

@Composable
fun QRConverterButton(onClick: () -> Unit) {


    androidx.compose.material3.Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 12.dp)
            .border(
                width = 1.dp,
                color = Secondary,
                shape = RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color.Transparent, // ✔️ İç kısmı şeffaf
            contentColor = Secondary,         // ✔️ Yazı rengi çerçeveyle uyumlu
            disabledContainerColor = Color.Transparent,
            disabledContentColor = Color.Gray
        )
    ) {
        Text(text = "QR Dönüştür", fontSize = 20.sp)
    }
}

val icons = listOf(
    R.drawable.row_user_icon,
    R.drawable.row_product_icon,
    R.drawable.row_url_icon,
    R.drawable.row_txt_icon,
    R.drawable.row_wifi_icon,
    R.drawable.row_email_icon,
    R.drawable.row_phone_icon
)
