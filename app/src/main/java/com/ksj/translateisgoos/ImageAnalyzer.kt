package com.ksj.translateisgoos

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.common.InputImage


class ImageAnalyzer(
    private val textRecognizer: com.google.mlkit.vision.text.TextRecognizer,
    private val onTextDetected: (String) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastDetectionTime = 0L
    private val throttleInterval = 3000L // 5초

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val currentTime = System.currentTimeMillis()
        if (currentTime - lastDetectionTime < throttleInterval) {
            imageProxy.close()
            return
        }

        // 텍스트 인식 처리
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val inputImage =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    lastDetectionTime = System.currentTimeMillis()
                    val detectedText = visionText.text
                    onTextDetected(detectedText)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }
}