package com.smartdaypulse.ui.timeline

import android.graphics.Color
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.smartdaypulse.R
import com.smartdaypulse.data.local.entity.ProductivityEntity
import com.smartdaypulse.domain.model.Task

data class HourData(
    val hour: Int,
    val tasks: List<Task>,
    val productivityLevel: Int
)

class TimelineAdapter(
    private val onTaskClick: (Task) -> Unit,
    private val onTaskLongClick: (Task) -> Unit,
    private val onTaskCompletedChange: (Task, Boolean) -> Unit
) : ListAdapter<HourData, TimelineAdapter.HourViewHolder>(HourDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hour, parent, false)
        return HourViewHolder(view)
    }

    override fun onBindViewHolder(holder: HourViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class HourViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvHour: TextView = itemView.findViewById(R.id.tvHour)
        private val viewProductivity: View = itemView.findViewById(R.id.viewProductivity)
        private val llTasks: LinearLayout = itemView.findViewById(R.id.llTasks)

        fun bind(hourData: HourData) {
            tvHour.text = String.format("%02d:00", hourData.hour)

            // Set productivity indicator color
            val color = getProductivityColor(hourData.productivityLevel)
            viewProductivity.setBackgroundColor(color)

            // Clear and add tasks
            llTasks.removeAllViews()
            hourData.tasks.forEach { task ->
                val taskView = createTaskView(task)
                llTasks.addView(taskView)
            }
        }

        private fun createTaskView(task: Task): View {
            val view = LayoutInflater.from(itemView.context)
                .inflate(R.layout.item_task, llTasks, false)

            val tvName = view.findViewById<TextView>(R.id.tvTaskName)
            val tvType = view.findViewById<TextView>(R.id.tvTaskType)
            val tvComplexity = view.findViewById<TextView>(R.id.tvComplexity)
            val cbCompleted = view.findViewById<CheckBox>(R.id.cbCompleted)
            val btnEdit = view.findViewById<ImageButton>(R.id.btnEdit)

            tvName.text = task.name
            tvType.text = task.type.displayName()
            tvComplexity.text = "Сложность: ${task.complexity}"
            cbCompleted.isChecked = task.isCompleted

            // Strikethrough if completed
            if (task.isCompleted) {
                tvName.paintFlags = tvName.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                tvName.paintFlags = tvName.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onTaskCompletedChange(task, isChecked)
            }

            btnEdit.setOnClickListener { onTaskClick(task) }
            view.setOnLongClickListener {
                onTaskLongClick(task)
                true
            }

            return view
        }

        private fun getProductivityColor(level: Int): Int {
            return when (level) {
                0 -> Color.parseColor("#FFCDD2") // Red 100
                1 -> Color.parseColor("#FFECB3") // Amber 100
                2 -> Color.parseColor("#FFF9C4") // Yellow 100
                3 -> Color.parseColor("#DCEDC8") // Light Green 100
                4 -> Color.parseColor("#C8E6C9") // Green 100
                5 -> Color.parseColor("#A5D6A7") // Green 200
                else -> Color.parseColor("#E0E0E0")
            }
        }
    }

    class HourDiffCallback : DiffUtil.ItemCallback<HourData>() {
        override fun areItemsTheSame(oldItem: HourData, newItem: HourData): Boolean {
            return oldItem.hour == newItem.hour
        }

        override fun areContentsTheSame(oldItem: HourData, newItem: HourData): Boolean {
            return oldItem == newItem
        }
    }
}