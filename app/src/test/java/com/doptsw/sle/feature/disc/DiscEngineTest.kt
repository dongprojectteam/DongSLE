package com.doptsw.sle.feature.disc

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DiscEngineTest {
    @Test
    fun calculateScore_appliesMostPlusLeastMinus() {
        val questions = listOf(
            DiscQuestion(1, listOf(DiscOption("a", DiscType.D), DiscOption("b", DiscType.I), DiscOption("c", DiscType.S), DiscOption("d", DiscType.C))),
            DiscQuestion(2, listOf(DiscOption("a", DiscType.D), DiscOption("b", DiscType.I), DiscOption("c", DiscType.S), DiscOption("d", DiscType.C)))
        )
        val answers = listOf(
            DiscAnswer(questionId = 1, mostIndex = 0, leastIndex = 1),
            DiscAnswer(questionId = 2, mostIndex = 1, leastIndex = 3)
        )

        val score = DiscEngine.calculateScore(questions, answers)

        assertEquals(1, score.d)
        assertEquals(0, score.i)
        assertEquals(0, score.s)
        assertEquals(-1, score.c)
    }

    @Test
    fun selectingSameOption_clearsOppositeSelection() {
        val base = DiscAnswer(questionId = 1, mostIndex = 2, leastIndex = 1)
        val next = DiscEngine.selectLeast(base, 2)
        assertNull(next.mostIndex)
        assertEquals(2, next.leastIndex)
    }

    @Test
    fun coordinate_isCalculatedByTwoAxisFormula() {
        val coordinate = DiscEngine.toCoordinate(DiscScore(d = 12, i = 10, s = 6, c = 4))
        assertEquals(12, coordinate.x)
        assertEquals(0, coordinate.y)
    }

    @Test
    fun interpretation_onRightCenter_isDiBlend() {
        val interpretation = DiscEngine.interpretation(DiscScore(d = 12, i = 10, s = 6, c = 4))
        assertEquals("DI", interpretation.typeCode)
    }

    @Test
    fun interpretation_strengthLabel_usesDistanceRule() {
        val weak = DiscEngine.interpretation(DiscScore(d = 1, i = 1, s = 0, c = 0))
        val strong = DiscEngine.interpretation(DiscScore(d = 20, i = 0, s = -20, c = 0))

        assertTrue(weak.summary.contains("약한 혼합"))
        assertTrue(strong.summary.contains("강한 유형"))
    }
}
