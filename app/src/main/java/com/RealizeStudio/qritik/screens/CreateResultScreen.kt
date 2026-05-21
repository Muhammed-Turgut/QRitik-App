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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.LaunchedEffect

@Composable
fun CreateResultScreen(scannerResultScreenViewModel: ScannerResultScreenViewModel= hiltViewModel(),
                       saveViewModel: SaveViewModel = hiltViewModel(),
                       navController: NavController,
                       type: String? = null,
                       kodType: String? = null,){

    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    var textKod by remember { mutableStateOf("") }

    val selectQRTypeString = remember(kodType) {
        mutableStateOf(
            if (type == "QR") {
                when (kodType) {
                    "Metin" -> "METIN"
                    "URL" -> "URL"
                    "Telefon" -> "TELEFON"
                    "Wi-Fi" -> "WIFI"
                    "Email Adresi" -> "E_POSTA"
                    "Kişi" -> "KONT_BLG"
                    else -> "METIN"
                }
            } else {
                kodType ?: "BARCODE"
            }
        )
    }


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
                                navController.navigate("AppScreen")
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
                    color= Color(0xFF474747),
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
                        QRDynamicFields(kodType = kodType ?: "Metin", onValueCompiled = { text ->
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
                            if (textKod.trim().isNotEmpty()) {
                                bitmap = scannerResultScreenViewModel.generateQrCode(textKod, size = 512)
                            } else {
                                Toast.makeText(context, "Lütfen gerekli alanları doldurun!", Toast.LENGTH_SHORT).show()
                            }
                        })
                    }
                    else if (type == "Barcod"){
                        BarcodConverterButton(onClick = {
                            if (textKod.trim().isEmpty()) {
                                Toast.makeText(context, "Lütfen bir ürün kodu girin!", Toast.LENGTH_SHORT).show()
                                return@BarcodConverterButton
                            }

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
                top.linkTo(btn.bottom, margin = 24.dp)

            }) {

                bitmap?.let {

                    if (it.width == 100 && it.height == 100 ){


                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                           Text(text = "Lütfen Açıklamayı okuyup doğru\n"+
                                   "değerler ile üretim yapın",
                               color = Color.Black,
                               textAlign = TextAlign.Center,
                               fontSize = 18.sp,
                               lineHeight = 18.sp,
                               fontStyle = FontStyle.Normal,
                               fontWeight = FontWeight.Bold)


                        }


                    }
                    else{
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {

                            Image(bitmap = it.asImageBitmap(),
                                contentDescription = "QR Code",
                                modifier = Modifier.size(100.dp))

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
                                        .size(32.dp, 48.dp)
                                        .clickable(
                                            indication = null, // Ripple'ı kapatır
                                            interactionSource = remember { MutableInteractionSource() }) {
                                            saveViewModel.save(
                                                "${selectQRTypeString.value}",
                                                "${textKod}",
                                                "${scannerResultScreenViewModel.getCurrentDateTime()}",
                                                isCreated = true
                                            )
                                            Toast.makeText(context, "Kayıt başarıyla kaydedildi!", Toast.LENGTH_SHORT).show()
                                        }
                                )

                                if (scannerResultScreenViewModel.isUrl(textKod)) {
                                    Image(
                                        painter = painterResource(R.drawable.open_url_icon),
                                        contentDescription = null,
                                        modifier = Modifier
                                            .padding(end = 10.dp)
                                            .size(32.dp, 48.dp)
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
                                        .size(32.dp, 48.dp)
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
                                        .size(32.dp, 48.dp)
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
                                            .size(32.dp, 48.dp)
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
            }

            Box(modifier = Modifier.constrainAs(informative) {
                start.linkTo(parent.start)
                end.linkTo(parent.end)

                if (bitmap == null){
                    top.linkTo(btn.bottom, margin = 16.dp)
                }
                else{
                    bottom.linkTo(adBanner.top)
                }

            }){

                val item = when(kodType){
                    "AZTEC" -> informativeList[0]
                    "CODE_128" -> informativeList[1]
                    "EAN_13" -> informativeList[2]
                    "CODABAR" -> informativeList[3]
                    "CODE_39" -> informativeList[4]
                    "CODE_93" -> informativeList[5]
                    "DATA_MATRIX" -> informativeList[6]
                    "EAN_8" -> informativeList[7]
                    "ITF" -> informativeList[8]
                    "PDF_417" -> informativeList[9]
                    "UPC_A" -> informativeList[10]
                    "UPC_E" -> informativeList[11]
                    else -> informativeList[0]
                }
                Informative(item)
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

val informativeList = listOf<InformativeKod>(
    InformativeKod("AZTEC",R.drawable.aztec_icon,"Çok veri saklayabilen 2D barkod türüdür, bozulsa bile okunabilir. En az 1 karakter yeterlidir (rakamsal veya metin olabilir).","Genellikle biniş kartlarında ve ulaşım biletlerinde kullanılan 2D barkod türüdür. Küçük alanda çok fazla veri saklayabilir ve bozuk olsa bile okunabilir."),

    InformativeKod("CODE_128",R.drawable.codee_icon,"Harf, rakam ve özel karakterleri destekleyen yoğun lineer barkoddur. En az 1 karakter gerekir, uzunluk sınırlaması yoktur.","Harf, rakam ve özel karakterleri destekleyen, yüksek yoğunluklu lineer barkod formatıdır. Genellikle lojistik, depo yönetimi ve kargo takibinde kullanılır."),

    InformativeKod("EAN_13",R.drawable.ean_icon,"Market ürünlerinde kullanılan 12 haneli sayısal barkoddur. Tam 13 hane rakam olmalı, başka karakter kabul etmez..","Marketlerde ürünlerin kasa okutma sisteminde gördüğünüz 13 haneli sayısal barkoddur. Küresel ticarette standart ürün kimliği sağlar."),

    InformativeKod("CODABAR",R.drawable.codabar_icon,"Kan bankası ve kütüphane sistemlerinde kullanılan basit barkoddur. En az 2 rakam veya karakter gerekir, genellikle 0–9, A–D harfleri kabul edilir.","Eski ama basit yapılı barkod formatıdır. Kan bankaları, kütüphaneler ve bazı sağlık sistemlerinde tercih edilir."),

    InformativeKod("CODE_39",R.drawable.codeee_icon,"Harf ve rakamları destekleyen ilk yaygın barkodlardan biridir. En az 1 karakter olmalı, A–Z, 0–9 ve bazı semboller kullanılabilir.","Harfleri ve rakamları destekleyen ilk yaygın barkodlardan biridir. Otomotiv ve savunma sanayinde uzun yıllardır kullanılır."),

    InformativeKod("CODE_93",R.drawable.codeeee_icon,"Code 39’un daha kompakt versiyonudur. En az 1 karakter olmalı, A–Z, 0–9 ve özel karakterleri destekler.","Code 39’un geliştirilmiş, daha yoğun veri saklayabilen versiyonudur. Özellikle posta hizmetlerinde ve kurumsal envanter yönetiminde kullanılır."),

    InformativeKod("DATA_MATRIX",R.drawable.data_matrix_icon,"Küçük alanda çok veri saklayan 2D barkoddur. En az 1 karakter (metin veya rakam) yeterlidir.","Çok küçük boyutta büyük veri saklayabilen 2D barkoddur. İlaç kutularında ve elektronik bileşenlerde sıkça kullanılır."),

    InformativeKod("EAN_8",R.drawable.eann_icon,"Küçük ürünlerde kullanılan kısa barkoddur. Tam 8 hane rakam olmalı, başka karakter kabul etmez.","Küçük paketler için tasarlanmış 8 haneli sayısal barkoddur. Genellikle küçük ürünlerde alan tasarrufu için tercih edilir."),

    InformativeKod("ITF",R.drawable.itf_icon,"Sadece sayısal veri için kullanılan barkoddur. En az 2 rakam olmalı ve rakam sayısı çift haneli olmalıdır (örn. 02, 1234, 567890).","Sadece rakamları destekleyen, çift satırlı (interleaved) barkod formatıdır. Karton koli ve lojistik ambalajlamada yaygın şekilde kullanılır."),

    InformativeKod("PDF_417",R.drawable.pdf_icon,"Çok satırlı 2D barkoddur, belgelerde ve kimliklerde kullanılır. En az 1 karakter yeterlidir, binlerce karakter saklayabilir.","Çok satırlı 2D barkod türüdür. Kimlik kartları, sürücü belgeleri ve nakliye evraklarında sıkça karşımıza çıkar."),

    InformativeKod("UPC_A",R.drawable.upc_a_icon,"ABD ve Kanada’da kullanılan 12 haneli barkoddur. Tam 12 hane rakam olmalı, harf kabul etmez.","ABD ve Kanada’da perakende ürünlerde kullanılan 12 haneli barkoddur. Market raflarındaki çoğu ürün bu formatla etiketlenir."),

    InformativeKod("UPC_E",R.drawable.upc_e_icon,"UPC-A’nın sıkıştırılmış versiyonudur. Tam 6 hane rakam olmalı, bazı durumlarda sistem 12 haneye genişletir.","UPC-A’nın daha kısa ve sıkıştırılmış versiyonudur. Küçük paketlerde veya dar etiket alanlarında kullanılır."),)

@Composable
fun Informative(informativeKod: InformativeKod){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Başlık
            Text(
                text = "${informativeKod.name} Kullanımı",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2C2C2C),
                modifier = Modifier.padding(bottom = 14.dp)
            )

            // İkon ve kullanım açıklaması
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // İkon container
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = Color(0xFFFF5151).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(informativeKod.image),
                        contentDescription = "${informativeKod.name} ikonu",
                        tint = Color(0xFFFF5151),
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Kullanım açıklaması
                Text(
                    text = informativeKod.text1,
                    modifier = Modifier
                        .padding(start = 8.dp)
                        .weight(1f),
                    fontSize = 12.sp,
                    color = Color(0xFF474747),
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
            }

            // Ayırıcı çizgi
            Divider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                color = Color(0xFFE0E0E0),
                thickness = 1.dp
            )

            // Kullanım alanları başlığı
            Text(
                text = "Kullanım Alanları:",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF2C2C2C),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Kullanım alanları açıklaması
            Text(
                text = informativeKod.text2,
                fontSize = 11.sp,
                color = Color(0xFF666666),
                fontWeight = FontWeight.Normal,
                lineHeight = 18.sp
            )
        }
    }
}

data class InformativeKod( val name:String, val image: Int, val text1:String, val text2:String)

@Preview(showBackground = true)
@Composable
private fun Show(){

}