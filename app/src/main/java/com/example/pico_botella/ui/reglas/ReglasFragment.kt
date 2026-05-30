package com.example.pico_botella.ui.reglas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentReglasBinding
import com.example.pico_botella.ui.toolbar.ToolbarRepository
import com.example.pico_botella.ui.toolbar.ToolbarViewModel
import com.example.pico_botella.ui.toolbar.ToolbarViewModelFactory

/**
 * Fragmento que muestra las reglas del juego.
 * Implementa pausa/resumen de música y animaciones de victoria.
 */
class ReglasFragment : Fragment() {

    private var _binding: FragmentReglasBinding? = null
    private val binding get() = _binding!!

    // ViewModel específico de la pantalla (MVVM)
    private val viewModel: ReglasViewModel by viewModels()

    // ViewModel compartido para gestionar el estado global del audio
    private val toolbarViewModel: ToolbarViewModel by activityViewModels {
        ToolbarViewModelFactory(ToolbarRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReglasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        handleMusicOnEnter()
        startVictoryAnimation()
    }

    private fun setupToolbar() {
        // Criterio 3: Flecha de regreso y navegación
        binding.btnBack.setOnClickListener {
            handleMusicOnExit()
            findNavController().popBackStack()
        }
    }

    /**
     * Criterio 1: Pausa la música si está activa al entrar.
     */
    private fun handleMusicOnEnter() {
        if (toolbarViewModel.isAudioEnabled.value) {
            viewModel.setRestoreMusic(true)
            toolbarViewModel.setMusicPausedTemporarily(true)
        }
    }

    /**
     * Criterio 1: Restaura el estado de la música al salir.
     */
    private fun handleMusicOnExit() {
        if (viewModel.shouldRestoreMusic.value) {
            toolbarViewModel.setMusicPausedTemporarily(false)
        }
    }

    /**
     * Criterio 8: Animación de victoria/triunfo.
     */
    private fun startVictoryAnimation() {
        val victoryAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.victory_animation)
        binding.ivVictory.startAnimation(victoryAnim)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Asegurar restauración si se sale por otros medios (ej. back button del sistema)
        handleMusicOnExit()
        _binding = null
    }
}
