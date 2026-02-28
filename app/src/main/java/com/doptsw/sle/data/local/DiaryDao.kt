package com.doptsw.sle.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DiaryDao {
    @Query(
        """
        SELECT * FROM diary_entries
        WHERE entryDate = :date
        ORDER BY updatedAt DESC
        """
    )
    fun getEntriesByDate(date: String): Flow<List<DiaryEntryEntity>>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    fun getEntryById(id: Long): Flow<DiaryEntryEntity?>

    @Query("SELECT * FROM diary_entries WHERE id = :id")
    suspend fun findEntryById(id: Long): DiaryEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: DiaryEntryEntity): Long

    @Update
    suspend fun update(entry: DiaryEntryEntity)

    @Query("DELETE FROM diary_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        """
        SELECT * FROM diary_entries
        WHERE situation LIKE '%' || :keyword || '%' COLLATE NOCASE
           OR feeling LIKE '%' || :keyword || '%' COLLATE NOCASE
           OR thought LIKE '%' || :keyword || '%' COLLATE NOCASE
           OR desiredAction LIKE '%' || :keyword || '%' COLLATE NOCASE
        ORDER BY updatedAt DESC
        """
    )
    fun search(keyword: String): Flow<List<DiaryEntryEntity>>

    @Query(
        """
        SELECT entryDate, COUNT(*) AS count
        FROM diary_entries
        WHERE entryDate BETWEEN :monthStart AND :monthEnd
        GROUP BY entryDate
        """
    )
    fun getDateCounts(monthStart: String, monthEnd: String): Flow<List<DiaryDateCountEntity>>
}
