//package com.ksj.translateisgoos
//
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Button
//import androidx.compose.material3.DropdownMenu
//import androidx.compose.material3.Text
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import com.google.mlkit.nl.translate.TranslateLanguage
//import java.util.Locale
//
//
//class Language {
//
//
//    var expanded by remember { mutableStateOf(false) }
//    val supportedLanguages = TranslateLanguage.getAllLanguages()
//    val languageList = supportedLanguages.map { languageCode ->
//        Pair(languageCode, Locale(languageCode).displayLanguage)
//    }
//    Column(
//    modifier = Modifier.fillMaxSize(),
//    horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
//    verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
//    )
//    {
//        Button(onClick = { expanded = !expanded }) {
//            Text(text = "Select Language")
//        }
//        DropdownMenu(
//            expanded = expanded,
//            onDismissRequest = { expanded = false },
//        ) {
//            Text(
//                text = "Language",
//                modifier = Modifier.padding(16.dp)
//            )
//            languageList.forEach { (languageCode, languageName) ->
//                Text(
//                    text = "$languageCode - $languageName",
//                    modifier = Modifier
//                        .padding(16.dp)
//                        .clickable {
//
//
//                        }
//                )
//
//            }
//
//        }
//    }
//}