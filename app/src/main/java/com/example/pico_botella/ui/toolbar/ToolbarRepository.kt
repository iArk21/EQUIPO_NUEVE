package com.example.pico_botella.ui.toolbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ToolbarRepository {
    // En una implementación real con Room, aquí se guardaría el estado del audio.
    // Por ahora lo manejamos en memoria para cumplir con la estructura MVVM solicitada.
    private val _isAudioEnabled = MutableStateFlow(true)
    val isAudioEnabled: StateFlow<Boolean> = _isAudioEnabled

    fun toggleAudio() {
        _isAudioEnabled.value = !_isAudioEnabled.value
    }
}