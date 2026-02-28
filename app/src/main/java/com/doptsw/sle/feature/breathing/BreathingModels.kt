package com.doptsw.sle.feature.breathing

enum class BreathingPhase {
    EXHALE,
    HOLD_AFTER_EXHALE,
    INHALE,
    HOLD_AFTER_INHALE
}

enum class BreathingSoundType {
    EXHALE_EDGE,
    HOLD_EXHALE_EDGE,
    INHALE_EDGE,
    HOLD_INHALE_EDGE
}

data class BreathingSetupUiState(
    val rounds: Int = 8
)

data class BreathingSessionUiState(
    val rounds: Int = 8,
    val completedRounds: Int = 0,
    val tickInCycle: Int = 0,
    val phase: BreathingPhase = BreathingPhase.EXHALE,
    val remainingSecondsInPhase: Int = 4,
    val isRunning: Boolean = true,
    val isPaused: Boolean = false,
    val isCompleted: Boolean = false,
    val soundEnabled: Boolean = true,
    val edgeSignal: Int = 0
) {
    val currentRound: Int
        get() = if (isCompleted) rounds else (completedRounds + 1).coerceAtMost(rounds)

    val totalProgress: Float
        get() {
            val totalTicks = rounds * BreathingEngine.CYCLE_TICKS
            val done = completedRounds * BreathingEngine.CYCLE_TICKS + tickInCycle
            return if (totalTicks == 0) 0f else done.toFloat() / totalTicks.toFloat()
        }
}
