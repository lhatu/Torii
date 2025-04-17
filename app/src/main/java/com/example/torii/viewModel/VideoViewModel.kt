package com.example.torii.viewModel

import android.app.Application
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.compose.runtime.State
import com.example.torii.R
import com.example.torii.model.Video
import com.google.common.reflect.TypeToken
import com.google.gson.Gson

class VideoViewModel(application: Application) : AndroidViewModel(application) {

    var currentVideoId: String = ""
    var currentPosition: Float = 0f
    var currentVideo: Video? = null

    private val _videoList = mutableStateOf<List<Video>>(emptyList())
    val videoList: State<List<Video>> = _videoList

    init {
        loadVideosFromJson()
    }

    private fun loadVideosFromJson() {
        val context = getApplication<Application>().applicationContext
        val inputStream = context.resources.openRawResource(R.raw.video) // Đặt file json là res/raw/videos.json
        val jsonString = inputStream.bufferedReader().use { it.readText() }

        val gson = Gson()
        val type = object : TypeToken<List<Video>>() {}.type
        _videoList.value = gson.fromJson(jsonString, type)
    }

}