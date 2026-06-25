package com.example.pico_botella.ui.challenges

import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.data.local.RetoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Tests unitarios para RetosViewModel.
 * Se enfoca en las operaciones CRUD y el estado de la música.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class RetosViewModelTest {

    private lateinit var viewModel: RetosViewModel
    private val repository: RetoRepository = mock()
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        
        // Mockeamos el flow de retos para que la inicialización del LiveData no falle
        whenever(repository.allRetos).thenReturn(flowOf(emptyList()))
        
        viewModel = RetosViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `insert llama al repositorio con una nueva entidad`() = runTest {
        /**
         * PROPÓSITO: Verificar que el comando de inserción llegue al repositorio.
         * HU: El usuario puede agregar nuevos retos.
         */
        val descripcion = "Nuevo Reto"
        
        viewModel.insert(descripcion)
        advanceUntilIdle()

        // Verificamos que se llamó a insert en el repositorio
        verify(repository).insert(any())
    }

    @Test
    fun `delete llama al repositorio para eliminar el reto`() = runTest {
        /**
         * PROPÓSITO: Validar la eliminación de un reto.
         * HU: El usuario puede borrar retos existentes.
         */
        val reto = RetoEntity(id = 1, descripcion = "Reto a borrar", createdAt = 0L)
        
        viewModel.delete(reto)
        advanceUntilIdle()

        verify(repository).delete(reto)
    }

    @Test
    fun `update llama al repositorio para actualizar el reto`() = runTest {
        /**
         * PROPÓSITO: Validar la edición de un reto.
         * HU: El usuario puede modificar retos existentes.
         */
        val reto = RetoEntity(id = 1, descripcion = "Reto editado", createdAt = 0L)
        
        viewModel.update(reto)
        advanceUntilIdle()

        verify(repository).update(reto)
    }

    @Test
    fun `setRestoreMusic actualiza correctamente el estado de shouldRestoreMusic`() {
        /**
         * PROPÓSITO: Controlar el estado de la música al navegar.
         */
        // Estado inicial
        assertEquals(false, viewModel.shouldRestoreMusic.value)

        // Cambio a true
        viewModel.setRestoreMusic(true)
        assertEquals(true, viewModel.shouldRestoreMusic.value)

        // Cambio a false
        viewModel.setRestoreMusic(false)
        assertEquals(false, viewModel.shouldRestoreMusic.value)
    }
}
