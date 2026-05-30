package com.example.pico_botella.ui.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ToolbarViewModel(private val repository: ToolbarRepository) : ViewModel() {

    val isAudioEnabled: StateFlow<Boolean> = repository.isAudioEnabled

    // Estado para pausar temporalmente por navegación (Criterio 1 de Reglas)
    private val _isMusicPausedTemporarily = MutableStateFlow(false)
    val isMusicPausedTemporarily = _isMusicPausedTemporarily.asStateFlow()

    fun toggleAudio() {
        viewModelScope.launch {
            repository.toggleAudio()
        }
    }

    fun setMusicPausedTemporarily(paused: Boolean) {
        _isMusicPausedTemporarily.value = paused
    }
}