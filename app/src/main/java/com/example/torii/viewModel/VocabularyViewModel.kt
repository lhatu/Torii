package com.example.torii.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.torii.R
import com.example.torii.model.Vocabulary
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class VocabularyViewModel(application: Application) : AndroidViewModel(application) {

    private val _vocabList = MutableLiveData<List<Vocabulary>>()
    val vocabList: LiveData<List<Vocabulary>> = _vocabList

    private lateinit var fullList: List<Vocabulary>

    fun loadAllVocabulary(onLoaded: () -> Unit = {}) {
        viewModelScope.launch(Dispatchers.IO) {
            if (!::fullList.isInitialized) {
                // Load data từ JSON
                val inputStream = getApplication<Application>().resources.openRawResource(R.raw.n5)
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<Vocabulary>>() {}.type
                fullList = Gson().fromJson(reader, type)

                withContext(Dispatchers.Main) {
                    _vocabList.value = fullList
                    onLoaded() // Gọi callback khi load xong
                }
            } else {
                onLoaded() // Đã có data sẵn
            }
        }
    }

    fun loadRandomFive() {
        if (::fullList.isInitialized) {
            val first100 = fullList.take(100) // Lấy 100 phần tử đầu tiên
            _vocabList.value = first100.shuffled().take(5) // Xáo trộn và lấy 5 phần tử
        }
    }

    fun searchVocabulary(keyword: String): Vocabulary? {
        if (!::fullList.isInitialized) {
            println("Warning: Data not loaded!")
            return null
        }

        return fullList.firstOrNull { vocab ->
            vocab.expression.trim() == keyword.trim() ||
                    vocab.reading.trim() == keyword.trim()
        }.also { result ->
            if (result == null) {
                println("""
                [DEBUG] Word '$keyword' not found!
                Available expressions: ${fullList.map { it.expression }}
            """.trimIndent())
            }
        }
    }
}
