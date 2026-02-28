package com.doptsw.sle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "diary_entries")
data class DiaryEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val entryDate: String,
    val situation: String,
    val feeling: String,
    val thought: String,
    val desiredAction: String,
    val createdAt: Long,
    val updatedAt: Long
)
