package com.example.pico_botella.ui.challenges

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.example.pico_botella.databinding.DialogEliminarRetoBinding

/**
 * Diálogo para confirmar la eliminación de un reto.
 */
class EliminarRetoDialogFragment(private val onConfirm: () -> Unit) : DialogFragment() {

    private var _binding: DialogEliminarRetoBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogEliminarRetoBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(binding.root)

        val dialog = builder.create()

        // Criterio 6: No cerrar al dar click fuera
        dialog.setCanceledOnTouchOutside(false)
        dialog.setCancelable(false)


        binding.btnDelete.setOnClickListener {
            onConfirm()
            dismiss()
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
