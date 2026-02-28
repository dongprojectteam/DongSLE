package com.doptsw.sle.feature.breathing

object BreathingEngine {
    const val PHASE_SECONDS = 4
    const val TICKS_PER_SECOND = 10
    const val CYCLE_SECONDS = PHASE_SECONDS * 4
    const val PHASE_TICKS = PHASE_SECONDS * TICKS_PER_SECOND
    const val CYCLE_TICKS = CYCLE_SECONDS * TICKS_PER_SECOND
    const val MIN_ROUNDS = 1
    const val MAX_ROUNDS = 30

    fun clampRounds(rounds: Int): Int = rounds.coerceIn(MIN_ROUNDS, MAX_ROUNDS)

    fun phaseAt(secondInCycle: Int): BreathingPhase {
        val normalized = secondInCycle.coerceIn(0, CYCLE_SECONDS - 1)
        return when (normalized / PHASE_SECONDS) {
            0 -> BreathingPhase.EXHALE
            1 -> BreathingPhase.HOLD_AFTER_EXHALE
            2 -> BreathingPhase.INHALE
            else -> BreathingPhase.HOLD_AFTER_INHALE
        }
    }

    fun phaseAtTick(tickInCycle: Int): BreathingPhase {
        val normalized = tickInCycle.coerceIn(0, CYCLE_TICKS - 1)
        return when (normalized / PHASE_TICKS) {
            0 -> BreathingPhase.EXHALE
            1 -> BreathingPhase.HOLD_AFTER_EXHALE
            2 -> BreathingPhase.INHALE
            else -> BreathingPhase.HOLD_AFTER_INHALE
        }
    }

    fun remainingSecondsInPhase(secondInCycle: Int): Int {
        val normalized = secondInCycle.coerceIn(0, CYCLE_SECONDS - 1)
        val offsetInPhase = normalized % PHASE_SECONDS
        return PHASE_SECONDS - offsetInPhase
    }

    fun remainingSecondsInPhaseAtTick(tickInCycle: Int): Int {
        val normalized = tickInCycle.coerceIn(0, CYCLE_TICKS - 1)
        val offsetInPhase = normalized % PHASE_TICKS
        return ((PHASE_TICKS - offsetInPhase) + (TICKS_PER_SECOND - 1)) / TICKS_PER_SECOND
    }

    fun soundTypeForPhase(phase: BreathingPhase): BreathingSoundType {
        return when (phase) {
            BreathingPhase.EXHALE -> BreathingSoundType.EXHALE_EDGE
            BreathingPhase.HOLD_AFTER_EXHALE -> BreathingSoundType.HOLD_EXHALE_EDGE
            BreathingPhase.INHALE -> BreathingSoundType.INHALE_EDGE
            BreathingPhase.HOLD_AFTER_INHALE -> BreathingSoundType.HOLD_INHALE_EDGE
        }
    }

    fun cycleProgress(secondInCycle: Int): Float {
        return secondInCycle.coerceIn(0, CYCLE_SECONDS).toFloat() / CYCLE_SECONDS.toFloat()
    }

    fun cycleProgressTick(tickInCycle: Int): Float {
        return tickInCycle.coerceIn(0, CYCLE_TICKS).toFloat() / CYCLE_TICKS.toFloat()
    }

    fun perimeterPoint(progress: Float): Pair<Float, Float> {
        val p = ((progress % 1f) + 1f) % 1f
        return when {
            p < 0.25f -> {
                val t = p / 0.25f
                0f to (1f - t)
            }
            p < 0.5f -> {
                val t = (p - 0.25f) / 0.25f
                t to 0f
            }
            p < 0.75f -> {
                val t = (p - 0.5f) / 0.25f
                1f to t
            }
            else -> {
                val t = (p - 0.75f) / 0.25f
                (1f - t) to 1f
            }
        }
    }

    fun totalSeconds(rounds: Int): Int = clampRounds(rounds) * CYCLE_SECONDS
}
