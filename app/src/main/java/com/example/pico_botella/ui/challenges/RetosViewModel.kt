package com.example.pico_botella.ui.challenges

import androidx.lifecycle.*
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.data.local.RetoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @HiltViewModel: Le indica a Dagger Hilt que esta clase es un ViewModel y habilita
 * su ciclo de vida para ser manejado por los componentes de Android (como Fragments o Activities).
 * 
 * @Inject constructor: Le dice a Hilt cómo debe instanciar esta clase, inyectando
 * de manera automática la dependencia de RetoRepository en su constructor.
 */
@HiltViewModel
class RetosViewModel @Inject constructor(private val repository: RetoRepository) : ViewModel() {

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
