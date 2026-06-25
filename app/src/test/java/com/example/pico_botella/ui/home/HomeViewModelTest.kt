package com.example.pico_botella.ui.home

import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.data.local.RetoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

/*
Dependencias añadidas en build.gradle.kts:
testImplementation(libs.mockito.kotlin)
testImplementation(libs.kotlinx.coroutines.test)
*/

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    // mock() crea "objetos falsos" que imitan el comportamiento de las clases reales.
    // Esto permite probar el ViewModel sin necesidad de una base de datos o conexion real
    private lateinit var viewModel: HomeViewModel
    private val homeRepository: HomeRepository = mock()
    private val retoRepository: RetoRepository = mock()
    // StandardTestDispatcher es un despachador que
    // no ejecuta las corrutinas inmediatamente.
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        // Esto es obligatorio para que los tests no fallen al usar viewModelScope.
        Dispatchers.setMain(testDispatcher)
        viewModel = HomeViewModel(homeRepository, retoRepository)
    }

    @After
    fun tearDown() {
        // Al terminar cada test, limpiamos el despachador Main
        // para no afectar a otros tests.
        Dispatchers.resetMain()
    }

    @Test
    fun `startCountdown actualiza countdown con los valores del Flow (3, 2, 1, 0)`() = runTest {
        // Criterio HU: Visualizar cuenta regresiva antes de que la botella se detenga.
        whenever(homeRepository.startCountdown()).thenReturn(flowOf(3, 2, 1, 0))

        viewModel.startCountdown()
        advanceUntilIdle()

        assertEquals(0, viewModel.countdown.value)
    }

    @Test
    fun `resetCountdown pone countdown en null`() = runTest {
        /**
         * PROPÓSITO: Verificar que la cuenta regresiva fluye correctamente hacia la UI.
         * HU: El usuario debe ver la cuenta atrás antes de conocer su reto.
         */

        // Criterio HU: Permitir reiniciar el ciclo del juego limpiando estados previos.
        whenever(homeRepository.startCountdown()).thenReturn(flowOf(3))
        viewModel.startCountdown()
        advanceUntilIdle()

        viewModel.resetCountdown()

        assertNull(viewModel.countdown.value)
    }

    @Test
    fun `getRandomReto con retos disponibles asigna un reto de la lista a randomReto`() = runTest {
        // Criterio HU: Al detenerse la botella, asignar un reto al azar al jugador.
        /**
         * PROPÓSITO: Limpiar el estado de la cuenta regresiva.
         * HU: Permitir reiniciar el ciclo del juego sin residuos de la partida anterior.
         */
        val retosMock = listOf(
            RetoEntity(1, "Bebe un trago", 0L),
            RetoEntity(2, "Canta una canción", 0L)
        )
        whenever(retoRepository.allRetos).thenReturn(flowOf(retosMock))

        viewModel.getRandomReto()
        advanceUntilIdle()

        val valorResultante = viewModel.randomReto.value
        assertTrue(retosMock.any { it.descripcion == valorResultante })
    }

    @Test
    fun `getRandomReto con lista vacía asigna el mensaje No hay retos disponibles`() = runTest {
        // Criterio HU: Notificar al usuario si no hay retos configurados.
        whenever(retoRepository.allRetos).thenReturn(flowOf(emptyList()))

        viewModel.getRandomReto()
        advanceUntilIdle()

        assertEquals("No hay retos disponibles. ¡Agrega algunos!", viewModel.randomReto.value)
    }

    @Test
    fun `resetRandomReto pone randomReto en null`() = runTest {
        // Criterio HU: Resetear la UI para un nuevo giro de botella.
        whenever(retoRepository.allRetos).thenReturn(flowOf(listOf(RetoEntity(1, "Test", 0L))))
        viewModel.getRandomReto()
        advanceUntilIdle()

        viewModel.resetRandomReto()

        assertNull(viewModel.randomReto.value)
    }

    @Test
    fun `getRandomReto llamado dos veces seguidas produce retos válidos ambas veces`() = runTest {
        // Criterio HU: El juego debe permitir múltiples giros con sus respectivos retos.
        val retosMock = listOf(RetoEntity(1, "Reto Único", 0L))
        whenever(retoRepository.allRetos).thenReturn(flowOf(retosMock))

        viewModel.getRandomReto()
        advanceUntilIdle()
        assertEquals("Reto Único", viewModel.randomReto.value)

        viewModel.getRandomReto()
        advanceUntilIdle()
        assertEquals("Reto Único", viewModel.randomReto.value)
    }
}
