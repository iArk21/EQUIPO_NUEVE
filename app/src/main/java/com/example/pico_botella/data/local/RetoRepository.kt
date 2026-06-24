package com.example.pico_botella.data.local

import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Repositorio para abstraer el acceso a datos de los retos.
 * Se usa @Inject para que Hilt sepa cómo crear instancias de esta clase.
 */
class RetoRepository @Inject constructor(private val retoDao: RetoDao) {

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
