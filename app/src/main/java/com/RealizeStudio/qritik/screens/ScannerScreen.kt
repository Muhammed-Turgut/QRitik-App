package com.RealizeStudio.qritik.screens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
fun ScannerScreen(navController: NavController){

    navController.navigate("CameraScreen") {
        popUpTo("ScannerScreen") {
            inclusive = true // Önceki ekranı da geri yığıttan sil
        }
        launchSingleTop = true // Aynı ekran birden fazla kez üst üste açılmaz
    }


}