package com.example.pico_botella.ui.toolbar

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

/**
 * Tests unitarios para ToolbarViewModel.
 * Se utiliza Mockito-Kotlin para simular el repositorio y Coroutines Test para el manejo de hilos.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ToolbarViewModelTest {

    private lateinit var viewModel: ToolbarViewModel
    private val repository: ToolbarRepository = mock()
    
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Redirigimos el Main dispatcher para que las corrutinas del ViewModel se ejecuten en nuestro entorno de prueba.
        Dispatchers.setMain(testDispatcher)
        
        // Simulamos que el repositorio tiene un flujo inicial de audio habilitado (true).
        whenever(repository.isAudioEnabled).thenReturn(MutableStateFlow(true))
        
        viewModel = ToolbarViewModel(repository)
    }

    @After
    fun tearDown() {
        // Limpiamos el estado de los dispatchers.
        Dispatchers.resetMain()
    }

    @Test
    fun `toggleAudio llama al repositorio para alternar el estado del audio`() = runTest {
        /**
         * PROPÓSITO: Verificar que al presionar el botón de audio en la Toolbar, se notifique al repositorio.
         * Criterio HU: El usuario puede activar/desactivar el sonido globalmente.
         */
        
        viewModel.toggleAudio()
        advanceUntilIdle()

        // Verificamos que se haya invocado la función toggleAudio() en el repositorio exactamente una vez.
        verify(repository).toggleAudio()
    }

    @Test
    fun `setMusicPausedTemporarily cambia el estado de pausa temporal correctamente`() = runTest {
        /**
         * PROPÓSITO: Validar que el estado de pausa temporal (por navegación) cambie según se solicite.
         * Criterio HU: La música debe pausarse cuando el usuario navega a fragmentos específicos.
         */
        
        // Inicialmente debería ser false.
        assertFalse(viewModel.isMusicPausedTemporarily.value)

        // Cambiamos a true.
        viewModel.setMusicPausedTemporarily(true)
        assertTrue(viewModel.isMusicPausedTemporarily.value)

        // Cambiamos de nuevo a false.
        viewModel.setMusicPausedTemporarily(false)
        assertFalse(viewModel.isMusicPausedTemporarily.value)
    }

    @Test
    fun `isAudioEnabled refleja el estado actual del repositorio`() = runTest {
        /**
         * PROPÓSITO: Asegurar que el ViewModel esté observando correctamente el flujo de datos del repositorio.
         */
        val audioFlow = MutableStateFlow(false)
        whenever(repository.isAudioEnabled).thenReturn(audioFlow)
        
        // Reinicializamos para que tome el nuevo mock del flow
        viewModel = ToolbarViewModel(repository)

        // Verificamos que el valor inicial sea false (el del nuevo flow).
        assertEquals(false, viewModel.isAudioEnabled.value)
        
        // Si el repositorio emite true, el VM debería reflejarlo.
        audioFlow.value = true
        assertEquals(true, viewModel.isAudioEnabled.value)
    }
}
