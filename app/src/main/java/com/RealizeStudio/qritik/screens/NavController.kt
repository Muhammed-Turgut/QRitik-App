package com.RealizeStudio.qritik.screens

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun NavHostScreen(){
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "SplashScreen") {

        composable("SplashScreen") { SplashScreen(navController) }

        composable ("AppScreen"){ AppScreen() }

    }
}