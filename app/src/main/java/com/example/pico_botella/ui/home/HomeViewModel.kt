package com.example.pico_botella.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: HomeRepository) : ViewModel() {

    private val _countdown = MutableStateFlow<Int?>(null)
    val countdown: StateFlow<Int?> = _countdown

    fun startCountdown() {
        viewModelScope.launch {
            repository.startCountdown().collect {
                _countdown.value = it
            }
        }
    }

    // Limpia el estado para evitar que se dispare el giro al volver de otro fragmento
    fun resetCountdown() {
        _countdown.value = null
    }
}
