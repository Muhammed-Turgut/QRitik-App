package com.RealizeStudio.qritik.viewModel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import com.RealizeStudio.qritik.screens.QRCodeAnalyzer
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.concurrent.Executors
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor() : ViewModel() {

    fun toggleFlash(camera: Camera, enable: Boolean) {
        camera.cameraControl.enableTorch(enable)
    }

     fun bindCamera(
        cameraProvider: ProcessCameraProvider,
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner,
        context: Context,
        lensFacing: Int = CameraSelector.LENS_FACING_BACK,
        onQRCodeDetected: (String, String?, String, String) -> Unit,
        onError: (String) -> Unit
    ): Camera {
        try {
            // Preview
            val preview = Preview.Builder()
                .setTargetResolution(android.util.Size(1280, 720))
                .build()
                .also {
                    it.setSurfaceProvider(previewView.surfaceProvider)
                }

            // Image Analysis
            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(android.util.Size(1280, 720))
                .build()
                .also {
                    it.setAnalyzer(
                        Executors.newSingleThreadExecutor(),
                        QRCodeAnalyzer(context, onQRCodeDetected, onError)
                    )
                }

            // Kamera seçici
            val cameraSelector = CameraSelector.Builder()
                .requireLensFacing(lensFacing)
                .build()

            // Mevcut bağlantıları kaldır
            cameraProvider.unbindAll()

            // Kamera bağlantısını başlat ve Camera nesnesini döndür
            return cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraSelector,
                preview,
                imageAnalysis
            )
        } catch (exc: Exception) {
            Log.e("CameraX", "Kamera bağlantısı başarısız", exc)
            onError("Kamera başlatılamadı: ${exc.message}")
            throw exc
        }
    }

    fun analyzeImageFromUri(
        context: Context,
        imageUri: Uri,
        onQRCodeDetected: (String, String, String, String) -> Unit,
        onError: (String) -> Unit
    ) {
        try {
            val resizedBitmap = resizeImage(imageUri, context)
            val inputImage = resizedBitmap?.let { InputImage.fromBitmap(it, 0) }
            val scanner = BarcodeScanning.getClient()

            scanner.process(inputImage!!)
                .addOnSuccessListener { barcodes ->
                    if (barcodes.isNotEmpty()) {
                        val barcode = barcodes[0]
                        val rawValue = barcode.rawValue
                        val codeType = when (barcode.valueType) {
                            Barcode.TYPE_TEXT -> "METIN"
                            Barcode.TYPE_URL -> "URL"
                            Barcode.TYPE_CONTACT_INFO -> "KONT_BLG"
                            Barcode.TYPE_EMAIL -> "E_POSTA"
                            Barcode.TYPE_PHONE -> "TELEFON"
                            Barcode.TYPE_SMS -> "SMS"
                            Barcode.TYPE_WIFI -> "WIFI"
                            Barcode.TYPE_GEO -> "KONUM"
                            else -> "UNKNOWN"
                        }
                        val imagePath = imageUri.toString()
                        val dateTime = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault())
                            .format(java.util.Date())

                        if (rawValue != null) {
                            onQRCodeDetected(rawValue, imagePath, codeType, dateTime)
                        } else {
                            onError("QR kod değeri boş.")
                        }
                    } else {
                        onError("QR kod bulunamadı.")
                    }
                }
                .addOnFailureListener { e ->
                    onError("Analiz sırasında hata oluştu: ${e.message}")
                }
        } catch (e: Exception) {
            onError("Resim işlenemedi: ${e.message}")
        }
    }

    fun resizeImage(uri: Uri, context: Context, maxSize: Int = 1024): Bitmap? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            val aspectRatio = originalBitmap.width.toFloat() / originalBitmap.height
            val width: Int
            val height: Int

            if (originalBitmap.width > originalBitmap.height) {
                width = maxSize
                height = (width / aspectRatio).toInt()
            } else {
                height = maxSize
                width = (height * aspectRatio).toInt()
            }

            Bitmap.createScaledBitmap(originalBitmap, width, height, true)
        } catch (e: Exception) {
            null
        }
    }



}
