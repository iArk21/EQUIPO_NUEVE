package com.example.pico_botella.ui.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ToolbarViewModel(private val repository: ToolbarRepository) : ViewModel() {

    val isAudioEnabled: StateFlow<Boolean> = repository.isAudioEnabled

    fun toggleAudio() {
        viewModelScope.launch {
            repository.toggleAudio()
        }
    }
}