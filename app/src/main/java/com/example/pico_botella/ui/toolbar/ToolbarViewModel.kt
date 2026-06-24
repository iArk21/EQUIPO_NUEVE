package com.example.pico_botella.ui.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para la Toolbar.
 * @HiltViewModel: Permite que Hilt gestione el ciclo de vida de este ViewModel.
 * @Inject constructor: Inyecta el ToolbarRepository automáticamente.
 */
@HiltViewModel
class ToolbarViewModel @Inject constructor(private val repository: ToolbarRepository) : ViewModel() {

    val isAudioEnabled: StateFlow<Boolean> = repository.isAudioEnabled

    // Estado para pausar temporalmente por navegación
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
