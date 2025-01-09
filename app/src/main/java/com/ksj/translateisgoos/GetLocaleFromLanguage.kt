package com.ksj.translateisgoos

import java.util.Locale

enum class GetLocaleFromLanguage(val locale: Locale) {
    KOREAN(Locale("한국어","KOREA")),
    ENGLISH(Locale("영어","ENGLISH")),
    FRENCH(Locale.FRENCH),
    GERMAN(Locale.GERMAN),
    JAPANESE(Locale.JAPANESE),
    ITALIAN(Locale.ITALIAN),
    SPANISH(Locale("es", "ES")),
    CHINESE(Locale.CHINESE),
    ARABIC(Locale("ar", "SA")),
    HINDI(Locale("hi", "IN")),
    RUSSIAN(Locale("ru", "RU")),
    PORTUGUESE(Locale("pt", "PT")),
    DUTCH(Locale("nl", "NL")),
    SWEDISH(Locale("sv", "SE")),
    DANISH(Locale("da", "DK")),
    NORWEGIAN(Locale("no", "NO")),
    FINNISH(Locale("fi", "FI")),
    TURKISH(Locale("tr", "TR")),
    THAI(Locale("th", "TH")),
    POLISH(Locale("pl", "PL")),
    INDONESIAN(Locale("id", "ID")),
    VIETNAMESE(Locale("vi", "VN")),
    TAGALOG(Locale("tl", "PH")),
    CZECH(Locale("cs", "CZ")),
    GREEK(Locale("el", "GR")),
    UKRAINIAN(Locale("uk", "UA")),
    HEBREW(Locale("iw", "IL"));

    companion object {
        fun getLocaleFromLanguage(language: String): Locale {
            return entries.find { it.name.equals(language, ignoreCase = true) }?.locale
                ?: Locale(language) // 기본적으로 언어 코드로 Locale 생성
        }
    }
}