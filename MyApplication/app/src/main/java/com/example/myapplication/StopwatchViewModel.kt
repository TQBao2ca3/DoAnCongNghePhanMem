import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StopwatchViewModel : ViewModel() {
    private val _timeMillis = MutableStateFlow(0L)
    val timeMillis: StateFlow<Long> get() = _timeMillis

    private val _isRunning = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> get() = _isRunning

    private val _laps = MutableStateFlow<List<Long>>(emptyList())
    val laps: StateFlow<List<Long>> get() = _laps

    private var job: Job? = null

    fun toggleRunning() {
        if (_isRunning.value) stop() else start()
    }

    private fun start() {
        _isRunning.value = true
        job = viewModelScope.launch {
            while (_isRunning.value) {
                delay(1)
                _timeMillis.value += 1
            }
        }
    }

    private fun stop() {
        _isRunning.value = false
        job?.cancel()
    }

    fun reset() {
        _timeMillis.value = 0
        _laps.value = emptyList()
    }

    fun addLap() {
        _laps.value = _laps.value + _timeMillis.value
    }

    fun clearLaps() {
        _laps.value = emptyList()
    }
}

