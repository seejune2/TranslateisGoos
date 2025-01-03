package com.ksj.translateisgoos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
    val KoEnTranslator = remember {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.KOREAN)
            .setTargetLanguage(TranslateLanguage.ENGLISH)
            .build()
        Translation.getClient(options)
    }
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
    var textCount by remember { mutableStateOf(0) }
    var newText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(WindowInsets.systemBars.asPaddingValues()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = text,
            onValueChange = { text = it },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .height(400.dp)
        )
        Box() {
            Text("번역 : ${newText}")

        }
        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = {
//                newText = text.length.toString()
//                text = ""
                KoEnTranslator.translate(text)
                    .addOnSuccessListener { translatedText ->
                        newText = translatedText
                    }
                    .addOnFailureListener { exception ->
                        // Error.
                        // ...
                    }

            },
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            shape = RectangleShape,
            enabled = isReady
        ) {
            Text("번역")
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
