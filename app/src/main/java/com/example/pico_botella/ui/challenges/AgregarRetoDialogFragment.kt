package com.example.pico_botella.ui.challenges

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
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

        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
