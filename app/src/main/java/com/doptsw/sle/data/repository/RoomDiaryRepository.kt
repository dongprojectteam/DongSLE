package com.doptsw.sle.data.repository

import com.doptsw.sle.data.local.DiaryDao
import com.doptsw.sle.data.local.DiaryEntryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomDiaryRepository(
    private val dao: DiaryDao
) : DiaryRepository {
    override fun observeAllEntriesDesc(): Flow<List<DiaryEntry>> {
        return dao.getAllEntriesDesc().map { list -> list.map { it.toDomain() } }
    }

    override fun observeDiaryDatesWithCount(monthStart: String, monthEnd: String): Flow<List<DiaryDateCount>> {
        return dao.getDateCounts(monthStart, monthEnd).map { list ->
            list.map { DiaryDateCount(entryDate = it.entryDate, count = it.count) }
        }
    }

    override fun observeEntriesByDate(date: String): Flow<List<DiaryEntry>> {
        return dao.getEntriesByDate(date).map { list -> list.map { it.toDomain() } }
    }

    override fun observeEntry(id: Long): Flow<DiaryEntry?> {
        return dao.getEntryById(id).map { it?.toDomain() }
    }

    override suspend fun save(entryDraft: DiaryEntryDraft): Result<Long> {
        return runCatching {
            val now = System.currentTimeMillis()
            if (entryDraft.id == null) {
                dao.insert(
                    DiaryEntryEntity(
                        entryDate = entryDraft.entryDate,
                        situation = entryDraft.situation,
                        feeling = entryDraft.feeling,
                        thought = entryDraft.thought,
                        desiredAction = entryDraft.desiredAction,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            } else {
                val existing = dao.findEntryById(entryDraft.id)
                dao.update(
                    DiaryEntryEntity(
                        id = entryDraft.id,
                        entryDate = entryDraft.entryDate,
                        situation = entryDraft.situation,
                        feeling = entryDraft.feeling,
                        thought = entryDraft.thought,
                        desiredAction = entryDraft.desiredAction,
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now
                    )
                )
                entryDraft.id
            }
        }
    }

    override suspend fun delete(id: Long): Result<Unit> {
        return runCatching {
            dao.deleteById(id)
        }
    }

    override fun search(keyword: String): Flow<List<DiarySearchItem>> {
        return dao.search(keyword).map { list ->
            list.map {
                DiarySearchItem(
                    id = it.id,
                    entryDate = it.entryDate,
                    preview = it.situation.take(60)
                )
            }
        }
    }
}

private fun DiaryEntryEntity.toDomain(): DiaryEntry {
    return DiaryEntry(
        id = id,
        entryDate = entryDate,
        situation = situation,
        feeling = feeling,
        thought = thought,
        desiredAction = desiredAction,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
