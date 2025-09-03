package com.RealizeStudio.qritik.screens


import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.RealizeStudio.qritik.R
import com.RealizeStudio.qritik.viewModel.PermissionViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    navController: NavController,
    permissionViewModel: PermissionViewModel = hiltViewModel()
) {
    val permissionState by permissionViewModel.permissionsGranted.collectAsState()
    var permissionRequested by remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        permissionRequested = true
        permissionViewModel.updatePermissionStatus(result)

        // İzin işlemi bittikten sonra direkt navigasyon yap
        navController.navigate("AppScreen") {
            popUpTo("SplashScreen") { inclusive = true }
        }
    }

    // İzin durumuna göre işlem yap
    LaunchedEffect(permissionState, permissionRequested) {
        when {
            permissionState -> {
                delay(3000)
                navController.navigate("AppScreen") {
                    popUpTo("SplashScreen") { inclusive = true }
                }
            }
            !permissionRequested -> {
                // İzin henüz istenmemiş, iste
                println("İzin isteniyor...")
                launcher.launch(PermissionViewModel.REQUIRED_PERMISSIONS)
            }
        }


    }

    // UI renkleri
    val systemUiController = rememberSystemUiController()
    val useDarkIcons = false
    val color = Color(0xFF9818D6)
    val defaultColor = Color.White

    SideEffect {
        systemUiController.setStatusBarColor(color, darkIcons = useDarkIcons)
        systemUiController.setNavigationBarColor(color, darkIcons = useDarkIcons)
    }

    DisposableEffect(Unit) {
        onDispose {
            systemUiController.setStatusBarColor(defaultColor, darkIcons = true)
            systemUiController.setNavigationBarColor(defaultColor, darkIcons = true)
        }
    }

    // Splash arayüzü
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color)
            .padding(bottom = 16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {


        Image(painter = painterResource(R.drawable.logo),
            contentDescription = "",
            modifier = Modifier.size(180.dp))

        Spacer(modifier = Modifier.height(12.dp))
        Text(text = "QRitik QR kod ve Barkod tarayıcı",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            color= Color.White)
    }

}




