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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.ui.ads.BannerAdView
import com.RealizeStudio.qritik.ui.theme.Secondary
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel
import com.google.zxing.BarcodeFormat

@Composable
fun CreateResultScreen(scannerResultScreenViewModel: ScannerResultScreenViewModel= hiltViewModel(),
                       saveViewModel: SaveViewModel = hiltViewModel(),
                       navController: NavController,
                       type: String? = null,
                       kodType: String? = null,){

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var textKod by remember { mutableStateOf("") }

    val selectQRTypeString = remember { mutableStateOf("") }


    Scaffold(modifier = Modifier.fillMaxSize()){ innerPadding ->

        ConstraintLayout(modifier = Modifier
            .padding(innerPadding)
            .fillMaxSize()
            .background(Color(0xFFF7F7F7))) {

            val (headerBar, title, textField, btn, image, iconRow, informative, adBanner) = createRefs()

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFFF5151))
                    .height(64.dp)
                    .constrainAs(headerBar) {
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        top.linkTo(parent.top)
                    },
                contentAlignment = Alignment.Center
            ) {

                Row(
                    modifier = Modifier.fillMaxSize(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Image(
                        painter = painterResource(R.drawable.arrow_left_icon),
                        contentDescription = null,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(24.dp)
                            .clickable(onClick = {
                                navController.navigate("CreateScreen")
                            })
                    )

                    Text(
                        text = "Oluştur",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.weight(1f),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    Icon(
                        painter = painterResource(R.drawable.delet_save),
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(24.dp)
                            .clickable(onClick = {
                                navController.navigate("AppScreen")
                            }
                    ))

                }

            }


            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) {
                        start.linkTo(parent.start, margin = 24.dp)
                        top.linkTo(headerBar.bottom, margin = 20.dp)
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {

                Icon(
                    painter = painterResource(if(type == "QR") R.drawable.qr_icon else R.drawable.barcod_icon),
                    contentDescription = null,
                    tint = Color(0xFF474747),
                    modifier = Modifier.size(32.dp)
                )

                Text(
                    text = kodType?: " ",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 12.dp)
                )

            }


            Box(modifier = Modifier.constrainAs(textField) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(title.bottom, margin = 8.dp)
            }) {

                if (type != null){

                    if (type == "QR"){
                        QRCustomTextField(text = { text ->
                            textKod = text
                        })
                    }
                    else if (type == "Barcod"){
                        BarcodCustomTextField(text={ text->
                            textKod = text
                        })
                    }

                }

            }

            Box(modifier = Modifier.constrainAs(btn) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(textField.bottom, margin = 4.dp)
            }) {
                if (type != null){

                    if (type == "QR"){
                        QRConverterButton(onClick = {



                        })
                    }
                    else if (type == "Barcod"){
                        BarcodConverterButton(onClick = {

                          val selectedFormat = when(kodType){
                                "AZTEC" -> BarcodeFormat.AZTEC
                                "CODE_128" -> BarcodeFormat.CODE_128
                                "EAN_13" -> BarcodeFormat.EAN_13
                                "CODABAR" -> BarcodeFormat.CODABAR
                                "CODE_39" -> BarcodeFormat.CODE_39
                                "CODE_93" -> BarcodeFormat.CODE_93
                                "DATA_MATRIX" -> BarcodeFormat.DATA_MATRIX
                                "EAN_8" -> BarcodeFormat.EAN_8
                                "ITF" -> BarcodeFormat.ITF
                                "PDF_417" -> BarcodeFormat.PDF_417
                                "UPC_A" -> BarcodeFormat.UPC_A
                                "UPC_E" -> BarcodeFormat.UPC_E
                              else -> BarcodeFormat.AZTEC
                            }

                            bitmap = scannerResultScreenViewModel.generateBarcode(textKod.toString(),selectedFormat,250,400)

                        })
                    }

                }
            }

            Box(modifier = Modifier.constrainAs(image) {

                start.linkTo(parent.start)
                end.linkTo(parent.end)
                top.linkTo(btn.bottom)

            }) {

                bitmap?.let {

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

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
                                    .clickable(
                                        indication = null, // Ripple'ı kapatır
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        saveViewModel.save(
                                            "${selectQRTypeString.value}",
                                            "${textKod}",
                                            "${scannerResultScreenViewModel.getCurrentDateTime()}"
                                        )
                                    }
                            )

                            if (scannerResultScreenViewModel.isUrl(textKod)) {
                                Image(
                                    painter = painterResource(R.drawable.open_url_icon),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .padding(end = 10.dp)
                                        .size(42.dp, 60.dp)
                                        .clickable(
                                            indication = null, // Ripple'ı kapatır
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            scannerResultScreenViewModel.openUrl(context, textKod)
                                        }
                                )
                            }

                            Image(
                                painter = painterResource(R.drawable.copy_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(42.dp, 60.dp)
                                    .clickable(
                                        indication = null, // Ripple'ı kapatır
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        scannerResultScreenViewModel.copyToClipboard(
                                            context,
                                            textKod
                                        )
                                    }
                            )

                            Image(
                                painter = painterResource(R.drawable.share_icon),
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 10.dp)
                                    .size(42.dp, 60.dp)
                                    .clickable(
                                        indication = null, // Ripple'ı kapatır
                                        interactionSource = remember { MutableInteractionSource() }) {
                                        scannerResultScreenViewModel.shareText(context, textKod)
                                    }
                            )
                            // WiFi QR kodu için bağlan butonu
                            if (scannerResultScreenViewModel.isWifiQR(textKod)) {
                                Image(
                                    painter = painterResource(R.drawable.wifi_icon), // WiFi ikonu ekleyin
                                    contentDescription = null,
                                    modifier = Modifier
                                        .size(42.dp, 60.dp)
                                        .clickable(
                                            indication = null,
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            Toast.makeText(
                                                context,
                                                "WiFi butonu çalışıyor!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            scannerResultScreenViewModel.connectToWifi(
                                                context,
                                                textKod
                                            )
                                        }
                                )
                            }
                        }
                    }

                }
            }

            Box(modifier = Modifier.constrainAs(informative) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(adBanner.top)
            }){
                Informative()
            }



            Box(modifier = Modifier.constrainAs(adBanner) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)
                bottom.linkTo(parent.bottom)
            }) {
                BannerAdView()
            }
        }
    }
}

@Composable
fun QRCustomTextField(text:(String) -> Unit) {

    var textState by remember { mutableStateOf("") }

    TextField(
        value = textState,
        onValueChange = { textState = it
                          text(textState)
                        },
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
   Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp, top = 4.dp), // dış boşluk
        shape = RoundedCornerShape(8.dp),
        colors = androidx.compose.material3.ButtonDefaults.buttonColors(
            containerColor = Color(0xFFFF5151),
            contentColor = Secondary,
            disabledContainerColor = Color(0xFFFF5151),
            disabledContentColor = Color.Gray
        ),
        contentPadding = PaddingValues(0.dp) // <- iç boşluğu kaldır
    ) {
        Text(
            text = "QR Dönüştür",
            fontSize = 20.sp,
            color = Color.White,
            modifier = Modifier.padding(vertical = 8.dp) // kontrol sende
        )
    }
}

@Composable
fun BarcodCustomTextField(text: (String) ->Unit) {
    var textState by remember { mutableStateOf("") }
    TextField(
        value = textState,
        onValueChange = { textState = it
                          text(textState)
                        },
        textStyle = TextStyle(color = Color.Black, fontSize = 18.sp),
        placeholder = {
            Text(
                text = "Lütfen Ürün Kodunu Buraya girin",
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
fun BarcodConverterButton(onClick: () -> Unit) {

    Button(
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
            disabledContentColor = Color.Gray,
        )
    ) {
        Text(text = "Barcoda Dönüştür", fontSize = 20.sp)
    }
}

@Composable
fun Informative(){
    Box(modifier = Modifier
        .fillMaxWidth()){

        Column(modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(Color.White)
           ) {

            Text(text = "AZTEC Kullanımı:",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic)
            
            Row (modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
                verticalAlignment = Alignment.CenterVertically){

                Icon(painter = painterResource(R.drawable.qr_icon),
                    contentDescription = null,
                    tint = Color(0xFFFF5151),
                    modifier = Modifier.size(54.dp)
                )

                Text(text = "Çok veri saklayabilen 2D barkod türüdür, bozulsa\n" +
                        "bile okunabilir. En az 1 karakter yeterlidir\n" +
                        "(rakamsal veya metin olabilir).",
                    modifier = Modifier.padding(start = 8.dp),
                    fontSize = 12.sp,
                    color = Color(0xFF474747),
                    fontWeight = FontWeight.Medium)

            }

            Text(text = "Genellikle biniş kartlarında ve ulaşım biletlerinde\n" +
                    "kullanılan 2D barkod türüdür. Küçük alanda çok fazla\n" +
                    "veri saklayabilir ve bozuk olsa bile okunabilir.",
                modifier = Modifier.padding(top = 8.dp),
                fontSize = 12.sp,
                color = Color(0xFF474747),
                fontWeight = FontWeight.Medium)

        }

    }
}

@Preview(showBackground = true)
@Composable
private fun Show(){
    Informative()
}