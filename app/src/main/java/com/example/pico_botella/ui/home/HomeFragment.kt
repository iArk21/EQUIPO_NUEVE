package com.example.pico_botella.ui.home

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pico_botella.R
import com.example.pico_botella.data.local.AppDatabase
import com.example.pico_botella.databinding.FragmentHomeBinding
import com.example.pico_botella.ui.challenges.RetoAleatorioDialogFragment
import com.example.pico_botella.ui.toolbar.ToolbarRepository
import com.example.pico_botella.ui.toolbar.ToolbarViewModel
import com.example.pico_botella.ui.toolbar.ToolbarViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
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
    private var spinPlayer: MediaPlayer? = null
    
    private var isGameRunning = false

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
            isGameRunning = true
            binding.btnPressMe.visibility = View.GONE
            binding.btnPressMe.clearAnimation()
            mediaPlayer?.pause()
            homeViewModel.startCountdown()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.countdown.collect { count ->
                    if (count != null) {
                        binding.tvCountdown.visibility = View.VISIBLE
                        binding.tvCountdown.setTextColor(android.graphics.Color.RED)
                        binding.tvCountdown.text = count.toString()
                        if (count == 0) {
                            binding.tvCountdown.text = "¡Gira!"
                            // Solución al error reportado: resetear el estado del conteo
                            // para que no se repita el giro al volver de otra pantalla.
                            homeViewModel.resetCountdown()
                            startBottleSpin()
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

    private fun startBottleSpin() {
        binding.ivBottle.animate().cancel()
        val duration = (3000..5000).random().toLong()
        val vueltas = (duration / 1000f * 2).toInt()
        val finalAngle = (0..359).random()
        val rotation = (vueltas * 360 + finalAngle).toFloat()

        if (toolbarViewModel.isAudioEnabled.value) {
            spinPlayer?.seekTo(0)
            spinPlayer?.start()
        }

        binding.ivBottle.animate()
            .rotationBy(rotation)
            .setDuration(duration)
            .withEndAction {
                spinPlayer?.pause()
                spinPlayer?.seekTo(0)
                showPostSpinCountdown()
            }
            .start()
    }

    private fun showPostSpinCountdown() {
        viewLifecycleOwner.lifecycleScope.launch {
            binding.tvCountdown.setTextColor(ContextCompat.getColor(requireContext(), R.color.neon_orange))
            binding.tvCountdown.visibility = View.VISIBLE
            
            for (i in 3 downTo 0) {
                binding.tvCountdown.text = i.toString()
                delay(1000)
            }
            
            binding.tvCountdown.visibility = View.GONE
            binding.btnPressMe.visibility = View.VISIBLE
            val blinkAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
            binding.btnPressMe.startAnimation(blinkAnim)
            
            showRandomChallengeDialog()
        }
    }

    private suspend fun showRandomChallengeDialog() {
        val database = AppDatabase.getDatabase(requireContext())
        val retosList = database.retoDao().getAllRetos().firstOrNull()
        
        val mensajeReto = if (retosList.isNullOrEmpty()) {
            getString(R.string.dialogo_reto_sin_retos)
        } else {
            retosList.random().descripcion
        }
        
        RetoAleatorioDialogFragment(mensajeReto) {
            isGameRunning = false
            if (toolbarViewModel.isAudioEnabled.value) {
                mediaPlayer?.start()
            }
        }.show(parentFragmentManager, "RetoAleatorioDialog")
    }

    private fun updateMusicState(isEnabled: Boolean) {
        if (!isGameRunning) {
            if (isEnabled && isResumed) {
                mediaPlayer?.start()
            } else {
                mediaPlayer?.pause()
            }
        }
    }

    private fun setupAudio() {
        try {
            val resId = resources.getIdentifier("background_music", "raw", requireContext().packageName)
            if (resId != 0) {
                mediaPlayer = MediaPlayer.create(requireContext(), resId).apply {
                    isLooping = true
                    if (toolbarViewModel.isAudioEnabled.value) start()
                }
            }
            spinPlayer = MediaPlayer.create(requireContext(), R.raw.spin_sound)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        if (!isGameRunning && toolbarViewModel.isAudioEnabled.value) {
            mediaPlayer?.start()
        }
    }

    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        mediaPlayer?.release()
        spinPlayer?.release()
        _binding = null
    }
}
