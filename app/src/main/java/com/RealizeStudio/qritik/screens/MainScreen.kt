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
import androidx.hilt.navigation.compose.hiltViewModel
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
fun MainScreen(viewModel: SaveViewModel = hiltViewModel(),
               scannerResultScreenViewModel: ScannerResultScreenViewModel = hiltViewModel()) {


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
                color = if(selectedType== true) Color.Black else Color(0xFFA3A3A3)
            )

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
                color = if(selectedType== false) Color.Black else Color(0xFFA3A3A3))
        }


    }
}

@Composable
fun MainScreenHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(Color(0xFFFF5151))
    ) {
        Row(
            modifier = Modifier.align(Alignment.Center),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.qritik_logo),
                contentDescription = "Logo",
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "QRitik",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}


@Preview(showBackground = true)
@Composable
private fun Show(){
    MainScreenHeader()
}

