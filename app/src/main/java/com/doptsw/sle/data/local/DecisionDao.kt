package com.doptsw.sle.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface DecisionDao {
    @Query("SELECT * FROM decision_records ORDER BY updatedAt DESC")
    fun getAllDesc(): Flow<List<DecisionRecordEntity>>

    @Query("SELECT * FROM decision_records WHERE id = :id")
    fun getById(id: Long): Flow<DecisionRecordEntity?>

    @Query("SELECT * FROM decision_records WHERE id = :id")
    suspend fun findById(id: Long): DecisionRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DecisionRecordEntity): Long

    @Update
    suspend fun update(entity: DecisionRecordEntity)

    @Query("DELETE FROM decision_records WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("SELECT COUNT(*) FROM decision_records")
    fun count(): Flow<Int>
}
