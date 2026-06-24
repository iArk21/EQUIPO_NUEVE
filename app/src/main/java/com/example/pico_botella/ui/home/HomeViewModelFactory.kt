package com.example.pico_botella.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.pico_botella.data.local.RetoRepository

class HomeViewModelFactory(
    private val repository: HomeRepository,
    private val retoRepository: RetoRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, retoRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}