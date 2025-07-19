package com.RealizeStudio.qritik.viewModel

import androidx.camera.core.Camera
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    fun toggleFlash(camera: Camera, enable: Boolean) {
        camera.cameraControl.enableTorch(enable)
    }
}
