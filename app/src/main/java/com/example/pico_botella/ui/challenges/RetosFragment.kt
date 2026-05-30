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
 * Fragmento que gestiona la lista de retos.
 * Implementa MVVM, Room y control de audio.
 */
class RetosFragment : Fragment() {

    private var _binding: FragmentRetosBinding? = null
    private val binding get() = _binding!!

    // ViewModel compartido para audio (Criterio 1)
    private val toolbarViewModel: ToolbarViewModel by activityViewModels {
        ToolbarViewModelFactory(ToolbarRepository())
    }

    // ViewModel específico para retos (Criterio 4)
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
        // Criterio 3: Flecha de regreso y navegación
        binding.btnBack.setOnClickListener {
            handleMusicOnExit()
            findNavController().popBackStack()
        }
    }

    private fun setupRecyclerView() {
        adapter = RetoAdapter(
            onEditClick = { reto -> showEditDialog(reto) },
            onDeleteClick = { reto -> showDeleteDialog(reto) }
        )
        binding.rvRetos.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@RetosFragment.adapter
        }
    }

    private fun setupListeners() {
        // Criterio 8: FAB para agregar reto
        binding.fabAddReto.setOnClickListener {
            showAddDialog()
        }
    }

    private fun observeViewModel() {
        viewModel.allRetos.observe(viewLifecycleOwner) { retos ->
            adapter.submitList(retos)
        }
    }

    private fun handleMusicOnEnter() {
        // Criterio 1: Pausar música si está en ON
        if (toolbarViewModel.isAudioEnabled.value) {
            viewModel.setRestoreMusic(true)
            toolbarViewModel.setMusicPausedTemporarily(true)
        }
    }

    private fun handleMusicOnExit() {
        // Criterio 1 y 3: Restaurar música si inicialmente estaba en ON
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

    private fun showDeleteDialog(reto: RetoEntity) {
        EliminarRetoDialogFragment {
            viewModel.delete(reto)
        }.show(parentFragmentManager, "EliminarRetoDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Asegurar restauración al salir
        handleMusicOnExit()
        _binding = null
    }
}
