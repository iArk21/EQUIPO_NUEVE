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
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.databinding.FragmentRetosBinding
import com.example.pico_botella.ui.toolbar.ToolbarViewModel
import dagger.hilt.android.AndroidEntryPoint

/**
 * Fragmento que gestiona la lista de retos.
 * @AndroidEntryPoint: Habilita la inyección de dependencias en este Fragmento.
 * Permite que Hilt proporcione automáticamente los ViewModels.
 */
@AndroidEntryPoint
class RetosFragment : Fragment() {

    private var _binding: FragmentRetosBinding? = null
    private val binding get() = _binding!!

    /**
     * Hilt se encarga de inyectar el ViewModel compartido sin necesidad de una Factory manual.
     */
    private val toolbarViewModel: ToolbarViewModel by activityViewModels()

    /**
     * Hilt se encarga de inyectar el ViewModel de retos automáticamente.
     */
    private val viewModel: RetosViewModel by viewModels()

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
