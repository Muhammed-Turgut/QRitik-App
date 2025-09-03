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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.ui.ads.BannerAdView
import com.RealizeStudio.qritik.ui.theme.Secondary
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.ScannerResultScreenViewModel
import com.google.zxing.BarcodeFormat

@Composable
fun CreateScreen(scannerResultScreenViewModel: ScannerResultScreenViewModel = hiltViewModel(),
                 saveViewModel: SaveViewModel = hiltViewModel(),
                 navController: NavController){

    var text = remember { mutableStateOf("") }
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    val context = LocalContext.current
    val selectQRType = remember { mutableStateOf(-1) }
    val selectQRTypeString = remember { mutableStateOf("") }
    var textBarcod = remember { mutableStateOf("") }
    var barcodBitmap by remember { mutableStateOf<Bitmap?>(null) }

    when(selectQRType.value){
        0 ->  selectQRTypeString.value ="KONT_BLG"
        1 -> selectQRTypeString.value = "Ürün"
        2 -> selectQRTypeString.value = "URI"
        3 -> selectQRTypeString.value = "METIN"
        4 -> selectQRTypeString.value = "WIFI"
        5 -> selectQRTypeString.value = "E_POSTA"
        6 -> selectQRTypeString.value = "TELEFON"
        else -> selectQRTypeString.value =  "BILINMEYEN"
    }


    val selectedIndex = remember { mutableIntStateOf(-1) } // Hiçbiri seçilmemişse -1
    var type  by  remember { mutableStateOf(true) } // true -> QR Kode, false -> Barcod

    Scaffold(modifier = Modifier.fillMaxSize()){ innerPadding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .background(Color(0xFFF7F7F7)),
            horizontalAlignment = Alignment.CenterHorizontally) {

            CreateScreenHeader(selected = { boolean->
                type = boolean
            })

            Spacer(modifier = Modifier.height(16.dp))

            BannerAdView() //reklam alanı
            Spacer(modifier = Modifier.height(12.dp))

            if (type == true) {


                LazyColumn (modifier = Modifier.fillMaxWidth()){

                    items(qrKodList){item ->
                        QRKodeListRow(item,navController)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                }
            }
            else {

                LazyColumn (modifier = Modifier.fillMaxWidth()){

                    items(barKodList){item ->
                        BarKodListRow(item,navController)
                        Spacer(modifier = Modifier.height(4.dp))
                    }

                }

            }
        }
    }


}

@Composable
fun CreateScreenHeader(selected: (Boolean) -> Unit) {
    var isSelected by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFFFF5151))
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Yeni Olştur",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            )

            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick={
                        isSelected = !isSelected
                        selected(isSelected)
                    })) {


                Icon(painter = painterResource(R.drawable.qr_icon),
                    contentDescription = null,
                    tint = if (isSelected) Color.White else Color(0xFFFFFFFF).copy(0.5f))

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "QR Kod",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color =if (isSelected) Color.White else Color(0xFFFFFFFF).copy(0.5f))
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column (verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(onClick={
                        isSelected = !isSelected
                        selected(isSelected)
                    })) {

                Icon(painter = painterResource(R.drawable.barcod_icon),
                    contentDescription = null,
                    tint = if (isSelected)  Color(0xFFFFFFFF).copy(0.5f) else  Color.White)

                Spacer(modifier = Modifier.height(4.dp))

                Text(text = "Barcod",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (isSelected)  Color(0xFFFFFFFF).copy(0.5f) else  Color.White)
            }
        }
    }
}

@Composable
fun QRKodeListRow(qrKodItem: QRKodItem,
                  navController: NavController){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(62.dp),
        contentAlignment = Alignment.Center){

        Row(modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
            verticalAlignment = Alignment.CenterVertically){

            Image(painter = painterResource(qrKodItem.image),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(42.dp))

            Text(text = qrKodItem.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp))

            Image(painter = painterResource(R.drawable.arrow_right_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(18.dp)
                    .clickable(onClick = {
                        navController.navigate("CreateResultScreen/${"QR"}/${qrKodItem.name}")
                    }))

        }
    }
}

@Composable
fun BarKodListRow(item: BarKodItem,
                  navController: NavController){

    Box(modifier = Modifier
        .fillMaxWidth()
        .height(62.dp),
        contentAlignment = Alignment.Center){

        Row (modifier = Modifier
            .fillMaxSize()
            .padding(start = 16.dp, end = 16.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White),
            verticalAlignment = Alignment.CenterVertically){

            Icon(painter = painterResource(R.drawable.barcod_icon),
                contentDescription = null,
                tint = Color(0xFF565656),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .size(42.dp))

            Text(text = item.name,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .padding(14.dp))

            Image(painter = painterResource(R.drawable.arrow_right_icon),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 12.dp)
                    .size(18.dp)
                    .clickable(onClick = {
                        navController.navigate("CreateResultScreen/${"Barcod"}/${item.name}")
                    }))

        }

    }
}


data class BarKodItem(val name: String)
private val barKodList = listOf<BarKodItem>(
    BarKodItem("AZTEC"),
    BarKodItem("CODE_128"),
    BarKodItem("EAN_13"),
    BarKodItem("CODABAR"),
    BarKodItem("CODE_39"),
    BarKodItem("CODE_93"),
    BarKodItem("DATA_MATRIX"),
    BarKodItem("EAN_8"),
    BarKodItem("ITF"),
    BarKodItem("PDF_417"),
    BarKodItem("UPC_A"),
    BarKodItem("UPC_E"))


data class QRKodItem(val name: String, val image:Int)
private val qrKodList = listOf<QRKodItem>(
    QRKodItem("Metin",R.drawable.row_txt_icon),
    QRKodItem("URL",R.drawable.row_url_icon),
    QRKodItem("Telefon",R.drawable.row_phone_icon),
    QRKodItem("Wi-Fi",R.drawable.row_wifi_icon),
    QRKodItem("Email Adresi",R.drawable.row_email_icon),
    QRKodItem("Kişi",R.drawable.row_user_icon))





@Preview(showBackground = true)
@Composable
private fun Show(){
    //QRKodeListRow()
}
