package com.RealizeStudio.qritik.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import com.RealizeStudio.qritik.data.dataSource.SaveDataSource
import com.RealizeStudio.qritik.data.repo.SaveRepository
import com.RealizeStudio.qritik.room.QRsavesDatabase
import com.RealizeStudio.qritik.ui.theme.QRitikAppTheme
import com.RealizeStudio.qritik.viewModel.SaveViewModel
import com.RealizeStudio.qritik.viewModel.SaveViewModelFactory
import com.google.android.gms.ads.MobileAds



class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = QRsavesDatabase.getInstance(applicationContext)
        val dao = database.qrSavesItemDao()
        val saveDataSource = SaveDataSource(dao)
        val saveRepository = SaveRepository(saveDataSource)
        val viewModelFactory = SaveViewModelFactory(saveRepository)
        val saveViewModel = ViewModelProvider(this, viewModelFactory)[SaveViewModel::class.java]

        setContent {

            QRitikAppTheme {
                MobileAds.initialize(this) {}
                NavHostScreen(saveViewModel)
            }
        }
    }
}


