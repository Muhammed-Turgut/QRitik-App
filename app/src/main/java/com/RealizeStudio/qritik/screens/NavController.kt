package com.RealizeStudio.qritik.screens

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun NavHostScreen(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "SplashScreen") {

        composable("SplashScreen") { SplashScreen(navController) }

        composable ("AppScreen"){ AppScreen(navController) }

        composable("CameraScreen") { CameraScreen(navController) }

        composable(
            route = "ScannerResult/{qrCodeData}/{imagePath}/{codeType}/{dateTime}",
            arguments = listOf(
                navArgument("qrCodeData") {
                    type = NavType.StringType
                },
                navArgument("imagePath") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("codeType") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                },
                navArgument("dateTime") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = ""
                }
            )
        ) { backStackEntry ->
            val qrCodeData = backStackEntry.arguments?.getString("qrCodeData") ?: ""
            val imagePath = backStackEntry.arguments?.getString("imagePath") ?: ""
            val codeType = backStackEntry.arguments?.getString("codeType") ?: ""
            val dateTime = backStackEntry.arguments?.getString("dateTime") ?: ""

            ScannerResultScreen(
                navController = navController,
                qrCodeData = Uri.decode(qrCodeData),
                imagePath = if (imagePath.isNotEmpty()) Uri.decode(imagePath) else null,
                codeType = if (codeType.isNotEmpty()) Uri.decode(codeType) else null,
                dateTime = if (dateTime.isNotEmpty()) Uri.decode(dateTime) else null
            )
        }

    }
}