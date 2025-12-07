package com.smartdaypulse.ui.diary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.slider.Slider
import com.smartdaypulse.R

class DiaryAdapter(
    private val onLevelChanged: (Int, Int) -> Unit
) : RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder>() {

    private val levels = MutableList(24) { 3 }

    fun setLevels(newLevels: List<Int>) {
        if (newLevels.size == 24) {
            newLevels.forEachIndexed { index, level ->
                levels[index] = level
            }
            notifyDataSetChanged()
        }
    }

    fun getLevels(): List<Int> = levels.toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_diary_hour, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        holder.bind(position, levels[position])
    }

    override fun getItemCount(): Int = 24

    inner class DiaryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHour: TextView = itemView.findViewById(R.id.tvHour)
        private val sliderLevel: Slider = itemView.findViewById(R.id.sliderLevel)
        private val tvLevel: TextView = itemView.findViewById(R.id.tvLevel)

        fun bind(hour: Int, level: Int) {
            tvHour.text = String.format("%02d:00", hour)
            sliderLevel.value = level.toFloat()
            tvLevel.text = level.toString()

            sliderLevel.addOnChangeListener { _, value, fromUser ->
                if (fromUser) {
                    val newLevel = value.toInt()
                    levels[hour] = newLevel
                    tvLevel.text = newLevel.toString()
                    onLevelChanged(hour, newLevel)
                }
            }
        }
    }
}