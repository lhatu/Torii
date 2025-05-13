import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.torii.R
import com.example.torii.model.Lesson
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.InputStreamReader

class LessonViewModel(application: Application) : AndroidViewModel(application) {

    private val _lessons = MutableStateFlow<List<Lesson>>(emptyList())
    val lessons: StateFlow<List<Lesson>> = _lessons

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    init {
        loadLessonsFromJson()
    }

    private fun loadLessonsFromJson() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                val inputStream = getApplication<Application>().resources.openRawResource(R.raw.lessons)
                val reader = InputStreamReader(inputStream)
                val type = object : TypeToken<List<Lesson>>() {}.type
                _lessons.value = Gson().fromJson(reader, type) ?: emptyList()

                if (_lessons.value.isEmpty()) {
                    _errorMessage.value = "No lessons found"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load data: ${e.localizedMessage}"
                _lessons.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getLessonById(lessonId: String): Lesson? {
        return _lessons.value.firstOrNull { it.id == lessonId }
    }

    fun refreshData() {
        loadLessonsFromJson()
    }
}