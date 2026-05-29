package com.example.pico_botella.ui.splash

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.pico_botella.R
import com.example.pico_botella.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SplashFragment: Pantalla de inicio de la aplicación. (HU 1.0)
 * Muestra una animación de botella cartoon y navega al Home tras 5 segundos.
 *
 * CAMBIOS DE VARIABLES vs versión anterior:
 * - _binding / binding  →  _splashBinding / splashBinding
 * - animation           →  bottleAnim
 * - ivBottle            →  ivBottleCartoon (en el layout)
 * - tvAppName           →  tvTituloJuego (en el layout)
 */
class SplashFragment : Fragment() {

    // VARIABLE RENOMBRADA: antes _binding, ahora _splashBinding
    private var _splashBinding: FragmentSplashBinding? = null

    // VARIABLE RENOMBRADA: antes binding, ahora splashBinding
    private val splashBinding get() = _splashBinding!!

    // Constante para el tiempo del splash (5 segundos - HU 1.0 Criterio 4)
    companion object {
        private const val SPLASH_DURATION_MS = 5000L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _splashBinding = FragmentSplashBinding.inflate(inflater, container, false)
        return splashBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        iniciarAnimacionBottle()
        iniciarTemporizadorSplash()
    }

    /**
     * Inicia la animación de la botella cartoon.
     * Usa la nueva animación bottle_animation (balanceo + flotación).
     * VARIABLE RENOMBRADA: antes 'animation', ahora 'bottleAnim'
     */
    private fun iniciarAnimacionBottle() {
        // VARIABLE RENOMBRADA: antes 'animation', ahora 'bottleAnim'
        val bottleAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.bottle_animation)
        // ivBottleCartoon es el nuevo ID del ImageView (antes ivBottle)
        splashBinding.ivBottleCartoon.startAnimation(bottleAnim)
    }

    /**
     * Temporizador de 5 segundos usando corrutinas (HU 1.0 Criterio 4).
     */
    private fun iniciarTemporizadorSplash() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            navegarAlHome()
        }
    }

    /**
     * Navegación segura al HomeFragment.
     * El popUpTo en nav_graph.xml limpia el Splash del backstack
     * (HU 1.0 Criterio 5: botón atrás desde Home no regresa al Splash).
     */
    private fun navegarAlHome() {
        if (isAdded) {
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Limpiar binding para evitar memory leaks
        // VARIABLE RENOMBRADA: antes _binding = null, ahora _splashBinding = null
        _splashBinding = null
    }
}
