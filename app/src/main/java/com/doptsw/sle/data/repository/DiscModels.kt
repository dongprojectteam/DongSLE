package com.doptsw.sle.data.repository

data class DiscAnswerSelection(
    val questionId: Int,
    val mostType: String,
    val leastType: String
)

data class DiscResultRecord(
    val id: Long,
    val answers: List<DiscAnswerSelection>,
    val d: Int,
    val i: Int,
    val s: Int,
    val c: Int,
    val topTypes: List<String>,
    val interpretationKey: String,
    val createdAt: Long
)

data class DiscResultDraft(
    val answers: List<DiscAnswerSelection>,
    val d: Int,
    val i: Int,
    val s: Int,
    val c: Int,
    val topTypes: List<String>,
    val interpretationKey: String
)
