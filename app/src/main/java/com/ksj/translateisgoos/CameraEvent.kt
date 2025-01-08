import android.content.ContentValues
import android.content.Context
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.core.content.ContextCompat
import java.text.SimpleDateFormat
import java.util.Locale

sealed class CameraEvent {
    object OnSwitchCameraClick : CameraEvent()
    data class OnTakePhotoClick(val imageCapture: ImageCapture, val context: Context) : CameraEvent() {
        fun takePhoto() {
            // Create timestamped name and ContentValues object
            val name = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(System.currentTimeMillis())
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "IMG_$name.jpg")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures") // 지정된 폴더로 저장
            }

            // Create output options object which contains file + metadata
            val outputOptions = ImageCapture.OutputFileOptions.Builder(
                context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            ).build()

            // Take a picture and save it to the provided location
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        Toast.makeText(context, "Photo saved successfully", Toast.LENGTH_SHORT).show()
                    }

                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                        Toast.makeText(context, "Failed to save photo", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
    }
}