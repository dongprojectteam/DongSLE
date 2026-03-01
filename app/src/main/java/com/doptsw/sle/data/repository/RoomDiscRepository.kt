package com.doptsw.sle.data.repository

import com.doptsw.sle.data.local.DiscDao
import com.doptsw.sle.data.local.DiscResultEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.json.JSONArray
import org.json.JSONObject

class RoomDiscRepository(
    private val dao: DiscDao
) : DiscRepository {
    override fun observeRecent(limit: Int): Flow<List<DiscResultRecord>> {
        return dao.getRecent(limit).map { entities -> entities.map { it.toDomain() } }
    }

    override fun observeOne(id: Long): Flow<DiscResultRecord?> {
        return dao.getById(id).map { entity -> entity?.toDomain() }
    }

    override suspend fun save(draft: DiscResultDraft): Result<Long> {
        return runCatching {
            dao.insert(
                DiscResultEntity(
                    answersJson = encodeDiscAnswers(draft.answers),
                    d = draft.d,
                    i = draft.i,
                    s = draft.s,
                    c = draft.c,
                    topTypesCsv = draft.topTypes.joinToString(","),
                    interpretationKey = draft.interpretationKey,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}

private fun DiscResultEntity.toDomain(): DiscResultRecord {
    return DiscResultRecord(
        id = id,
        answers = decodeDiscAnswers(answersJson),
        d = d,
        i = i,
        s = s,
        c = c,
        topTypes = topTypesCsv.split(",").map { it.trim() }.filter { it.isNotEmpty() },
        interpretationKey = interpretationKey,
        createdAt = createdAt
    )
}

internal fun encodeDiscAnswers(answers: List<DiscAnswerSelection>): String {
    val array = JSONArray()
    answers.forEach { answer ->
        array.put(
            JSONObject()
                .put("questionId", answer.questionId)
                .put("mostType", answer.mostType)
                .put("leastType", answer.leastType)
        )
    }
    return array.toString()
}

internal fun decodeDiscAnswers(value: String): List<DiscAnswerSelection> {
    val array = JSONArray(value)
    return buildList {
        for (i in 0 until array.length()) {
            val item = array.optJSONObject(i) ?: continue
            add(
                DiscAnswerSelection(
                    questionId = item.optInt("questionId"),
                    mostType = item.optString("mostType"),
                    leastType = item.optString("leastType")
                )
            )
        }
    }
}
