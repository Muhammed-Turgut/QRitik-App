package com.RealizeStudio.qritik.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppScreen(navControllerNoBottom: NavController){

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Home.route) {
                MainScreen()
            }
            composable(BottomNavItem.Scanne.route) {
                ScannerScreen(navControllerNoBottom)
            }
            composable(BottomNavItem.Create.route) {
                CreateScreen()
            }

        }
    }
}
