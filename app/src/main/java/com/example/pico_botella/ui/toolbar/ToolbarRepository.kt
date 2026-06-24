package com.example.pico_botella.ui.toolbar

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repositorio para la Toolbar.
 * @Singleton: Asegura que la misma instancia sea compartida en toda la app.
 */
@Singleton
class ToolbarRepository @Inject constructor() {
    private val _isAudioEnabled = MutableStateFlow(true)
    val isAudioEnabled: StateFlow<Boolean> = _isAudioEnabled

    fun toggleAudio() {
        _isAudioEnabled.value = !_isAudioEnabled.value
    }
}
