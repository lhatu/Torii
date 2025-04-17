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

    fun loadAllVocabulary(onLoaded: (() -> Unit)? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val inputStream = getApplication<Application>().resources.openRawResource(R.raw.n5)
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<Vocabulary>>() {}.type
                fullList = Gson().fromJson(reader, type)

                withContext(Dispatchers.Main) {
                    _vocabList.value = fullList
                    onLoaded?.invoke()  // Gọi callback nếu có
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadRandomFive() {
        if (::fullList.isInitialized) {
            val first100 = fullList.take(100) // Lấy 100 phần tử đầu tiên
            _vocabList.value = first100.shuffled().take(5) // Xáo trộn và lấy 5 phần tử
        }
    }

    fun loadAll() {
        if (::fullList.isInitialized) {
            _vocabList.value = fullList
        }
    }
}
