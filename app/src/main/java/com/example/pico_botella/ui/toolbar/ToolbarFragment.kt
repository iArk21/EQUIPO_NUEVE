package com.example.pico_botella.ui.toolbar

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentToolbarBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

/**
 * ToolbarFragment — HU 3.0
 * 6 botones naranja: estrella, audio, instrucciones, retos, compartir, cerrar sesión.
 *
 * NUEVO — btn_logout:
 * Muestra un AlertDialog de confirmación antes de cerrar sesión.
 * Al confirmar: llama FirebaseAuth.signOut() y navega al Login
 * limpiando todo el backstack (el usuario no puede volver al Home con "atrás").
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

        // HU 3.0 Criterio 3: Toggle audio ON/OFF
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

        // HU 10: Compartir app
        binding.btnShare.setOnClickListener {
            animarBoton(it) {
                lanzarChooserCompartir()
            }
        }

        // NUEVO — Cerrar sesión
        binding.btnLogout.setOnClickListener {
            animarBoton(it) {
                mostrarDialogoLogout()
            }
        }
    }

    /**
     * Muestra un diálogo de confirmación antes de cerrar sesión.
     * Evita que el usuario cierre sesión por accidente.
     */
    private fun mostrarDialogoLogout() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cerrar sesión")
            .setMessage("¿Deseas cerrar tu sesión?")
            .setPositiveButton("Sí") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("No", null)
            .show()
    }

    /**
     * Cierra la sesión de Firebase y navega al Login.
     *
     * FirebaseAuth.signOut() borra el token de sesión guardado en el dispositivo,
     * por lo que la próxima vez que abra la app, el SplashFragment detectará
     * currentUser == null y navegará al Login.
     *
     * popUpTo(nav_graph) + inclusive=true limpia TODO el backstack —
     * el usuario no puede volver al Home presionando "atrás" desde el Login.
     */
    private fun cerrarSesion() {
        FirebaseAuth.getInstance().signOut()
        findNavController().navigate(R.id.action_global_to_loginFragment)
    }

    /**
     * HU 10: Lanza el bottom sheet nativo del SO para compartir.
     */
    private fun lanzarChooserCompartir() {
        val mensaje = "${getString(R.string.compartir_titulo_app)}\n" +
                      "${getString(R.string.compartir_eslogan)}\n" +
                      getString(R.string.compartir_url)

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, mensaje)
        }
        startActivity(Intent.createChooser(shareIntent, getString(R.string.compartir_chooser_titulo)))
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
     * Animación táctil sutil (HU 3.0 Criterio 7).
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
