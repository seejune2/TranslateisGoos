package com.ksj.translateisgoos

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.compose.runtime.remember
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions

class ImageAnalyzer(
    private val textRecognizer: TextRecognizer, // TextRecognizer를 생성자로 전달받음
    private val onTextDetected: (String) -> Unit // 텍스트 검출 결과를 전달하기 위한 콜백)
) : ImageAnalysis.Analyzer {

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // ML Kit 텍스트 인식 처리
            textRecognizer.process(image)
                .addOnSuccessListener { visionText ->
                    if (visionText.text.isNotEmpty())
                        onTextDetected(visionText.text) // 텍스트 검출 성공 시 콜백 호출
                }
                .addOnFailureListener {
                    it.printStackTrace() // 에러 처리
                }
                .addOnCompleteListener {
                    imageProxy.close() // 이미지 리소스 해제
                }
        } else {
            imageProxy.close()
        }
    }
}
