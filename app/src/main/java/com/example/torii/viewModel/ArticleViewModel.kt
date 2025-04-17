package com.example.torii.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.R
import com.example.torii.model.Article
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader

class ArticleViewModel(application: Application) : AndroidViewModel(application) {

    private val _articles = MutableStateFlow<List<Article>>(emptyList())
    val articles: StateFlow<List<Article>> = _articles

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadArticlesFromRaw()
    }

    private fun loadArticlesFromRaw() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val context = getApplication<Application>().applicationContext
                val inputStream = context.resources.openRawResource(R.raw.article)
                val json = inputStream.bufferedReader().use(BufferedReader::readText)

                val type = object : TypeToken<List<Article>>() {}.type
                val articleList: List<Article> = Gson().fromJson(json, type)

                _articles.value = articleList
            } catch (e: Exception) {
                e.printStackTrace()
            }
            finally {
                _isLoading.value = false
            }
        }
    }
}


