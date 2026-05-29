package com.example.pico_botella.ui.toolbar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class ToolbarViewModelFactory(private val repository: ToolbarRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ToolbarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ToolbarViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}