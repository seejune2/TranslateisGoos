package com.ksj.translateisgoos

import CameraEvent
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.Display
import android.view.Surface
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.korean.KoreanTextRecognizerOptions
import com.ksj.translateisgoos.ui.theme.TranslateisGoosTheme
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
    onTextDetected: (String) -> Unit,
    context: Context,
    imageCapture: ImageCapture,
    selectedMode: CameraMode
) {

    //var isDetected = false
    val lensFacing = CameraSelector.LENS_FACING_BACK
    val lifecycleOwner = LocalLifecycleOwner.current
    val preview = Preview.Builder().build()
    val previewView = remember {
        PreviewView(context)
    }
    val cameraxSelector = CameraSelector.Builder().requireLensFacing(lensFacing).build()
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
        try {
            cameraProvider.bindToLifecycle(
                lifecycleOwner,
                cameraxSelector,
                preview,
                imageCapture,
                imageAnalyzer
            )
            preview.setSurfaceProvider(previewView.surfaceProvider)
        } catch (exc: Exception) {
            // Log or handle exception
        }
    }
    AndroidView(factory = { previewView }, modifier = Modifier.fillMaxSize())

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
    var selectedMode by remember { mutableStateOf(CameraMode.REALTIME) }
    val localcontext = LocalContext.current
    val intent =
        (localcontext as? ImageTranslate)?.intent // 현재 context가 ImageTranslate인 경우에만 intent를 가져옴
    // val intent = Intent(context, ImageTranslate::class.java) // 새로운 Intent 생성 현재 Intent가 아님 그래서 get못해옴.
    val sourceLanguage = intent?.getStringExtra("sourceLanguage") ?: TranslateLanguage.KOREAN
    val targetLanguage = intent?.getStringExtra("targetLanguage") ?: TranslateLanguage.ENGLISH

    val translator = remember(sourceLanguage, targetLanguage) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        Translation.getClient(options)
    }
    val displayManager = localcontext.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
    val rotation =
        displayManager.getDisplay(Display.DEFAULT_DISPLAY)?.rotation ?: Surface.ROTATION_90

    var imageTransText: String by remember { mutableStateOf<String>("") }
    val onTextTranslated: (String) -> Unit = { text ->
        translator.downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener {
                translator.translate(text)
                    .addOnSuccessListener { translated ->
                        imageTransText = translated
                    }
            }
    }


    var detectedText: String by remember { mutableStateOf("") }
    var translatedText: String by remember { mutableStateOf("") }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setTargetRotation(rotation)
            .build()
    }


    CameraPreviewScreen(
        onTextDetected = { text ->
            detectedText = text
        }, context = localcontext,
        imageCapture = imageCapture,
        selectedMode = CameraMode.REALTIME
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        horizontalAlignment = Alignment.CenterHorizontally,
    )
    {
        Text("$selectedMode", style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold))
        if (selectedMode == CameraMode.REALTIME) {
            if (translatedText.isNotEmpty()) {
                Text(
                    text = translatedText,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    style = TextStyle(fontSize = 20.sp, color = Color.White)
                )
            }
            imageTransText = ""
        } else {
            Text(
                text = imageTransText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                style = TextStyle(fontSize = 20.sp, color = Color.White)
            )
            translatedText = ""
        }
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        if (selectedMode == CameraMode.CAPTURE) {
            IconButton(
                onClick = {
                    CameraEvent.OnTakePhotoClick(imageCapture, localcontext, onTextTranslated)
                        .takePhoto()
                },
                modifier = Modifier.padding(bottom = 20.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    Icon(
                        Icons.Rounded.Favorite,
                        contentDescription = "촬영",
                        tint = Color.Red,
                        modifier = Modifier.size(200.dp)
                    )

                }
            }
        }
        CameraModeButtons(selectedMode) { mode ->
            selectedMode = mode
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
                    if (mode == selectedMode) Color.Transparent else Color.Transparent
                ),
                shape = RectangleShape,
                modifier = Modifier
                    .width(200.dp)
                    .height(70.dp)
            )
            {
                Text(text = mode.name)
            }

        }
    }
}

enum class CameraMode {
    REALTIME,
    CAPTURE
}