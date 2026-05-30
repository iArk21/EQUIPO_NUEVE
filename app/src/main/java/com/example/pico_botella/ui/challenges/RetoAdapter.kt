package com.example.pico_botella.ui.challenges

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.pico_botella.R
import com.example.pico_botella.data.local.RetoEntity
import com.example.pico_botella.databinding.ItemRetoBinding

/**
 * Adapter para el RecyclerView de retos.
 * Implementa animaciones táctiles y ListAdapter para eficiencia.
 */
class RetoAdapter(
    private val onEditClick: (RetoEntity) -> Unit,
    private val onDeleteClick: (RetoEntity) -> Unit
) : ListAdapter<RetoEntity, RetoAdapter.RetoViewHolder>(RetoDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RetoViewHolder {
        val binding = ItemRetoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RetoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RetoViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class RetoViewHolder(private val binding: ItemRetoBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(reto: RetoEntity) {
            binding.tvDescripcion.text = reto.descripcion

            // Criterio 7: Animación táctil de 150ms
            val touchAnimation = AnimationUtils.loadAnimation(binding.root.context, R.anim.button_touch)

            binding.btnEdit.setOnClickListener {
                it.startAnimation(touchAnimation)
                binding.root.postDelayed({ onEditClick(reto) }, 150)
            }

            binding.btnDelete.setOnClickListener {
                it.startAnimation(touchAnimation)
                binding.root.postDelayed({ onDeleteClick(reto) }, 150)
            }
        }
    }

    class RetoDiffCallback : DiffUtil.ItemCallback<RetoEntity>() {
        override fun areItemsTheSame(oldItem: RetoEntity, newItem: RetoEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: RetoEntity, newItem: RetoEntity) = oldItem == newItem
    }
}
