package com.ksj.translateisgoos

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.ksj.translateisgoos.ui.theme.TranslateisGoosTheme
import kotlinx.coroutines.launch
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ImageTranslate : ComponentActivity() {
    private val permissionsRequest =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            ) {
                // 권한이 모두 허용됨
                // 카메라 관련 코드 구현
            } else {
                // 권한이 거부됨
                // 권한 요청 실패 시 처리
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED -> {
                // 권한이 모두 부여됨
                // 카메라 관련 코드 구현
            }

            else -> {
                // 권한이 없으면 여러 권한을 요청
                permissionsRequest.launch(
                    arrayOf(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                )
            }
        }
        setContent() {
            TranslateisGoosTheme() {
                ImageTranslateScreen()
            }
        }

    }
}

@Composable
fun CameraPreviewScreen(
    lensFacing: Int,
    lifecycleOwner: LifecycleOwner,
    context: Context,
    previewView: PreviewView,
    cameraExecutor: java.util.concurrent.Executor,
    onTextDetected: (String) -> Unit,
    onCapture: (String) -> Unit
) {
    //var isDetected = false
    val preview = Preview.Builder().build()

    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraProviders = cameraProviderFuture.get()

    val cameraExecutor = remember { java.util.concurrent.Executors.newSingleThreadExecutor() }

    //텍스트 인식 및 분석
    val textRecognizer =
        remember { TextRecognition.getClient(KoreanTextRecognizerOptions.Builder().build()) }


    val imageAnalyzer = remember {
        ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .apply {
                setAnalyzer(
                    ContextCompat.getMainExecutor(context),
                    ImageAnalyzer(textRecognizer, onTextDetected)
                )
            }
    }

    LaunchedEffect(lensFacing) {
        val cameraProvider = context.getCameraProvider()
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraxSelector, preview, imageAnalyzer)
        preview.setSurfaceProvider(previewView.surfaceProvider)
    }
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())
    LaunchedEffect(onCapture) {
        val imageCapture = ImageCapture.Builder()
            .setTargetRotation(previewView.display.rotation)
            .build()

        cameraProviders.bindToLifecycle(lifecycleOwner, cameraxSelector, imageCapture, preview)

    }
}


private suspend fun Context.getCameraProvider(): ProcessCameraProvider =
    suspendCoroutine { continuation ->
        ProcessCameraProvider.getInstance(this).also { cameraProvider ->
            cameraProvider.addListener({
                continuation.resume(cameraProvider.get())
            }, ContextCompat.getMainExecutor(this))
        }
    }


@Composable
fun ImageTranslateScreen() {
    val coroutineScope = rememberCoroutineScope()
    var selectedMode by remember { mutableStateOf(CameraMode.RIALTIME) }
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    val cameraExecutor = remember { java.util.concurrent.Executors.newSingleThreadExecutor() }

    val intent = (context as? ImageTranslate)?.intent
    val sourceLanguage = intent?.getStringExtra("sourceLanguage") ?: TranslateLanguage.KOREAN
    val targetLanguage = intent?.getStringExtra("targetLanguage") ?: TranslateLanguage.ENGLISH

    val translator = remember(sourceLanguage, targetLanguage) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        Translation.getClient(options)
    }

    var detectedText: String by remember { mutableStateOf("") } // 인식된 텍스트
    var translatedText: String by remember { mutableStateOf("") } // 번역된 텍스트(실시간)
    var captureTranslate: String by remember { mutableStateOf("") } // 번역된 텍스트(이미지)
    var cameraCapture: (suspend () -> Unit)? by remember { mutableStateOf(null) }

    LaunchedEffect(selectedMode) {
        if (selectedMode == CameraMode.CAPTURE) {
            cameraCapture = {
                val imageCapture = ImageCapture.Builder()
                    .setTargetRotation(previewView.display.rotation)
                    .build()

                val cameraProvider = context.getCameraProvider()
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(lifecycleOwner, CameraSelector.DEFAULT_BACK_CAMERA, imageCapture)

                val photoFile = File(
                    context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
                    "photo_${System.currentTimeMillis()}.jpg"
                )
                val outputFileOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()
                imageCapture.takePicture(outputFileOptions, cameraExecutor,
                    object : ImageCapture.OnImageSavedCallback {
                        override fun onError(error: ImageCaptureException) {
                            Log.e("ImageCapture", "Image capture failed: ${error.message}", error)
                        }

                        override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                            captureTranslate = photoFile.absolutePath // 이미지 경로를 onCapture로 전달
                        }
                    })
            }
        } else {
            cameraCapture = null
        }
    }

    // 실시간 번역 모드
    if (selectedMode == CameraMode.RIALTIME) {
        CameraPreviewScreen(
            lensFacing = CameraSelector.LENS_FACING_BACK,
            lifecycleOwner = lifecycleOwner,
            context = context,
            previewView = previewView,
            cameraExecutor = cameraExecutor,
            onTextDetected = { text ->
                detectedText = text
            },
            onCapture = {}
        )

        // 검출된 텍스트를 UI에 표시
        LaunchedEffect(detectedText) {
            if (detectedText.isNotEmpty()) {
                translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
                    .addOnSuccessListener {
                        translator.translate(detectedText)
                            .addOnSuccessListener { translated ->
                                translatedText = translated
                            }
                    }
            }
        }
    } else {
        CameraPreviewScreen(
            lensFacing = CameraSelector.LENS_FACING_BACK,
            lifecycleOwner = lifecycleOwner,
            context = context,
            previewView = previewView,
            cameraExecutor = cameraExecutor,
            onTextDetected = { },
            onCapture = {
                captureTranslate = it
            }
        )
    }




    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Text(text = "Selected Mode: ${selectedMode.name}")
        if (translatedText.isNotEmpty()) {
            Text(
                text = translatedText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = TextStyle(fontSize = 20.sp, color = Color.White)
            )
        } else {
            Text(
                text = captureTranslate,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = TextStyle(fontSize = 20.sp, color = Color.White)
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                coroutineScope.launch {
                    cameraCapture?.invoke()
                }

            },
            modifier = Modifier.padding(bottom = 20.dp)
        ) {
            Text(text = "캡쳐")

        }
        CameraModeButtons(selectedMode) { mode ->
            selectedMode = mode
            Log.d("selectedMode", selectedMode.toString())
        }

    }
}

@Composable
fun CameraModeButtons(selectedMode: CameraMode, onModeSelected: (CameraMode) -> Unit) {
    Row {
        CameraMode.entries.forEach { mode ->
            Button(
                onClick = { onModeSelected(mode) },
                colors = ButtonDefaults.buttonColors(
                    if (mode == selectedMode) Color.Blue else Color.Gray
                )
            )
            {
                Text(text = mode.name)
            }

        }
    }
}

enum class CameraMode {
    RIALTIME,
    CAPTURE
}