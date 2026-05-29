package com.example.pico_botella.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pico_botella.databinding.FragmentHomeBinding

/**
 * HomeFragment: Pantalla principal del juego. (HU 2.0)
 * Muestra: fondo madera pino, toolbar personalizada, botella, contador y botón parpadeante.
 *
 * CAMBIOS DE VARIABLES vs versión anterior:
 * - _binding / binding  →  _homeBinding / homeBinding
 */
class HomeFragment : Fragment() {

    // VARIABLE RENOMBRADA: antes _binding, ahora _homeBinding
    private var _homeBinding: FragmentHomeBinding? = null

    // VARIABLE RENOMBRADA: antes binding, ahora homeBinding
    private val homeBinding get() = _homeBinding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // VARIABLE RENOMBRADA: antes _binding = null, ahora _homeBinding = null
        _homeBinding = null
    }
}
