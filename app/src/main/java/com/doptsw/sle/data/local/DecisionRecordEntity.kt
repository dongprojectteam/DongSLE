package com.doptsw.sle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "decision_records")
data class DecisionRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val optionA: String,
    val optionB: String,
    val reasonsAJson: String,
    val reasonsBJson: String,
    val conclusion: String,
    val createdAt: Long,
    val updatedAt: Long
)
