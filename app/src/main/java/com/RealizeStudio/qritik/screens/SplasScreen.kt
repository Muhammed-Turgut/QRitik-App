package com.RealizeStudio.qritik.screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import app.rive.runtime.kotlin.RiveAnimationView
import com.RealizeStudio.qritik.ui.theme.Primary
import com.RealizeStudio.qritik.R
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import java.nio.file.WatchEvent

@Composable
fun SplashScreen(navController: NavController){

    var animateControl by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(3000)
        animateControl = true

    }
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false // beyaz ikonlar için false
    val color = Color(0xFF9818D6) // örnek bir mavi
    val defaultColor = Color(0xFFFFFFFF)

    SideEffect {
        systemUiController.setStatusBarColor(
            color = color,
            darkIcons = useDarkIcons
        )
        systemUiController.setNavigationBarColor(
            color = color,
            darkIcons = useDarkIcons
        )
    }
    DisposableEffect(Unit) {
        onDispose {
            systemUiController.setStatusBarColor(defaultColor, darkIcons = true)
            systemUiController.setNavigationBarColor(defaultColor, darkIcons = true)
        }
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .background(Primary),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally) {

        RiveAnimationComposable()
        if (animateControl){
            navController.navigate("MainScreen") {
                popUpTo("SplashScreen") { inclusive = true }
            }
        }

    }
}


@Composable
fun RiveAnimationComposable() {

        Box(modifier = Modifier.size(240.dp)) {
             AndroidView(
                factory = { context ->
                    RiveAnimationView(context).apply {
                        setRiveResource(R.raw.splasscreenaimet)
                        play()
                    }
                }
            )
        }

        Text(text = "QRitik Kod Tarayıcı",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 100.dp))


}

@Preview(showBackground = true)
@Composable
fun goster(){
    val navController = rememberNavController()
    SplashScreen(navController)
}
