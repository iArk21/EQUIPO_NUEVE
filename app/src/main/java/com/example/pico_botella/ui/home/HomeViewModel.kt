package com.example.pico_botella.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.data.local.RetoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository,
    private val retoRepository: RetoRepository
) : ViewModel() {

    private val _countdown = MutableStateFlow<Int?>(null)
    val countdown: StateFlow<Int?> = _countdown

    private val _randomReto = MutableStateFlow<String?>(null)
    val randomReto: StateFlow<String?> = _randomReto

    fun startCountdown() {
        viewModelScope.launch {
            repository.startCountdown().collect {
                _countdown.value = it
            }
        }
    }

    fun resetCountdown() {
        _countdown.value = null
    }

    fun getRandomReto() {
        viewModelScope.launch {
            val retos = retoRepository.allRetos.firstOrNull()
            _randomReto.value = if (retos.isNullOrEmpty()) {
                "No hay retos disponibles. ¡Agrega algunos!"
            } else {
                retos.random().descripcion
            }
        }
    }

    fun resetRandomReto() {
        _randomReto.value = null
    }
}