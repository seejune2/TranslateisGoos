package com.ksj.translateisgoos

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.ksj.translateisgoos.ui.theme.TranslateisGoosTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val supportedLanguages = TranslateLanguage.getAllLanguages()
        val languageList = supportedLanguages.map { languageCode ->
            Pair(languageCode, Locale(languageCode).displayLanguage)
        }

        enableEdgeToEdge()
        setContent {
            TranslateisGoosTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    val context = LocalContext.current
    var isSource by remember { mutableStateOf(true) }
    var isTarget by remember { mutableStateOf(false) }
    var inputLanguage by remember { mutableStateOf(TranslateLanguage.KOREAN) }
    var outputLanguage by remember { mutableStateOf(TranslateLanguage.ENGLISH) }
    var inputButtonText by remember { mutableStateOf("한국어") }
    var outputButtonText by remember { mutableStateOf("영어") }


    val supportedLanguages = TranslateLanguage.getAllLanguages()
    val languageList = supportedLanguages.map { languageCode ->
        Pair(languageCode, Locale(languageCode).displayLanguage)
    }
    val sourceLanguage = inputLanguage
    val targetLanguage = outputLanguage
    val translator = remember(sourceLanguage, targetLanguage) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(sourceLanguage)
            .setTargetLanguage(targetLanguage)
            .build()
        Translation.getClient(options)
    }

    val tts = remember {
        TextToSpeech(context) { status ->
            if (status != TextToSpeech.SUCCESS) {
                // Handle initialization error.
            }
        }
    }

    // 언어 다운
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(translator) {
        val conditions = DownloadConditions.Builder()
//        .requireWifi()
            .build()
        translator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                isReady = true
            }
    }

    var inputExpanded by remember { mutableStateOf(false) }
    var outputExpanded by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var newText by remember { mutableStateOf("") }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues())
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        // 상단 언어 변경 메뉴
        Row {
            Button(
                onClick = { inputExpanded = !inputExpanded }, modifier = Modifier.weight(1f),
                shape = RectangleShape, colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text(
                    inputButtonText,
                    style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextStyle(fontSize = 25.sp).fontSize
                    )
                )
            }
            DropdownMenu(
                expanded = inputExpanded,
                onDismissRequest = { inputExpanded = false },
            ) {
                Text(
                    text = "Language",
                    modifier = Modifier.padding(16.dp)
                )
                languageList.forEach { (languageCode, languageName) ->
                    Text(
                        text = "$languageCode - $languageName",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                inputLanguage = languageCode
                                inputButtonText = languageName.uppercase()
                                inputExpanded = false
                            }
                    )
                }
            }
            IconButton(
                onClick = {
                    val temp = inputLanguage
                    val temp2 = inputButtonText
                    isSource = !isSource
                    isTarget = !isTarget
                    if (isSource) {
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                        inputButtonText = outputButtonText
                        outputButtonText = temp2
                    } else {
                        inputLanguage = outputLanguage
                        outputLanguage = temp
                        inputButtonText = outputButtonText
                        outputButtonText = temp2
                    }
                }, modifier = Modifier.weight(1f)
            ) {
                Icon(
                    painter = painterResource(R.drawable.repeat),
                    contentDescription = "repeat",
                    modifier = Modifier.size(50.dp),
                    tint = Color.Unspecified
                )
            }


            Button(
                onClick = { outputExpanded = !outputExpanded }, modifier = Modifier.weight(1f),
                shape = RectangleShape,
                colors = ButtonDefaults.buttonColors(Color.Transparent)
            ) {
                Text(
                    outputButtonText,
                    style = TextStyle(
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = TextStyle(fontSize = 25.sp).fontSize
                    )
                )


            }
            DropdownMenu(
                expanded = outputExpanded,
                onDismissRequest = { outputExpanded = false },

                ) {
                Text(
                    text = "Language",
                    modifier = Modifier.padding(16.dp)
                )
                languageList.forEach { (languageCode, languageName) ->
                    Text(
                        text = "$languageCode - $languageName",
                        modifier = Modifier
                            .padding(16.dp)
                            .clickable {
                                outputLanguage = languageCode
                                outputButtonText = languageName.uppercase()
                                outputExpanded = false
                            },
                    )
                }
            }


        }

        // 번역할 텍스트
        TextField(
            value = text,
            textStyle = TextStyle(fontSize = 20.sp),
            onValueChange = { text = it },
            modifier = Modifier
                .padding(top = 10.dp, start = 10.dp, end = 10.dp)
                .fillMaxWidth()
                .weight(1f),
            colors = OutlinedTextFieldDefaults.colors(unfocusedContainerColor = Color.White),
        )
        // 번역된 텍스트
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                .weight(1f)
                .background(color = Color.White)
        ) {
            Text(
                newText,
                style = TextStyle(
                    fontSize = 30.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier.padding(top = 10.dp, start = 10.dp)
            )

        }
        // 번역 버튼(비동기)
        TransButton("번역", enabled = isReady, onClick = {
            translator.translate(text)
                .addOnSuccessListener { translatedText ->
                    newText = translatedText
                }
        })

        TransButton("번역 읽어줭", enabled = newText.isNotBlank(), onClick = {
            val locale = GetLocaleFromLanguage.getLocaleFromLanguage(outputLanguage.uppercase())
            tts.language = locale
            tts.speak(newText, TextToSpeech.QUEUE_FLUSH, null, null)
        })

        // 이미지 번역(화면전환)
        TransButton("이미지 번역", enabled = isReady, onClick = {
            val intent = Intent(context, ImageTranslate::class.java).apply {
                putExtra("sourceLanguage", sourceLanguage)
                putExtra("targetLanguage", targetLanguage)
            }
            context.startActivity(intent)
        })
    }
}

@Composable
fun TransButton(text: String, onClick: () -> Unit, enabled: Boolean) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(1.dp),
        shape = RectangleShape,
        enabled = enabled,
        colors = ButtonDefaults.buttonColors(Color(0xFF009688))
    ) {
        Text(
            text,
            style = TextStyle(color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        )
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TranslateisGoosTheme {
        MainScreen()
    }
}
