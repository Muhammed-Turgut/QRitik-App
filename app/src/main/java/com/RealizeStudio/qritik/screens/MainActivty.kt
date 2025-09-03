package com.RealizeStudio.qritik.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import com.RealizeStudio.qritik.ui.theme.QRitikAppTheme
import com.google.android.gms.ads.MobileAds
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {

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


