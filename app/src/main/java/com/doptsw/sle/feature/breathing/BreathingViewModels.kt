package com.doptsw.sle.feature.breathing

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BreathingSetupViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(BreathingSetupUiState())
    val uiState: StateFlow<BreathingSetupUiState> = _uiState.asStateFlow()

    fun setRounds(value: Int) {
        _uiState.value = _uiState.value.copy(rounds = BreathingEngine.clampRounds(value))
    }

    fun increaseRounds() {
        setRounds(_uiState.value.rounds + 1)
    }

    fun decreaseRounds() {
        setRounds(_uiState.value.rounds - 1)
    }
}

class BreathingSessionViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    private val roundsArg = savedStateHandle.get<String>("rounds")?.toIntOrNull() ?: 8
    private val rounds = BreathingEngine.clampRounds(roundsArg)

    private val _uiState = MutableStateFlow(
        BreathingSessionUiState(
            rounds = rounds,
            phase = BreathingEngine.phaseAtTick(0),
            remainingSecondsInPhase = BreathingEngine.remainingSecondsInPhaseAtTick(0)
        )
    )
    val uiState: StateFlow<BreathingSessionUiState> = _uiState.asStateFlow()

    private var sessionJob: Job? = null

    init {
        startLoop()
    }

    fun togglePause() {
        _uiState.value = _uiState.value.copy(isPaused = !_uiState.value.isPaused)
    }

    fun toggleSound() {
        _uiState.value = _uiState.value.copy(soundEnabled = !_uiState.value.soundEnabled)
    }

    fun stopSession() {
        sessionJob?.cancel()
        _uiState.value = _uiState.value.copy(isRunning = false)
    }

    fun restartSession() {
        sessionJob?.cancel()
        _uiState.value = BreathingSessionUiState(
            rounds = rounds,
            phase = BreathingEngine.phaseAtTick(0),
            remainingSecondsInPhase = BreathingEngine.remainingSecondsInPhaseAtTick(0)
        )
        startLoop()
    }

    private fun startLoop() {
        sessionJob = viewModelScope.launch {
            while (_uiState.value.isRunning && !_uiState.value.isCompleted) {
                delay(100)
                val current = _uiState.value
                if (current.isPaused || current.isCompleted) continue

                var nextTick = current.tickInCycle + 1
                var nextCompletedRounds = current.completedRounds
                var completed = false
                var edgeSignal = current.edgeSignal

                if (nextTick % BreathingEngine.PHASE_TICKS == 0) {
                    edgeSignal += 1
                }

                if (nextTick >= BreathingEngine.CYCLE_TICKS) {
                    nextCompletedRounds += 1
                    if (nextCompletedRounds >= current.rounds) {
                        completed = true
                        nextTick = BreathingEngine.CYCLE_TICKS - 1
                    } else {
                        nextTick = 0
                    }
                }

                val phase = BreathingEngine.phaseAtTick(nextTick)
                _uiState.value = current.copy(
                    completedRounds = nextCompletedRounds,
                    tickInCycle = nextTick,
                    phase = phase,
                    remainingSecondsInPhase = BreathingEngine.remainingSecondsInPhaseAtTick(nextTick),
                    isCompleted = completed,
                    isRunning = !completed,
                    edgeSignal = edgeSignal
                )
            }
        }
    }

    override fun onCleared() {
        sessionJob?.cancel()
        super.onCleared()
    }
}
