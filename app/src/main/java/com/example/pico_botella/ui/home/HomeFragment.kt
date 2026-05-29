package com.example.pico_botella.ui.home

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentHomeBinding
import com.example.pico_botella.ui.toolbar.ToolbarRepository
import com.example.pico_botella.ui.toolbar.ToolbarViewModel
import com.example.pico_botella.ui.toolbar.ToolbarViewModelFactory
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels {
        HomeViewModelFactory(HomeRepository())
    }

    // Usamos activityViewModels para compartir el estado del audio con la Toolbar
    private val toolbarViewModel: ToolbarViewModel by activityViewModels {
        ToolbarViewModelFactory(ToolbarRepository())
    }

    private var mediaPlayer: MediaPlayer? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        observeViewModel()
        setupAudio()
    }

    private fun setupUI() {
        // Animación de parpadeo para el botón
        val blinkAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        binding.btnPressMe.startAnimation(blinkAnim)

        binding.btnPressMe.setOnClickListener {
            homeViewModel.startCountdown()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.countdown.collect { count ->
                    if (count != null) {
                        binding.tvCountdown.visibility = View.VISIBLE
                        binding.tvCountdown.text = count.toString()
                        if (count == 0) {
                            binding.tvCountdown.text = "¡Gira!"
                            // Aquí se activaría la lógica de giro de la botella
                        }
                    } else {
                        binding.tvCountdown.visibility = View.GONE
                    }
                }
            }
        }

        // Observar el estado del audio desde el ToolbarViewModel
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                toolbarViewModel.isAudioEnabled.collect { isEnabled ->
                    if (isEnabled) {
                        mediaPlayer?.start()
                    } else {
                        mediaPlayer?.pause()
                    }
                }
            }
        }
    }

    private fun setupAudio() {
        // Nota: Asegúrate de agregar un archivo 'background_music.mp3' en res/raw
        // Por ahora, intentamos inicializarlo si existe el recurso. 
        // Si no existe, el try-catch evitará el crash.
        try {
            val resId = resources.getIdentifier("background_music", "raw", requireContext().packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(requireContext(), resId).apply {
                    isLooping = true
                    if (toolbarViewModel.isAudioEnabled.value) {
                        start()
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}