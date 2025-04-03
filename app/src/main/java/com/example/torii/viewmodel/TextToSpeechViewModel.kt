package com.example.torii.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import java.util.Locale

class TextToSpeechViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private var textToSpeech: TextToSpeech? = null

    init {
        textToSpeech = TextToSpeech(application, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            textToSpeech?.language = Locale.US  // Thiết lập ngôn ngữ tiếng Anh
        } else {
            Log.e("TTS", "Khởi tạo TextToSpeech thất bại!")
        }
    }

    fun speak(text: String) {
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
    }

    override fun onCleared() {
        super.onCleared()
        textToSpeech?.stop()
        textToSpeech?.shutdown()
    }
}