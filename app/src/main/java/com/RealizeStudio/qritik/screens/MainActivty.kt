package com.RealizeStudio.qritik.screens

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import com.RealizeStudio.qritik.screens.navigator.NavHostScreen
import com.RealizeStudio.qritik.ui.theme.QRitikAppTheme
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        enableEdgeToEdge()

        setContent {

            QRitikAppTheme {
                MobileAds.initialize(this) {}
                NavHostScreen()
            }
        }
    }
}


