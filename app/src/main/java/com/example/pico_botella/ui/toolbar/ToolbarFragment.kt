package com.example.pico_botella.ui.toolbar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentToolbarBinding
import kotlinx.coroutines.launch

/**
 * ToolbarFragment — HU 3.0.
 * Gestiona los 5 botones de la toolbar personalizada.
 *
 * CAMBIO para HU 10:
 * - btnShare ya NO navega a shareFragment (que tenía una pantalla vacía).
 * - Ahora lanza el Intent de compartir DIRECTAMENTE desde la toolbar,
 *   que es el comportamiento correcto según HU 10 Criterio 1.
 *
 * CAMBIO para HU 4.0:
 * - URL de calificar corregida a la de Nequi según indica la HU.
 */
class ToolbarFragment : Fragment() {

    private var _binding: FragmentToolbarBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ToolbarViewModel by activityViewModels {
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

        // HU 4.0: Calificar — abre Nequi en Google Play como ejemplo
        binding.btnRate.setOnClickListener {
            animarBoton(it) {
                val intent = Intent(Intent.ACTION_VIEW).apply {
                    data = Uri.parse(getString(R.string.calificar_url))
                }
                startActivity(intent)
            }
        }

        // HU 3.0 Criterio 3: Toggle de audio ON/OFF
        binding.btnAudio.setOnClickListener {
            animarBoton(it) {
                viewModel.toggleAudio()
            }
        }

        // HU 5.0: Instrucciones del juego
        binding.btnReglas.setOnClickListener {
            animarBoton(it) {
                findNavController().navigate(R.id.reglasFragment)
            }
        }

        // HU 6.0: Agregar y listar retos
        binding.btnChallenges.setOnClickListener {
            animarBoton(it) {
                findNavController().navigate(R.id.challengesFragment)
            }
        }

        // HU 10: Compartir app — lanza el bottom sheet del SO directamente.
        // NO navega a ShareFragment; el chooser se abre aquí mismo.
        binding.btnShare.setOnClickListener {
            animarBoton(it) {
                lanzarChooserCompartir()
            }
        }
    }

    /**
     * HU 10 — Construye y lanza el Intent de compartir.
     *
     * Criterio 1: createChooser() genera el bottom sheet nativo del SO.
     * Criterio 2: Mensaje con título, eslogan y URL (Nequi como ejemplo).
     */
    private fun lanzarChooserCompartir() {
        val titulo  = getString(R.string.compartir_titulo_app)   // "App pico botella"
        val eslogan = getString(R.string.compartir_eslogan)       // "Solo los valientes lo juegan !!"
        val url     = getString(R.string.compartir_url)           // URL Nequi como ejemplo

        val mensajeCompleto = "$titulo\n$eslogan\n$url"

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensajeCompleto)
        }

        val chooser = Intent.createChooser(
            shareIntent,
            getString(R.string.compartir_chooser_titulo)
        )
        startActivity(chooser)
    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.isAudioEnabled.collect { isEnabled ->
                    binding.btnAudio.setImageResource(
                        if (isEnabled) R.drawable.ic_audio_on
                        else R.drawable.ic_audio_off
                    )
                }
            }
        }
    }

    /**
     * Animación táctil sutil antes de ejecutar la acción (HU 3.0 Criterio 7).
     */
    private fun animarBoton(view: View, accion: () -> Unit) {
        view.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.button_touch))
        view.postDelayed(accion, 150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
