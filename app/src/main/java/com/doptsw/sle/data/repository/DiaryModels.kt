package com.doptsw.sle.data.repository

data class DiaryEntry(
    val id: Long,
    val entryDate: String,
    val situation: String,
    val feeling: String,
    val thought: String,
    val desiredAction: String,
    val createdAt: Long,
    val updatedAt: Long
)

data class DiaryEntryDraft(
    val id: Long?,
    val entryDate: String,
    val situation: String,
    val feeling: String,
    val thought: String,
    val desiredAction: String
)

data class DiaryDateCount(
    val entryDate: String,
    val count: Int
)

data class DiarySearchItem(
    val id: Long,
    val entryDate: String,
    val preview: String
)
