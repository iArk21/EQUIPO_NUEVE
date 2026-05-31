package com.example.pico_botella.ui.challenges

import android.app.Dialog
import android.os.Bundle
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pico_botella.R
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.databinding.DialogEditarRetoBinding

/**
 * HU 8.0 — Cuadro de diálogo editar reto.
 *
 * Criterio 1: Fondo blanco (definido en dialog_editar_reto.xml).
 * Criterio 2: Título "Editar reto", centrado, negro, bold (en XML).
 * Criterio 3: Caja de texto con la descripción actual del reto (cargada desde BD).
 *             La línea de la caja es naranja (definida en XML con app:boxStrokeColor).
 * Criterio 4: Botón "Cancelar" naranja — cierra el diálogo y deja al usuario en HU 6.0.
 * Criterio 5: Botón "Guardar" naranja — siempre habilitado (el texto ya viene de la BD).
 * Criterio 6: Al dar click en "Guardar", guarda el reto modificado en SQLite via ViewModel
 *             y la lista se actualiza automáticamente en HU 6.0 (LiveData/Flow de Room).
 * Criterio 7: El diálogo NO se cierra al tocar fuera — solo con "Cancelar" o "Guardar".
 *
 * @param reto        El RetoEntity a editar, obtenido desde la base de datos.
 * @param onUpdate    Callback que recibe el RetoEntity actualizado para persistirlo.
 */
class EditarRetoDialogFragment(
    private val reto: RetoEntity,
    private val onUpdate: (RetoEntity) -> Unit
) : DialogFragment() {

    private var _binding: DialogEditarRetoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditarRetoBinding.inflate(layoutInflater)

        // Criterio 3: Cargar descripción actual del reto en la caja de texto.
        // Esta información viene de la base de datos (Room/SQLite).
        binding.etDescripcion.setText(reto.descripcion)

        // Posicionar el cursor al final del texto para facilitar la edición
        binding.etDescripcion.setSelection(reto.descripcion.length)

        val dialog = AlertDialog.Builder(requireContext())
            .setView(binding.root)
            .create()

        // Criterio 7: El diálogo solo desaparece con Cancelar o Guardar.
        // Si el usuario toca fuera, NO se cierra.
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        configurarBotones(dialog)

        return dialog
    }

    /**
     * Configura los listeners de los botones Cancelar y Guardar.
     */
    private fun configurarBotones(dialog: AlertDialog) {

        // Criterio 4: Botón "Cancelar" — naranja, cierra el diálogo.
        // Deja al usuario en la ventana de HU 6.0 (Agregar y listar retos).
        binding.btnCancel.setOnClickListener { view ->
            animarBoton(view) {
                dismiss()
            }
        }

        // Criterio 5 y 6: Botón "Guardar" — naranja, siempre habilitado.
        // A diferencia del HU 7 (Agregar), aquí el botón arranca habilitado
        // porque la caja ya tiene el texto del reto cargado desde la BD.
        // Al dar click:
        //   - Valida que la caja no esté vacía (validación defensiva)
        //   - Crea un RetoEntity actualizado con copy() manteniendo el mismo id
        //   - Llama al callback onUpdate, que en RetosFragment llama a viewModel.update()
        //   - Room actualiza la BD y el Flow notifica al LiveData automáticamente
        //   - El RecyclerView de HU 6.0 se actualiza sin acción adicional (Criterio 6)
        binding.btnUpdate.setOnClickListener { view ->
            val descripcionNueva = binding.etDescripcion.text.toString().trim()

            if (descripcionNueva.isBlank()) {
                // Si por alguna razón borró todo el texto, mostrar error en el campo
                binding.tilDescripcion.error = "El reto no puede estar vacío"
                return@setOnClickListener
            }

            // Limpiar error si existía
            binding.tilDescripcion.error = null

            animarBoton(view) {
                // Criterio 6: copy() conserva el id original para que Room haga UPDATE
                // (no INSERT) en la base de datos SQLite.
                val retoActualizado = reto.copy(descripcion = descripcionNueva)
                onUpdate(retoActualizado)
                dismiss()
            }
        }
    }

    /**
     * Aplica la animación táctil sutil antes de ejecutar la acción.
     * Criterio 7 de HU 6.0: animación de touch en botones de editar/eliminar.
     * Aquí se replica dentro del diálogo para consistencia visual.
     *
     * @param view   La vista que se va a animar.
     * @param accion El bloque a ejecutar tras la animación (150ms de retardo).
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
