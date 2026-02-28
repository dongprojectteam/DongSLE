package com.doptsw.sle.data.repository

import kotlinx.coroutines.flow.Flow

interface DecisionRepository {
    fun observeCount(): Flow<Int>
    fun observeAll(): Flow<List<DecisionRecord>>
    fun observeOne(id: Long): Flow<DecisionRecord?>
    suspend fun save(draft: DecisionDraft): Result<Long>
    suspend fun delete(id: Long): Result<Unit>
}
