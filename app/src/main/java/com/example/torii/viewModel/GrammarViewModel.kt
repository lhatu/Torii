package com.example.torii.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.R
import com.example.torii.model.Grammar
import com.example.torii.model.GrammarExample
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class GrammarViewModel(application: Application) : AndroidViewModel(application) {

    private val _allGrammar = MutableStateFlow<List<Grammar>>(emptyList())
    private val _filteredGrammar = MutableStateFlow<List<Grammar>>(emptyList())
    val filteredGrammarList: StateFlow<List<Grammar>> = _filteredGrammar

    init {
        loadGrammarFromJson()
    }

    private fun loadGrammarFromJson() {
        viewModelScope.launch {
            val context = getApplication<Application>().applicationContext
            val inputStream = context.resources.openRawResource(R.raw.grammar)
            val jsonString = inputStream.bufferedReader().use { it.readText() }

            val jsonArray = JSONArray(jsonString) // Sử dụng JSONArray thay vì JSONObject

            val grammarList = mutableListOf<Grammar>()

            // Duyệt qua mảng
            for (i in 0 until jsonArray.length()) {
                val grammarData = jsonArray.getJSONObject(i)
                val phrase = grammarData.getString("phrase")
                val structure = grammarData.getString("structure")
                val meaning = grammarData.getString("meaning")
                val explanation = grammarData.getString("explanation")
                val jlptLevel = grammarData.getString("jlptLevel")

                // Duyệt qua ví dụ
                val examplesArray = grammarData.getJSONArray("examples")
                val examples = List(examplesArray.length()) { j ->
                    val ex = examplesArray.getJSONObject(j)
                    GrammarExample(
                        sentence = ex.getString("sentence"),
                        translation = ex.getString("translation")
                    )
                }

                grammarList.add(
                    Grammar(
                        phrase = phrase,
                        structure = structure,
                        meaning = meaning,
                        explanation = explanation,
                        jlptLevel = jlptLevel,
                        examples = examples
                    )
                )
            }

            _allGrammar.value = grammarList
            _filteredGrammar.value = grammarList
        }
    }

    private fun getAllGrammar(): List<Grammar> {
        return _allGrammar.value
    }

    private fun getGrammarByJlptLevel(level: String): List<Grammar> {
        return _allGrammar.value.filter { it.jlptLevel == level }
    }

    private fun searchGrammarInDatabase(query: String): List<Grammar> {
        return _allGrammar.value.filter { grammar ->
            grammar.phrase.contains(query, ignoreCase = true) ||
                    grammar.meaning.contains(query, ignoreCase = true)
        }
    }

    fun searchGrammar(query: String) {
        viewModelScope.launch {
            val results = if (query.isEmpty()) {
                getAllGrammar()
            } else {
                searchGrammarInDatabase(query)
            }
            _filteredGrammar.value = results
        }
    }

    fun filterByJlpt(level: String) {
        viewModelScope.launch {
            val filteredList = if (level == "All") {
                getAllGrammar()
            } else {
                getGrammarByJlptLevel(level)
            }
            _filteredGrammar.value = filteredList
        }
    }
}
