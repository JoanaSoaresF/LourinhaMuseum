package com.example.lourinhamuseum.screens.scores

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lourinhamuseum.data.domain.Score
import com.example.lourinhamuseum.databinding.ScoreItemBinding

class ScoresAdapter : ListAdapter<Score, ScoresAdapter.ViewHolder>(ScoreDiffCallback()) {
    class ViewHolder private constructor(val binding: ScoreItemBinding) : RecyclerView
    .ViewHolder(binding.root) {
        fun bind(item: Score) {
            binding.score = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ScoreItemBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }
}


/**
 * Callback for calculating the differences between non-null items on the list.
 * Used by the List-Adapter to calculate the minimum number of changes between and old
 * list and a new list that's passed by submitList
 */
class ScoreDiffCallback : DiffUtil.ItemCallback<Score>() {
    override fun areItemsTheSame(oldItem: Score, newItem: Score): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: Score, newItem: Score): Boolean {
        return oldItem == newItem
    }

}