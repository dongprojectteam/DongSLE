package com.doptsw.sle.feature.breathing

import org.junit.Assert.assertEquals
import org.junit.Test

class BreathingSetupTest {
    @Test
    fun roundsClamp_worksAsExpected() {
        assertEquals(1, BreathingEngine.clampRounds(0))
        assertEquals(30, BreathingEngine.clampRounds(31))
        assertEquals(8, BreathingEngine.clampRounds(8))
    }
}
