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
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * SplashFragment — HU 1.0
 *
 * Criterio nuevo (sesión guardada):
 * Firebase Auth persiste la sesión automáticamente en el dispositivo.
 * Al arrancar la app, se verifica si hay un usuario autenticado:
 *   - SI hay sesión  → navega directo al Home (sin pasar por Login)
 *   - NO hay sesión → navega al Login como siempre
 *
 * Esto cubre el caso: usuario hizo login, cerró la app sin cerrar sesión,
 * y al volver no debe ver el Login de nuevo.
 */
class SplashFragment : Fragment() {

    private var _splashBinding: FragmentSplashBinding? = null
    private val splashBinding get() = _splashBinding!!

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

    private fun iniciarAnimacionBottle() {
        val bottleAnim = AnimationUtils.loadAnimation(requireContext(), R.anim.bottle_animation)
        splashBinding.ivBottleCartoon.startAnimation(bottleAnim)
    }

    /**
     * Espera los 5 segundos del splash y luego decide a dónde navegar
     * según si hay sesión activa en Firebase Auth.
     */
    private fun iniciarTemporizadorSplash() {
        viewLifecycleOwner.lifecycleScope.launch {
            delay(SPLASH_DURATION_MS)
            verificarSesionYNavegar()
        }
    }

    /**
     * Verifica si Firebase tiene un usuario autenticado.
     *
     * FirebaseAuth.getInstance().currentUser:
     *   - Devuelve el usuario si hizo login/registro previamente y NO cerró sesión.
     *   - Devuelve null si nunca inició sesión o si cerró sesión explícitamente.
     *
     * Firebase guarda esta sesión en el dispositivo de forma persistente —
     * no se pierde al cerrar la app, solo al llamar auth.signOut().
     */
    private fun verificarSesionYNavegar() {
        if (!isAdded) return

        val usuarioActual = FirebaseAuth.getInstance().currentUser

        if (usuarioActual != null) {
            // Hay sesión guardada → ir directo al Home
            findNavController().navigate(R.id.action_splashFragment_to_homeFragment)
        } else {
            // No hay sesión → ir al Login
            findNavController().navigate(R.id.action_splashFragment_to_loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _splashBinding = null
    }
}
