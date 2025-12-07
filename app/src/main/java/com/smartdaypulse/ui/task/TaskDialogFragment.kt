package com.smartdaypulse.ui.task

import android.app.Dialog
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.ChipGroup
import com.google.android.material.slider.Slider
import com.google.android.material.textfield.TextInputEditText
import com.smartdaypulse.R
import com.smartdaypulse.domain.model.Task
import com.smartdaypulse.domain.model.TaskType

class TaskDialogFragment : DialogFragment() {

    private var task: Task? = null
    private var onSave: ((String, TaskType, Int) -> Unit)? = null
    private var onUpdate: ((Task) -> Unit)? = null
    private var onDelete: ((Long) -> Unit)? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_task, null)

        val etName = view.findViewById<TextInputEditText>(R.id.etTaskName)
        val cgType = view.findViewById<ChipGroup>(R.id.cgTaskType)
        val sliderComplexity = view.findViewById<Slider>(R.id.sliderComplexity)
        val tvComplexityValue = view.findViewById<TextView>(R.id.tvComplexityValue)

        // Set up complexity slider
        sliderComplexity.addOnChangeListener { _, value, _ ->
            tvComplexityValue.text = value.toInt().toString()
        }

        // If editing, populate fields
        task?.let { existingTask ->
            etName.setText(existingTask.name)
            sliderComplexity.value = existingTask.complexity.toFloat()
            tvComplexityValue.text = existingTask.complexity.toString()

            when (existingTask.type) {
                TaskType.MENTAL -> cgType.check(R.id.chipMental)
                TaskType.PHYSICAL -> cgType.check(R.id.chipPhysical)
                TaskType.ROUTINE -> cgType.check(R.id.chipRoutine)
            }
        }

        val builder = AlertDialog.Builder(requireContext())
            .setTitle(if (task == null) "Новая задача" else "Редактировать задачу")
            .setView(view)
            .setPositiveButton("Сохранить") { _, _ ->
                val name = etName.text?.toString()?.trim() ?: ""
                if (name.isNotEmpty()) {
                    val type = when (cgType.checkedChipId) {
                        R.id.chipMental -> TaskType.MENTAL
                        R.id.chipPhysical -> TaskType.PHYSICAL
                        else -> TaskType.ROUTINE
                    }
                    val complexity = sliderComplexity.value.toInt()

                    if (task == null) {
                        onSave?.invoke(name, type, complexity)
                    } else {
                        onUpdate?.invoke(task!!.copy(
                            name = name,
                            type = type,
                            complexity = complexity
                        ))
                    }
                }
            }
            .setNegativeButton("Отмена", null)

        // Add delete button if editing
        if (task != null) {
            builder.setNeutralButton("Удалить") { _, _ ->
                onDelete?.invoke(task!!.id)
            }
        }

        return builder.create()
    }

    companion object {
        fun newInstance(
            task: Task? = null,
            onSave: ((String, TaskType, Int) -> Unit)? = null,
            onUpdate: ((Task) -> Unit)? = null,
            onDelete: ((Long) -> Unit)? = null
        ): TaskDialogFragment {
            return TaskDialogFragment().apply {
                this.task = task
                this.onSave = onSave
                this.onUpdate = onUpdate
                this.onDelete = onDelete
            }
        }
    }
}