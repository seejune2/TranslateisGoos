package com.ksj.translateisgoos

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.material3.Scaffold
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

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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


    // 한국어 -> 영어 번역
    val KoEnTranslator = remember {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        Translation.getClient(options)
    }
    // 영어 -> 한국어 번역
    val EnKoTranslator = remember {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.KOREAN)
            .build()
        Translation.getClient(options)
    }
    // 언어 다운
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(KoEnTranslator) {
        val conditions = DownloadConditions.Builder()
//        .requireWifi()
            .build()
        KoEnTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                isReady = true
            }
    }


    var text by remember { mutableStateOf("") }
    var newText by remember { mutableStateOf("") }
    var isKorean by remember { mutableStateOf(true) }
    var isEnglish by remember { mutableStateOf(false) }
    var inputLanguage by remember { mutableStateOf("한국어") }
    var outputLanguage by remember { mutableStateOf("영어") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // 상단 언어 변경 메뉴
        Row {
            Spacer(modifier = Modifier.weight(1f))
            Text(inputLanguage)
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                isKorean = !isKorean
                isEnglish = !isEnglish
                if (isKorean) {
                    inputLanguage = "한국어"
                    outputLanguage = "영어"
                } else {
                    inputLanguage = "영어"
                    outputLanguage = "한국어"
                }

            }) {
                Text("언어변경")
            }
            Spacer(modifier = Modifier.weight(1f))
            Text(outputLanguage)
            Spacer(modifier = Modifier.weight(1f))
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
                if (isKorean) {
                    KoEnTranslator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            newText = translatedText
                        }
                } else {
                    EnKoTranslator.translate(text)
                        .addOnSuccessListener { translatedText ->
                            newText = translatedText
                        }
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

        // 이미지 번역(화면전환)
        Button(
            onClick = {
                var intent = Intent(context, ImageTranslate::class.java)
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

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TranslateisGoosTheme {
        MainScreen()
    }
}
