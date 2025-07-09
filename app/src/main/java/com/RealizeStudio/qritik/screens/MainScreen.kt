package com.RealizeStudio.qritik.screens

import android.widget.Button
import androidx.annotation.Nullable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.ui.theme.Primary
import com.RealizeStudio.qritik.ui.theme.Secondary


@Composable
fun MainScreen(){

    Column(modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally) {

            MainScreenHeader()
            CustomTextField()
            ConverterButton(onClick = {

            })

            OpenGalleryButton(onClick = {

            })
            SaveListem(list)

    }

}
@Composable
fun SaveListem(list: List<SaveListOf>){


    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 16.dp, end = 16.dp, top = 12.dp)){

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.Absolute.Left){

            Image(painter = painterResource(R.drawable.save_selected), contentDescription = "")
            Text(text = "Kayıtlar"
                ,fontSize = 20.sp,
                fontWeight = FontWeight.Medium)

        }

        if(list.isEmpty()){
            Column(modifier = Modifier.fillMaxWidth(),
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
                    SaveRow(it)
                }

            }
        }
    }
}

@Composable
fun SaveRow(list: SaveListOf){
    Row (modifier = Modifier
        .fillMaxWidth()
        .padding(top = 4.dp, bottom = 4.dp)){

        Image(painter = painterResource(R.drawable.qrcode), contentDescription = "")
        Column(modifier = Modifier.padding(start = 8.dp).weight(1f)) {

            Text(text = "QR name",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black)

            Text(text = "QR Link",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray)
        }

        Column {
            Image(modifier = Modifier.size(20.dp),
                painter = painterResource(R.drawable.delet_save),
                contentDescription = "")

            Image(modifier = Modifier.padding(top = 4.dp).size(18.dp),
                painter = painterResource(R.drawable.update_save),
                contentDescription = "")
        }
    }
}

@Composable
fun OpenGalleryButton(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp) // Butonlar arası boşluk
    ) {
        androidx.compose.material3.Button(
            onClick = onClick,
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Primary,
                contentColor = Primary,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Gray
            )
        ) {
            Image(painter = painterResource(R.drawable.galleriy),
                contentDescription = "")


            Text(text = "Galleriyi aç",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 18.sp,
                color = Color.White)
        }

        androidx.compose.material3.Button(
            onClick = onClick,
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(4.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Secondary,
                contentColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                disabledContentColor = Color.Gray
            )
        ) {
            Image(painter = painterResource(R.drawable.camera),
                contentDescription = "")

            Text(text = "Kameryı aç",
                modifier = Modifier.padding(start = 4.dp),
                fontSize = 18.sp,
                color = Color.White)
        }
    }


}

@Composable
fun CustomTextField() {
    var textState by remember { mutableStateOf("") }

    TextField(
        value = textState,
        onValueChange = { textState = it },
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
fun ConverterButton(onClick: () -> Unit) {


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

@Composable
fun MainScreenHeader(){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(top = 16.dp),
        horizontalArrangement = Arrangement.Absolute.Center,
        verticalAlignment = Alignment.CenterVertically){

        Image(painter = painterResource(R.drawable.qritik_logo),
            contentDescription = "null",
            modifier = Modifier.size(44.dp))

        Text(text = "QRitik",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFF5151)
        )
    }
}

@Composable
@Preview(showBackground = true)
fun showe(){
    MainScreen()
}

val list = listOf<SaveListOf>()

data class SaveListOf(val listName:String)