package com.doptsw.sle.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiscDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DiscResultEntity): Long

    @Query("SELECT * FROM disc_results WHERE id = :id")
    fun getById(id: Long): Flow<DiscResultEntity?>

    @Query("SELECT * FROM disc_results ORDER BY createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int): Flow<List<DiscResultEntity>>
}
