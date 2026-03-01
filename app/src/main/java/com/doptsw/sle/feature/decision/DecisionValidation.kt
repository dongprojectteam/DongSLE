package com.doptsw.sle.feature.decision

object DecisionValidation {
    const val MIN_REQUIRED_REASONS = 3
    const val INITIAL_REQUIRED_PAIRS = 3

    fun validate(state: DecisionEditUiState): DecisionValidationResult {
        if (state.optionA.trim().isEmpty() || state.optionB.trim().isEmpty()) {
            return DecisionValidationResult(false, "두 선택지를 모두 입력해 주세요.")
        }
        val countA = state.reasonsA.count { it.trim().isNotEmpty() }
        val countB = state.reasonsB.count { it.trim().isNotEmpty() }
        if (countA < MIN_REQUIRED_REASONS || countB < MIN_REQUIRED_REASONS) {
            return DecisionValidationResult(
                false,
                "각 선택의 이유를 최소 ${MIN_REQUIRED_REASONS}개 이상 작성해 주세요."
            )
        }
        if (state.conclusion.trim().isEmpty()) {
            return DecisionValidationResult(false, "마지막 결론을 입력해 주세요.")
        }
        return DecisionValidationResult(true)
    }
}

fun buildAlternatingReasonItems(reasonsA: List<String>, reasonsB: List<String>): List<DecisionReasonItem> {
    val max = maxOf(reasonsA.size, reasonsB.size)
    val result = mutableListOf<DecisionReasonItem>()
    for (i in 0 until max) {
        if (i < reasonsA.size) {
            result += DecisionReasonItem(ReasonTarget.A, i, result.size + 1)
        }
        if (i < reasonsB.size) {
            result += DecisionReasonItem(ReasonTarget.B, i, result.size + 1)
        }
    }
    return result
}
