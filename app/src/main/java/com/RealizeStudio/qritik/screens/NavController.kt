package com.RealizeStudio.qritik.screens

import android.net.Uri
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.accompanist.navigation.animation.AnimatedNavHost


@OptIn(ExperimentalAnimationApi::class)
@Composable
fun NavHostScreen(){
    val navController = rememberNavController()


        AnimatedNavHost(
            navController = navController,
            startDestination = "SplashScreen",
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) }
        ){

            composable("SplashScreen") { SplashScreen(navController) }

            composable ("AppScreen"){ AppScreen(navController) }

            composable("CameraScreen") { CameraScreen(navController) }

            composable(route="CreateResultScreen/{type}/{kodType}",
                arguments = listOf(
                    navArgument("type") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    },
                    navArgument("kodType") {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = ""
                    }

                )) { backStackEntry ->

                val type = backStackEntry.arguments?.getString("type") ?: ""
                val kodType = backStackEntry.arguments?.getString("kodType") ?: ""

                CreateResultScreen(
                    navController = navController,
                    type = if (type.isNotEmpty()) Uri.decode(type) else null,
                    kodType = if (kodType.isNotEmpty()) Uri.decode(kodType) else null)

            }

            composable("CreateScreen"){ CreateScreen(navController=navController) }

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
                    dateTime = if (dateTime.isNotEmpty()) Uri.decode(dateTime) else null)
     }
   }
}