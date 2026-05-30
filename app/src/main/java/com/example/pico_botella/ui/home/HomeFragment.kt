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
                        }
                    } else {
                        binding.tvCountdown.visibility = View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                toolbarViewModel.isAudioEnabled.collect { isEnabled ->
                    updateMusicState(isEnabled)
                }
            }
        }
    }

    private fun updateMusicState(isEnabled: Boolean) {
        if (isEnabled && isResumed) {
            mediaPlayer?.start()
        } else {
            mediaPlayer?.pause()
        }
    }

    private fun setupAudio() {
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

    override fun onResume() {
        super.onResume()
        // Reanudar si el audio está habilitado en la configuración
        if (toolbarViewModel.isAudioEnabled.value) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        // Pausar siempre que la app pase a segundo plano
        mediaPlayer?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        mediaPlayer = null
        _binding = null
    }
}