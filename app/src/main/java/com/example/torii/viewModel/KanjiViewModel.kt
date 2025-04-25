package com.example.torii.viewModel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.R
import com.example.torii.model.Kanji
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject

class KanjiViewModel(application: Application) : AndroidViewModel(application) {

    private val _kanjiList = MutableStateFlow<List<Kanji>>(emptyList())
    private val _filteredKanjiList = MutableStateFlow<List<Kanji>>(emptyList())

    val kanjiList: StateFlow<List<Kanji>> = _kanjiList
    val filteredKanjiList: StateFlow<List<Kanji>> = _filteredKanjiList

    init {
        loadKanjiFromJson()
    }

    private fun loadKanjiFromJson() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val inputStream = context.resources.openRawResource(R.raw.kanji)
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            val jsonObject = JSONObject(jsonString)

            val kanjis = mutableListOf<Kanji>()

            for (key in jsonObject.keys()) {
                val character = key
                val data = jsonObject.getJSONObject(key)

                val onyomi = data.getJSONArray("readings_on").let { array ->
                    List(array.length()) { array.getString(it) }
                }

                val kunyomi = data.getJSONArray("readings_kun").let { array ->
                    List(array.length()) { array.getString(it) }
                }

                val strokes = data.getInt("strokes")
                val meaning = data.optJSONArray("meanings")?.let { array ->
                    if (array.length() > 0) array.getString(0) else ""
                } ?: ""
                val jlpt = data.optInt("jlpt_new", 0).toString()
                val example = ""

                kanjis.add(
                    Kanji(
                        character = character,
                        onyomi = onyomi,
                        kunyomi = kunyomi,
                        strokes = strokes,
                        meaning = meaning,
                        jlptLevel = "N$jlpt",
                        example = example
                    )
                )
            }

            _kanjiList.value = kanjis
            _filteredKanjiList.value = kanjis // Set mặc định là tất cả
        }
    }

    private fun getAllKanji(): List<Kanji> {
        return _kanjiList.value
    }

    private fun getKanjiByJlptLevel(level: String): List<Kanji> {
        return _kanjiList.value.filter { it.jlptLevel == level }
    }

    private fun searchKanjiInDatabase(query: String): List<Kanji> {
        return _kanjiList.value.filter { kanji ->
            kanji.character.contains(query, ignoreCase = true) ||
                    kanji.meaning.contains(query, ignoreCase = true) ||
                    kanji.onyomi.any { it.contains(query, ignoreCase = true) } ||
                    kanji.kunyomi.any { it.contains(query, ignoreCase = true) }
        }
    }

    fun filterByJlptLevel(level: String) {
        viewModelScope.launch {
            val filteredList = if (level == "All") {
                getAllKanji()
            } else {
                getKanjiByJlptLevel(level)
            }
            _filteredKanjiList.value = filteredList
        }
    }

    fun searchKanji(query: String) {
        viewModelScope.launch {
            val results = if (query.isEmpty()) {
                getAllKanji()
            } else {
                searchKanjiInDatabase(query)
            }
            _filteredKanjiList.value = results
        }
    }
}

