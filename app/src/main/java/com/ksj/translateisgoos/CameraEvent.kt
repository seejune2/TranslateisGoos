package com.ksj.translateisgoos

import android.content.Context
import androidx.camera.core.ImageCapture

sealed class CameraEvent {
        object OnSwitchCameraClick : CameraEvent()
        data class OnTakePhotoClick(val imageCapture: ImageCapture, val context: Context) : CameraEvent()
}
