package com.doptsw.sle.feature.decision

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DecisionValidationTest {
    @Test
    fun validation_fails_whenOptionsMissing() {
        val result = DecisionValidation.validate(DecisionEditUiState(optionA = "", optionB = "B"))
        assertFalse(result.valid)
    }

    @Test
    fun validation_fails_whenReasonsLessThanFive() {
        val state = DecisionEditUiState(
            optionA = "A",
            optionB = "B",
            reasonsA = listOf("1", "2", "3", "4"),
            reasonsB = listOf("1", "2", "3", "4", "5"),
            conclusion = "결론"
        )
        val result = DecisionValidation.validate(state)
        assertFalse(result.valid)
    }

    @Test
    fun validation_succeeds_whenAllConditionsMet() {
        val state = DecisionEditUiState(
            optionA = "A",
            optionB = "B",
            reasonsA = List(5) { "A$it" },
            reasonsB = List(5) { "B$it" },
            conclusion = "결론"
        )
        val result = DecisionValidation.validate(state)
        assertTrue(result.valid)
    }

    @Test
    fun alternatingList_buildsExpectedOrder() {
        val items = buildAlternatingReasonItems(
            reasonsA = listOf("a1", "a2", "a3"),
            reasonsB = listOf("b1", "b2")
        )
        assertEquals(5, items.size)
        assertEquals(ReasonTarget.A, items[0].target)
        assertEquals(ReasonTarget.B, items[1].target)
        assertEquals(ReasonTarget.A, items[2].target)
        assertEquals(ReasonTarget.B, items[3].target)
        assertEquals(ReasonTarget.A, items[4].target)
    }
}
