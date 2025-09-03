package com.RealizeStudio.qritik.screens

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.google.accompanist.navigation.animation.AnimatedNavHost

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun AppScreen(navControllerNoBottom: NavController,
              viewModel: SaveViewModel = hiltViewModel()){

    println("App screen calisti")

    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController,navControllerNoBottom)
        }
    ) { innerPadding ->

        AnimatedNavHost(
            navController = navController,
            startDestination = BottomNavItem.Home.route,
            enterTransition = { fadeIn(animationSpec = tween(150)) },
            exitTransition = { fadeOut(animationSpec = tween(150)) },
            modifier = Modifier.padding(innerPadding)

        )
        {
            composable(BottomNavItem.Home.route) {
                MainScreen(viewModel)
            }

            composable(BottomNavItem.Create.route) {
                CreateScreen(saveViewModel = viewModel)
            }

        }
    }
}
