package com.example.pico_botella.ui.challenges

import androidx.lifecycle.*
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.data.local.RetoRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel para la gestión de retos y estado de la música.
 */
class RetosViewModel(private val repository: RetoRepository) : ViewModel() {

    // Lista de retos observada desde Room
    val allRetos: LiveData<List<RetoEntity>> = repository.allRetos.asLiveData()

    // Estado para saber si debemos restaurar la música al salir
    private val _shouldRestoreMusic = MutableStateFlow(false)
    val shouldRestoreMusic: StateFlow<Boolean> = _shouldRestoreMusic

    fun insert(descripcion: String) = viewModelScope.launch {
        repository.insert(RetoEntity(descripcion = descripcion))
    }

    fun update(reto: RetoEntity) = viewModelScope.launch {
        repository.update(reto)
    }

    fun delete(reto: RetoEntity) = viewModelScope.launch {
        repository.delete(reto)
    }

    fun setRestoreMusic(restore: Boolean) {
        _shouldRestoreMusic.value = restore
    }
}

class RetosViewModelFactory(private val repository: RetoRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RetosViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RetosViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
