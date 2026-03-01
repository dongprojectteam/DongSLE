package com.doptsw.sle.data.repository

import kotlinx.coroutines.flow.Flow

interface DiaryRepository {
    fun observeAllEntriesDesc(): Flow<List<DiaryEntry>>
    fun observeDiaryDatesWithCount(monthStart: String, monthEnd: String): Flow<List<DiaryDateCount>>
    fun observeEntriesByDate(date: String): Flow<List<DiaryEntry>>
    fun observeEntry(id: Long): Flow<DiaryEntry?>
    suspend fun save(entryDraft: DiaryEntryDraft): Result<Long>
    suspend fun delete(id: Long): Result<Unit>
    fun search(keyword: String): Flow<List<DiarySearchItem>>
}
