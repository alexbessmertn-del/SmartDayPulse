package com.smartdaypulse.ui.diary

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.smartdaypulse.R
import com.smartdaypulse.data.local.entity.ProductivityEntity

class DiaryDialogFragment : DialogFragment() {

    private var productivityList: List<ProductivityEntity> = emptyList()
    private var onSave: ((List<Int>) -> Unit)? = null

    private lateinit var adapter: DiaryAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = layoutInflater.inflate(R.layout.dialog_diary, null)
        val rvDiary = view.findViewById<RecyclerView>(R.id.rvDiary)

        adapter = DiaryAdapter { _, _ -> }
        rvDiary.layoutManager = LinearLayoutManager(requireContext())
        rvDiary.adapter = adapter

        // Set existing productivity levels
        if (productivityList.size == 24) {
            adapter.setLevels(productivityList.map { it.level })
        }

        return AlertDialog.Builder(requireContext())
            .setTitle("Дневник продуктивности")
            .setView(view)
            .setPositiveButton("Сохранить") { _, _ ->
                onSave?.invoke(adapter.getLevels())
            }
            .setNegativeButton("Отмена", null)
            .create()
    }

    companion object {
        fun newInstance(
            productivityList: List<ProductivityEntity>,
            onSave: (List<Int>) -> Unit
        ): DiaryDialogFragment {
            return DiaryDialogFragment().apply {
                this.productivityList = productivityList
                this.onSave = onSave
            }
        }
    }
}