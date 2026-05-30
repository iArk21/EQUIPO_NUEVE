package com.example.pico_botella.ui.challenges

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.databinding.DialogEditarRetoBinding

/**
 * Diálogo para editar un reto existente.
 */
class EditarRetoDialogFragment(
    private val reto: RetoEntity,
    private val onUpdate: (RetoEntity) -> Unit
) : DialogFragment() {

    private var _binding: DialogEditarRetoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEditarRetoBinding.inflate(layoutInflater)

        binding.etDescripcion.setText(reto.descripcion)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        binding.btnUpdate.setOnClickListener {
            val desc = binding.etDescripcion.text.toString()
            if (desc.isNotBlank()) {
                onUpdate(reto.copy(descripcion = desc))
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
