package com.doptsw.sle.feature.decision

enum class ReasonTarget {
    A,
    B
}

data class DecisionReasonItem(
    val target: ReasonTarget,
    val indexInTarget: Int,
    val displayIndex: Int
)

data class DecisionEditUiState(
    val id: Long? = null,
    val optionA: String = "",
    val optionB: String = "",
    val reasonsA: List<String> = List(3) { "" },
    val reasonsB: List<String> = List(3) { "" },
    val conclusion: String = "",
    val error: String? = null
) {
    val nextTarget: ReasonTarget
        get() = if (reasonsA.size == reasonsB.size) ReasonTarget.A else ReasonTarget.B
}

data class DecisionValidationResult(
    val valid: Boolean,
    val message: String? = null
)
