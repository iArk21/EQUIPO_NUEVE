package com.example.pico_botella.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidad que representa un Reto en la base de datos.
 */
@Entity(tableName = "retos")
data class RetoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val descripcion: String,
    val createdAt: Long = System.currentTimeMillis()
)
