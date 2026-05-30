package com.example.pico_botella.data.local

import kotlinx.coroutines.flow.Flow

/**
 * Repositorio para abstraer el acceso a datos de los retos.
 */
class RetoRepository(private val retoDao: RetoDao) {

    val allRetos: Flow<List<RetoEntity>> = retoDao.getAllRetos()

    suspend fun insert(reto: RetoEntity) {
        retoDao.insertReto(reto)
    }

    suspend fun update(reto: RetoEntity) {
        retoDao.updateReto(reto)
    }

    suspend fun delete(reto: RetoEntity) {
        retoDao.deleteReto(reto)
    }
}
