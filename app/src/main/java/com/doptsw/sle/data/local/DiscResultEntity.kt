package com.doptsw.sle.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disc_results")
data class DiscResultEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,
    val answersJson: String,
    val d: Int,
    val i: Int,
    val s: Int,
    val c: Int,
    val topTypesCsv: String,
    val interpretationKey: String,
    val createdAt: Long
)
