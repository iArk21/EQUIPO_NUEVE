package com.example.pico_botella.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * DAO para realizar operaciones sobre la tabla de retos.
 */
@Dao
interface RetoDao {

    @Query("SELECT * FROM retos ORDER BY id DESC")
    fun getAllRetos(): Flow<List<RetoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReto(reto: RetoEntity)

    @Update
    suspend fun updateReto(reto: RetoEntity)

    @Delete
    suspend fun deleteReto(reto: RetoEntity)
}
