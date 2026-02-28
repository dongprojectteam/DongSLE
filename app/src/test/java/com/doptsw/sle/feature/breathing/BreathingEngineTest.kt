package com.doptsw.sle.feature.breathing

import org.junit.Assert.assertEquals
import org.junit.Test

class BreathingEngineTest {
    @Test
    fun phaseSequence_isExhaleHoldInhaleHold() {
        assertEquals(BreathingPhase.EXHALE, BreathingEngine.phaseAt(0))
        assertEquals(BreathingPhase.HOLD_AFTER_EXHALE, BreathingEngine.phaseAt(4))
        assertEquals(BreathingPhase.INHALE, BreathingEngine.phaseAt(8))
        assertEquals(BreathingPhase.HOLD_AFTER_INHALE, BreathingEngine.phaseAt(12))
    }

    @Test
    fun totalSeconds_matchesRounds() {
        assertEquals(16, BreathingEngine.totalSeconds(1))
        assertEquals(128, BreathingEngine.totalSeconds(8))
    }

    @Test
    fun perimeterPoint_boundariesAreCorrect() {
        assertEquals(Pair(0f, 1f), BreathingEngine.perimeterPoint(0f))
        assertEquals(Pair(0f, 0f), BreathingEngine.perimeterPoint(0.25f))
        assertEquals(Pair(1f, 0f), BreathingEngine.perimeterPoint(0.5f))
        assertEquals(Pair(1f, 1f), BreathingEngine.perimeterPoint(0.75f))
    }
}
