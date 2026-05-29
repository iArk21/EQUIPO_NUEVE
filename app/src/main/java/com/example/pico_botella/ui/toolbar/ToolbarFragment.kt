package com.example.pico_botella.ui.toolbar

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentToolbarBinding
import kotlinx.coroutines.launch

class ToolbarFragment : Fragment() {

    private var _binding: FragmentToolbarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ToolbarViewModel by viewModels {
        ToolbarViewModelFactory(ToolbarRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentToolbarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupListeners()
        observeState()
    }

    private fun setupListeners() {
        binding.btnRate.setOnClickListener {
            findNavController().navigate(R.id.ratingFragment)
        }

        binding.btnAudio.setOnClickListener {
            viewModel.toggleAudio()
        }

        binding.btnInstructions.setOnClickListener {
            findNavController().navigate(R.id.instructionsFragment)
        }

        binding.btnChallenges.setOnClickListener {
            findNavController().navigate(R.id.challengesFragment)
        }

        binding.btnShare.setOnClickListener {
            findNavController().navigate(R.id.shareFragment)
        }
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isAudioEnabled.collect { isEnabled ->
                    if (isEnabled) {
                        binding.btnAudio.setImageResource(R.drawable.ic_audio_on)
                    } else {
                        binding.btnAudio.setImageResource(R.drawable.ic_audio_off)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}