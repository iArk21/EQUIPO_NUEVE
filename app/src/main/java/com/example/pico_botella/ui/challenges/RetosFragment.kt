package com.example.pico_botella.ui.challenges

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pico_botella.data.local.AppDatabase
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.data.local.RetoRepository
import com.example.pico_botella.databinding.FragmentRetosBinding
import com.example.pico_botella.ui.toolbar.ToolbarRepository
import com.example.pico_botella.ui.toolbar.ToolbarViewModel
import com.example.pico_botella.ui.toolbar.ToolbarViewModelFactory

/**
 * Fragmento que gestiona la lista de retos — HU 6.0.
 * Coordina los diálogos de HU 7.0, HU 8.0 y HU 9.0.
 *
 * CAMBIO vs versión anterior:
 * - showDeleteDialog(reto) ahora pasa el RetoEntity completo a EliminarRetoDialogFragment,
 *   necesario para que HU 9.0 Criterio 3 muestre la descripción del reto a eliminar.
 */
class RetosFragment : Fragment() {

    private var _binding: FragmentRetosBinding? = null
    private val binding get() = _binding!!

    // ViewModel compartido para audio (HU 6.0 Criterio 1)
    private val toolbarViewModel: ToolbarViewModel by activityViewModels {
        ToolbarViewModelFactory(ToolbarRepository())
    }

    // ViewModel específico para retos
    private val viewModel: RetosViewModel by viewModels {
        val database = AppDatabase.getDatabase(requireContext())
        RetosViewModelFactory(RetoRepository(database.retoDao()))
    }

    private lateinit var adapter: RetoAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRetosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupToolbar()
        setupRecyclerView()
        setupListeners()
        observeViewModel()
        handleMusicOnEnter()
    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            handleMusicOnExit()
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = RetoAdapter(
            onEditClick  = { reto -> showEditDialog(reto) },
            onDeleteClick = { reto -> showDeleteDialog(reto) }
        )
        binding.rvRetos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RetosFragment.adapter
        }
    }

    private fun setupListeners() {
        binding.fabAddReto.setOnClickListener { showAddDialog() }
    }

    private fun observeViewModel() {
        viewModel.allRetos.observe(viewLifecycleOwner) { retos ->
            adapter.submitList(retos)
        }
    }

    private fun handleMusicOnEnter() {
        if (toolbarViewModel.isAudioEnabled.value) {
            viewModel.setRestoreMusic(true)
            toolbarViewModel.setMusicPausedTemporarily(true)
        }
    }

    private fun handleMusicOnExit() {
        if (viewModel.shouldRestoreMusic.value) {
            toolbarViewModel.setMusicPausedTemporarily(false)
        }
    }

    private fun showAddDialog() {
        AgregarRetoDialogFragment { descripcion ->
            viewModel.insert(descripcion)
        }.show(parentFragmentManager, "AgregarRetoDialog")
    }

    private fun showEditDialog(reto: RetoEntity) {
        EditarRetoDialogFragment(reto) { updatedReto ->
            viewModel.update(updatedReto)
        }.show(parentFragmentManager, "EditarRetoDialog")
    }

    /**
     * CORREGIDO para HU 9.0:
     * Ahora pasa el RetoEntity completo al diálogo, no solo el callback.
     * Esto permite que EliminarRetoDialogFragment muestre la descripción
     * del reto (Criterio 3) y ejecute el delete correcto (Criterio 5).
     */
    private fun showDeleteDialog(reto: RetoEntity) {
        EliminarRetoDialogFragment(reto) {
            viewModel.delete(reto)
        }.show(parentFragmentManager, "EliminarRetoDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handleMusicOnExit()
        _binding = null
    }
}
