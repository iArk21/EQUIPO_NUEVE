package com.example.pico_botella.ui.reglas

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

/**
 * ViewModel para la pantalla de Reglas.
 * Gestiona el estado de la UI y la lógica de restauración de música.
 */
class ReglasViewModel : ViewModel() {

    // Estado para saber si debemos restaurar la música al salir
    private val _shouldRestoreMusic = MutableStateFlow(false)
    val shouldRestoreMusic: StateFlow<Boolean> = _shouldRestoreMusic

    /**
     * Define si la música debe restaurarse al volver al Home.
     */
    fun setRestoreMusic(restore: Boolean) {
        _shouldRestoreMusic.value = restore
    }
}
