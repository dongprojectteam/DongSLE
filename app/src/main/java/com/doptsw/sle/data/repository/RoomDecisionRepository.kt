package com.doptsw.sle.data.repository

import com.doptsw.sle.data.local.DecisionDao
import com.doptsw.sle.data.local.DecisionRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray

class RoomDecisionRepository(
    private val dao: DecisionDao
) : DecisionRepository {
    override fun observeCount(): Flow<Int> = dao.count()

    override fun observeAll(): Flow<List<DecisionRecord>> {
        return dao.getAllDesc().map { list -> list.map { it.toDomain() } }
    }

    override fun observeOne(id: Long): Flow<DecisionRecord?> {
        return dao.getById(id).map { it?.toDomain() }
    }

    override suspend fun save(draft: DecisionDraft): Result<Long> {
        return runCatching {
            val now = System.currentTimeMillis()
            if (draft.id == null) {
                dao.insert(
                    DecisionRecordEntity(
                        optionA = draft.optionA,
                        optionB = draft.optionB,
                        reasonsAJson = encodeReasons(draft.reasonsA),
                        reasonsBJson = encodeReasons(draft.reasonsB),
                        conclusion = draft.conclusion,
                        createdAt = now,
                        updatedAt = now
                    )
                )
            } else {
                val existing = dao.findById(draft.id)
                dao.update(
                    DecisionRecordEntity(
                        id = draft.id,
                        optionA = draft.optionA,
                        optionB = draft.optionB,
                        reasonsAJson = encodeReasons(draft.reasonsA),
                        reasonsBJson = encodeReasons(draft.reasonsB),
                        conclusion = draft.conclusion,
                        createdAt = existing?.createdAt ?: now,
                        updatedAt = now
                    )
                )
                draft.id
            }
        }
    }

    override suspend fun delete(id: Long): Result<Unit> {
        return runCatching {
            dao.deleteById(id)
        }
    }
}

private fun DecisionRecordEntity.toDomain(): DecisionRecord {
    return DecisionRecord(
        id = id,
        optionA = optionA,
        optionB = optionB,
        reasonsA = decodeReasons(reasonsAJson),
        reasonsB = decodeReasons(reasonsBJson),
        conclusion = conclusion,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

private fun encodeReasons(reasons: List<String>): String {
    val array = JSONArray()
    reasons.forEach { array.put(it) }
    return array.toString()
}

private fun decodeReasons(value: String): List<String> {
    val array = JSONArray(value)
    return buildList {
        for (i in 0 until array.length()) {
            add(array.optString(i))
        }
    }
}
