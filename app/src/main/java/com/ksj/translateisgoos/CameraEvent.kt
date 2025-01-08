import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import java.text.SimpleDateFormat
import java.util.Locale

sealed class CameraEvent {
    object OnSwitchCameraClick : CameraEvent()
    data class OnTakePhotoClick(val imageCapture: ImageCapture, val context: Context, val onTextTranslated: (String) -> Unit) : CameraEvent() {
        fun takePhoto() {
            // 타임스탬프 이름 및 ContentValues 객체 생성
            val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/TranslateImage") // 지정된 폴더로 저장
            }

            // 파일 + 메타데이터를 포함하는 출력 옵션 객체 생성
            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

            // 사진 찍기
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Toast.makeText(context, "캡쳐 성공", Toast.LENGTH_SHORT).show()
                        val uri = output.savedUri
                        if (uri != null){
                            val bitmap = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                            } else{
                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            }
                            recognizeAndTranslateText(bitmap)
                        }
                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                        Toast.makeText(context, "캡쳐 실패 ㅜㅜ", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
        private fun recognizeAndTranslateText(bitmap: Bitmap) {
            val textRecognizer = TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build())
            val inputImage = InputImage.fromBitmap(bitmap, 0)
            textRecognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val detectedText = visionText.text
                    onTextTranslated(detectedText)
                }
                .addOnFailureListener { e ->
                    e.printStackTrace()
                }
        }
    }
}