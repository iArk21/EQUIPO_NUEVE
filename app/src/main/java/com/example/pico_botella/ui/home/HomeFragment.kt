package com.example.pico_botella.ui.home

import android.media.MediaPlayer
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentHomeBinding
import com.example.pico_botella.ui.challenges.RetoAleatorioDialogFragment
import com.example.pico_botella.ui.toolbar.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val homeViewModel: HomeViewModel by viewModels()
    private val toolbarViewModel: ToolbarViewModel by activityViewModels()

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
        setupBackPress()
        setupUI()
        observeViewModel()
        setupAudio()
    }

    private fun setupBackPress() {
        requireActivity().onBackPressedDispatcher.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            }
        )
    }

    private fun setupUI() {
        val blinkAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
        binding.btnPressMe.startAnimation(blinkAnim)

        binding.btnPressMe.setOnClickListener {
            isGameRunning = true
            binding.btnPressMe.visibility = View.GONE
            binding.btnPressMe.clearAnimation()
            mediaPlayer?.pause()
            startBottleSpin()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    toolbarViewModel.isAudioEnabled.collect { isEnabled ->
                        updateMusicState(isEnabled)
                    }
                }
                launch {
                    homeViewModel.countdown.collect { count ->
                        if (count != null) {
                            binding.tvCountdown.visibility = View.VISIBLE
                            binding.tvCountdown.text = count.toString()
                            if (count == 0) {
                                binding.tvCountdown.visibility = View.GONE
                                homeViewModel.getRandomReto()
                            }
                        }
                    }
                }
                launch {
                    homeViewModel.randomReto.collect { reto ->
                        if (reto != null) {
                            showChallengeDialog(reto)
                            homeViewModel.resetRandomReto()
                            homeViewModel.resetCountdown()
                        }
                    }
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
                binding.tvCountdown.setTextColor(
                    ContextCompat.getColor(requireContext(), R.color.neon_orange)
                )
                homeViewModel.startCountdown()
            }
            .start()
    }

    private fun showChallengeDialog(reto: String) {
        RetoAleatorioDialogFragment(reto) {
            isGameRunning = false
            binding.btnPressMe.visibility = View.VISIBLE
            val blinkAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.blink)
            binding.btnPressMe.startAnimation(blinkAnim)
            if (toolbarViewModel.isAudioEnabled.value) {
                mediaPlayer?.start()
            }
        }.show(parentFragmentManager, "RetoAleatorioDialog")
    }

    private fun updateMusicState(isEnabled: Boolean) {
        if (!isGameRunning) {
            if (isEnabled && isResumed) mediaPlayer?.start()
            else mediaPlayer?.pause()
        }
    }

    private fun setupAudio() {
        try {
            val resId = resources.getIdentifier(
                "background_music", "raw", requireContext().packageName
            )
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