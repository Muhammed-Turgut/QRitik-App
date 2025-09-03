package com.RealizeStudio.qritik.viewModel


import android.app.Application
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(application: Application) : AndroidViewModel(application) {

    private val context = application.applicationContext

    private val _permissionsGranted = MutableStateFlow(false)
    val permissionsGranted: StateFlow<Boolean> = _permissionsGranted

    private val _cameraPermissionGranted = MutableStateFlow(false)
    val cameraPermissionGranted: StateFlow<Boolean> = _cameraPermissionGranted

    init {
        checkPermissions(REQUIRED_PERMISSIONS) // Otomatik kontrol
    }

    fun checkPermissions(permissions: Array<String>) {
        val allGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
        _permissionsGranted.value = allGranted

        // Kamera izni ayrıca kontrol et
        val cameraGranted = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        _cameraPermissionGranted.value = cameraGranted

        println("Tüm izinler: $allGranted, Kamera izni: $cameraGranted")
    }

    fun updatePermissionStatus(grantResults: Map<String, Boolean>) {
        val granted = grantResults.values.all { it }
        _permissionsGranted.value = granted

        // Kamera izni durumunu güncelle
        grantResults[android.Manifest.permission.CAMERA]?.let { cameraGranted ->
            _cameraPermissionGranted.value = cameraGranted
        }
    }
    fun updateCameraPermission(isGranted: Boolean) {
        _cameraPermissionGranted.value = isGranted
    }

    // Kamera kullanımı öncesi kontrol için
    fun isCameraPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        val REQUIRED_PERMISSIONS = arrayOf(
            android.Manifest.permission.CAMERA,
            android.Manifest.permission.RECORD_AUDIO
        )
    }
}
