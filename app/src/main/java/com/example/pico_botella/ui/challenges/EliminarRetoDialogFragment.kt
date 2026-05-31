package com.example.pico_botella.ui.challenges

import android.app.Dialog
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pico_botella.R
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.databinding.DialogEliminarRetoBinding

/**
 * HU 9.0 — Cuadro de diálogo eliminar reto.
 *
 * Criterio 1: Fondo blanco (definido en dialog_eliminar_reto.xml).
 * Criterio 2: Título "¿Desea eliminar el siguiente reto?:", centrado, negro, bold (en XML).
 * Criterio 3: Texto con la descripción del reto a eliminar, cargado desde la base de datos.
 * Criterio 4: Texto "NO" naranja — cierra el diálogo, regresa a HU 6.0 sin eliminar nada.
 * Criterio 5: Texto "SI" naranja — elimina el reto de SQLite via ViewModel;
 *             el RecyclerView de HU 6.0 se actualiza automáticamente (Room Flow/LiveData).
 * Criterio 6: El diálogo NO se cierra al tocar fuera — solo con "NO" o "SI".
 *
 * @param reto       El RetoEntity a eliminar, obtenido desde la base de datos.
 * @param onConfirm  Callback ejecutado cuando el usuario confirma con "SI".
 */
class EliminarRetoDialogFragment(
    private val reto: RetoEntity,
    private val onConfirm: () -> Unit
) : DialogFragment() {

    private var _binding: DialogEliminarRetoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEliminarRetoBinding.inflate(layoutInflater)

        // Criterio 3: Mostrar la descripción del reto que se va a eliminar.
        // Esta información viene de la base de datos SQLite a través del ViewModel.
        binding.tvDescripcionReto.text = reto.descripcion

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        // Criterio 6: El diálogo solo desaparece al dar clic en "NO" o "SI".
        // Tocar fuera NO cierra el diálogo.
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        configurarBotones()

        return dialog
    }

    /**
     * Configura los listeners de los botones NO y SI.
     */
    private fun configurarBotones() {

        // Criterio 4: Botón "NO" — naranja, cierra el diálogo sin eliminar.
        // Deja al usuario en la ventana de HU 6.0 (Agregar y listar retos).
        binding.btnNo.setOnClickListener { view ->
            animarBoton(view) {
                dismiss()
            }
        }

        // Criterio 5: Botón "SI" — naranja, elimina el reto de SQLite.
        // El callback onConfirm() llama a viewModel.delete(reto) en RetosFragment,
        // Room ejecuta el DELETE en la BD y el Flow notifica al LiveData,
        // lo que remueve el item del RecyclerView automáticamente en HU 6.0.
        binding.btnSi.setOnClickListener { view ->
            animarBoton(view) {
                onConfirm()
                dismiss()
            }
        }
    }

    /**
     * Aplica animación táctil sutil antes de ejecutar la acción (150ms).
     * Consistente con la animación de los botones editar/eliminar del RecyclerView
     * definida en HU 6.0 Criterio 7.
     *
     * @param view   La vista a animar.
     * @param accion Bloque a ejecutar tras la animación.
     */
    private fun animarBoton(view: android.view.View, accion: () -> Unit) {
        val animToque = AnimationUtils.loadAnimation(requireContext(), R.anim.button_touch)
        view.startAnimation(animToque)
        view.postDelayed(accion, 150)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
