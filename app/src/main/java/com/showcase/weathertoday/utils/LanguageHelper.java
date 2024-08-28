package com.showcase.weathertoday.utils;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class LanguageHelper {
    public static String recuperarLang() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("ar", "ar");       // Árabe
        languageMap.put("bn", "bn");       // Bengali
        languageMap.put("bg", "bg");       // Búlgaro
        languageMap.put("zh", "zh");       // Chinês Simplificado
        languageMap.put("zh_tw", "zh_tw"); // Chinês Tradicional
        languageMap.put("cs", "cs");       // Checo
        languageMap.put("da", "da");       // Dinamarquês
        languageMap.put("nl", "nl");       // Holandês
        languageMap.put("fi", "fi");       // Finlandês
        languageMap.put("fr", "fr");       // Francês
        languageMap.put("de", "de");       // Alemão
        languageMap.put("el", "el");       // Grego
        languageMap.put("hi", "hi");       // Hindi
        languageMap.put("hu", "hu");       // Húngaro
        languageMap.put("it", "it");       // Italiano
        languageMap.put("ja", "ja");       // Japonês
        languageMap.put("jv", "jv");       // Javanês
        languageMap.put("ko", "ko");       // Coreano
        languageMap.put("zh_cmn", "zh_cmn"); // Mandarim
        languageMap.put("mr", "mr");       // Marathi
        languageMap.put("pl", "pl");       // Polonês
        languageMap.put("pt", "pt");       // Português
        languageMap.put("pa", "pa");       // Punjabi
        languageMap.put("ro", "ro");       // Romeno
        languageMap.put("ru", "ru");       // Russo
        languageMap.put("sr", "sr");       // Sérvio
        languageMap.put("si", "si");       // Cingalês
        languageMap.put("sk", "sk");       // Eslovaco
        languageMap.put("es", "es");       // Espanhol
        languageMap.put("sv", "sv");       // Sueco
        languageMap.put("ta", "ta");       // Tamil
        languageMap.put("te", "te");       // Telugu
        languageMap.put("tr", "tr");       // Turco
        languageMap.put("uk", "uk");       // Ucraniano
        languageMap.put("ur", "ur");       // Urdu
        languageMap.put("vi", "vi");       // Vietnamita
        languageMap.put("zh_wuu", "zh_wuu"); // Wu (Shanghainês)
        languageMap.put("zh_hsn", "zh_hsn"); // Xiang
        languageMap.put("zh_yue", "zh_yue"); // Yue (Cantonês)
        languageMap.put("zu", "zu");       // Zulu
        String lang;
        if (languageMap.containsKey(language)) {
            lang = languageMap.get(language);
        } else {
            lang = "en"; // Inglês será definido como idioma padrão caso o idioma do usuário não esteja nessa lista.
        }
        return lang;
    }
}
