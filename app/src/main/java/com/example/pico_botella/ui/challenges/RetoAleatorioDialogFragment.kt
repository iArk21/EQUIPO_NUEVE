package com.example.pico_botella.ui.challenges

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import coil.load
import com.example.pico_botella.data.remote.PokemonApi
import com.example.pico_botella.databinding.DialogRetoAleatorioBinding
import kotlinx.coroutines.launch

class RetoAleatorioDialogFragment(
    private val reto: String,
    private val onDismissListener: () -> Unit
) : DialogFragment() {

    private var _binding: DialogRetoAleatorioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogRetoAleatorioBinding.inflate(inflater, container, false)
        
        // Criterio 6: El diálogo solo desaparece al dar clic en el botón "Cerrar"
        dialog?.setCanceledOnTouchOutside(false)
        dialog?.setCancelable(false)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Criterio 3: Un texto de color blanco (bold) que mostrará el reto a realizar
        binding.tvChallenge.text = reto

        // Criterio 2: Consumir API de Pokemones y mostrar imagen aleatoria
        loadRandomPokemon()

        // Criterio 4 y 5: Botón naranja "Cerrar" que reinicia la partida
        binding.btnClose.setOnClickListener {
            onDismissListener() // Callback para reanudar música y habilitar botón home
            dismiss()
        }
    }

    private fun loadRandomPokemon() {
        lifecycleScope.launch {
            try {
                val api = PokemonApi.create()
                val response = api.getPokedex()
                if (response.pokemon.isNotEmpty()) {
                    val randomPoke = response.pokemon.random()
                    // Aseguramos protocolo seguro si es necesario o permitimos http en manifest
                    val imgUrl = randomPoke.img.replace("http://", "https://")
                    binding.ivPokemon.load(imgUrl) {
                        crossfade(true)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Si falla la API, podrías poner una imagen por defecto
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Ajustar ancho del diálogo
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
