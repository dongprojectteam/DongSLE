package com.doptsw.sle.data.repository

data class DecisionRecord(
    val id: Long,
    val optionA: String,
    val optionB: String,
    val reasonsA: List<String>,
    val reasonsB: List<String>,
    val conclusion: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class DecisionDraft(
    val id: Long?,
    val optionA: String,
    val optionB: String,
    val reasonsA: List<String>,
    val reasonsB: List<String>,
    val conclusion: String
)
