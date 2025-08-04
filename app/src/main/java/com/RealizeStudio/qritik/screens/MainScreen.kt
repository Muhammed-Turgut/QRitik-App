package com.RealizeStudio.qritik.screens

import android.content.Context
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallTopAppBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.data.entity.QRsavesItem
import com.RealizeStudio.qritik.ui.ads.BannerAdView
import com.RealizeStudio.qritik.ui.theme.Primary
import com.RealizeStudio.qritik.ui.theme.Secondary
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel
import com.google.zxing.BarcodeFormat


@Composable
fun MainScreen(viewModel: SaveViewModel,scannerResultScreenViewModel: ScannerResultScreenViewModel = viewModel()) {

    var textState = remember { mutableStateOf("") }
    var text = remember { mutableStateOf("") }
    var barcodBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        MainScreenHeader()
        Spacer(modifier = Modifier.height(12.dp))
        SelectedListType()
        Spacer(modifier = Modifier.height(12.dp))
        BannerAdView()
        barcodBitmap?.let {
            Spacer(modifier = Modifier.padding(top = 8.dp))

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
                                viewModel.save("Barcod","${text.value}","${scannerResultScreenViewModel.getCurrentDateTime()}")
                            }
                    )

                    if (scannerResultScreenViewModel.isUrl(text.value)) {
                        Image(
                            painter = painterResource(R.drawable.open_icon),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 10.dp)
                                .size(42.dp, 60.dp)
                                .clickable( indication = null, // Ripple'ı kapatır
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    scannerResultScreenViewModel.openUrl(context, text.value)
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
                                scannerResultScreenViewModel.copyToClipboard(context, text.value)
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
                                scannerResultScreenViewModel.shareText(context, text.value)
                            }
                    )
                    // WiFi QR kodu için bağlan butonu
                    if (scannerResultScreenViewModel.isWifiQR(text.value)) {
                        Image(
                            painter = painterResource(R.drawable.wifi_icon), // WiFi ikonu ekleyin
                            contentDescription = null,
                            modifier = Modifier
                                .size(42.dp, 60.dp)
                                .clickable( indication = null,
                                    interactionSource = remember { MutableInteractionSource() }) {
                                    Toast.makeText(context, "WiFi butonu çalışıyor!", Toast.LENGTH_SHORT).show()
                                    scannerResultScreenViewModel.connectToWifi(context, text.value)
                                }
                        )
                    }
                }
            }

        }

        Spacer(modifier = Modifier.padding(top = 12.dp))

        val saveList = viewModel.saveList.collectAsState()
        SaveListem(saveList.value, viewModel,scannerResultScreenViewModel,context)
    } // Column bitti

} // Composable bitti

@Composable
fun SaveListem(list: List<QRsavesItem>,viewModel: SaveViewModel, screenViewModel: ScannerResultScreenViewModel,context: Context){


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 12.dp)){

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Start ,
            verticalAlignment = Alignment.CenterVertically ){

            Image(painter = painterResource(R.drawable.save_selected),
                contentDescription = "",
                modifier = Modifier.size(20.dp))

            Text(text = "Kayıtlar"
                ,fontSize = 20.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium)

        }


        Spacer(modifier = Modifier.padding(top = 4.dp))


        if(list.isEmpty()){
            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally){

                Text(text = "Listelenecek kayıt yok",
                    fontSize = 20.sp,
                    color = Color.Gray)

            }
        }
        else{
            LazyColumn(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)){

                items(list) { it ->
                    SaveRow(it,viewModel,screenViewModel,context)
                }

            }
        }
    }
}

@Composable
fun SaveRow(list: QRsavesItem,viewModel: SaveViewModel,screenViewModel: ScannerResultScreenViewModel, context: Context){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, bottom = 4.dp)){

        Image(painter = painterResource(
            when(list.QR_Type){
                "METIN" -> R.drawable.row_txt_icon
                "URL" -> R.drawable.row_url_icon
                "KONT_BLG" -> R.drawable.qrcode
                "E_POSTA" -> R.drawable.row_email_icon
                "TELEFON" -> R.drawable.row_phone_icon
                "SMS" -> R.drawable.qrcode
                "WIFI" -> R.drawable.row_wifi_icon
                "KONUM" -> R.drawable.qrcode
                else -> R.drawable.qrcode
            }),
            contentDescription = "",
            modifier = Modifier.clickable(onClick = {
                when(list.QR_Type){
                    "METIN" -> screenViewModel.copyToClipboard(context,list.QR_contents.toString())
                    "URL" ->  screenViewModel.openUrl(context,list.QR_contents.toString())
                    "KONT_BLG" -> screenViewModel.copyToClipboard(context,list.QR_contents.toString())
                    "E_POSTA" -> screenViewModel.copyToClipboard(context,list.QR_contents.toString())
                    "TELEFON" ->  screenViewModel.openDialerWithNumber(context,list.QR_contents.toString())
                    "SMS" ->   screenViewModel.copyToClipboard(context,list.QR_contents.toString())
                    "WIFI" -> screenViewModel.connectToWifi(context,list.QR_contents.toString())
                    "KONUM" -> screenViewModel.copyToClipboard(context,list.QR_contents.toString())
                    else -> screenViewModel.copyToClipboard(context,list.QR_contents.toString())
                }
            }))


        Column(modifier = Modifier.padding(start = 8.dp).weight(1f),
            verticalArrangement = Arrangement.Center) {

            Text(text = "${list.QR_Type}",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black)

            Text(text = "${list.QR_contents}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis)
        }

            Image(modifier = Modifier
                .size(20.dp)
                .clickable(onClick = {
                    //listeden kaydı sildi.
                    viewModel.delete(list.id)
                }),
                painter = painterResource(R.drawable.delet_save),
                contentDescription = "")

    }
}


@Composable
fun BarcodCustomTextField(textState: MutableState<String>) {

    TextField(
        value = textState.value,
        onValueChange = { textState.value = it },
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
            disabledContentColor = Color.Gray,
        )
    ) {
        Text(text = "Barcoda Dönüştür", fontSize = 20.sp)
    }
}

@Composable
fun SelectedListType(){
    var selectedType by remember { mutableStateOf(true)}

    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){

        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally) {

            Text(text = "Tarananlar",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = {
                    selectedType=true
                }),
                color = if(selectedType== true) Secondary else Color(0xFFA3A3A3)
            )

            if(selectedType== true){
                Box(modifier = Modifier
                    .height(5.dp)
                    .width(130.dp)
                    .clip(shape = CircleShape)
                    .background(color = Primary))
            }

        }

        Column(modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally)  {

            Text(text = "Üretilenler",
                modifier = Modifier.clickable(onClick = {
                   selectedType=false
                }),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if(selectedType== false) Secondary else Color(0xFFA3A3A3))

            if(selectedType == false){
                Box(modifier = Modifier
                    .height(5.dp)
                    .width(130.dp)
                    .clip(shape = CircleShape)
                    .background(color = Primary))
            }

        }


    }
}

@Composable
fun MainScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RectangleShape, ambientColor = Color.Gray, spotColor = Color.Gray)
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.qritik_logo),
                contentDescription = "Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "QRitik",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFF5151)
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
fun show(){
    SelectedListType()
}