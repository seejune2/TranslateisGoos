package com.ksj.translateisgoos

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
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
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 상단 언어 변경 메뉴
        Row {
            Button(onClick = { inputExpanded = !inputExpanded }) {
                Text(inputLanguage)
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

                            }
                    )
                }

            }
            Button(onClick = {
                var temp = inputLanguage
                isSource = !isSource
                isTarget = !isTarget
                if (isSource) {
                    inputLanguage = outputLanguage
                    outputLanguage = temp
                } else {
                    inputLanguage = outputLanguage
                    outputLanguage = temp
                }

            }) {
                Text("언어변경")
            }
            Button(onClick = { outputExpanded = !outputExpanded }) {
                Text(outputLanguage)
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

                            }
                    )
                }
            }


        }
        // 번역할 텍스트
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(400.dp)
        )

        // 번역된 텍스트
        Box() {
            Text("번역 : ${newText}")

        }

        // 번역 버튼(비동기)
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
                translator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        newText = translatedText
                    }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(5.dp),
            shape = RectangleShape,
            enabled = isReady
        ) {
            Text("번역")
        }
        Button(
            onClick = {
                val locale = getLocaleFromLanguage(outputLanguage)
                tts.language = locale
                tts.speak(newText, TextToSpeech.QUEUE_FLUSH, null, null)
            },
            enabled = newText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(5.dp),
            shape = RectangleShape
        ) {
            Text("번역 읽어줭")
        }

        // 이미지 번역(화면전환)
        Button(
            onClick = {
                val intent = Intent(context, ImageTranslate::class.java).apply {
                    putExtra("sourceLanguage", sourceLanguage)
                    putExtra("targetLanguage", targetLanguage)
                }
//                Log.d("MainActivity", "Starting ImageTranslate activity")
//                Log.d("MainActivity", "sourceLanguage: $sourceLanguage")
//                Log.d("MainActivity", "targetLanguage: $targetLanguage")

                context.startActivity(intent)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
                .padding(5.dp),
            shape = RectangleShape,
            enabled = isReady
        ) {
            Text("이미지 번역")
        }


    }

}

fun getLocaleFromLanguage(language: String): Locale {
    return when (language.lowercase()) {
        "korean" -> Locale.KOREAN
        "english" -> Locale.ENGLISH
        "french" -> Locale.FRENCH
        "german" -> Locale.GERMAN
        "japanese" -> Locale.JAPANESE
        "italian" -> Locale.ITALIAN
        "spanish" -> Locale("es", "ES")
        "chinese" -> Locale.CHINESE
        else -> Locale(language) // 기본적으로 언어 코드로 Locale 생성
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TranslateisGoosTheme {
        MainScreen()
    }
}
