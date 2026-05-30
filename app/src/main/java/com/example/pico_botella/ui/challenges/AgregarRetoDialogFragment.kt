package com.example.pico_botella.ui.challenges

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.content.res.ColorStateList
import android.graphics.Color
import android.text.Editable
import android.text.TextWatcher
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pico_botella.databinding.DialogAgregarRetoBinding

/**
 * Diálogo para agregar un nuevo reto.
 */
class AgregarRetoDialogFragment(private val onSave: (String) -> Unit) : DialogFragment() {

    private var _binding: DialogAgregarRetoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAgregarRetoBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)
        
        val dialog = builder.create()

        // Criterio 7: No cerrar al dar click fuera
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)

        // Criterio 5: Habilitar/Deshabilitar botón Guardar según texto
        binding.etDescripcion.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val hasText = !s.isNullOrBlank()
                binding.btnSave.isEnabled = hasText
                
                // Criterio 5 revisado: Cambiar color explícitamente a naranja solo si hay texto
                if (hasText) {
                    binding.btnSave.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF6D00"))
                } else {
                    binding.btnSave.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        binding.btnSave.setOnClickListener {
            val desc = binding.etDescripcion.text.toString()
            if (desc.isNotBlank()) {
                onSave(desc)
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
