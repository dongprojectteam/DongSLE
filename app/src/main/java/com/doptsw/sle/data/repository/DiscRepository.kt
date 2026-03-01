package com.doptsw.sle.data.repository

import kotlinx.coroutines.flow.Flow

interface DiscRepository {
    fun observeRecent(limit: Int = 10): Flow<List<DiscResultRecord>>
    fun observeOne(id: Long): Flow<DiscResultRecord?>
    suspend fun save(draft: DiscResultDraft): Result<Long>
}
