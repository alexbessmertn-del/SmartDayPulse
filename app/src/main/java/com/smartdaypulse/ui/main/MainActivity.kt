package com.smartdaypulse.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.smartdaypulse.SmartDayApplication
import com.smartdaypulse.databinding.ActivityMainBinding
import com.smartdaypulse.domain.model.Task
import com.smartdaypulse.notification.NotificationScheduler
import com.smartdaypulse.ui.diary.DiaryDialogFragment
import com.smartdaypulse.ui.task.TaskDialogFragment
import com.smartdaypulse.ui.timeline.HourData
import com.smartdaypulse.ui.timeline.TimelineAdapter
import com.smartdaypulse.util.DateUtils
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val viewModel: MainViewModel by viewModels {
        val app = application as SmartDayApplication
        MainViewModel.Factory(
            app.taskRepository,
            app.productivityRepository,
            app.aiSchedulerRepository
        )
    }

    private lateinit var timelineAdapter: TimelineAdapter
    private lateinit var notificationScheduler: NotificationScheduler

    private val notificationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (!isGranted) {
            Toast.makeText(this, "Уведомления отключены", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        notificationScheduler = NotificationScheduler(this)

        setupToolbar()
        setupRecyclerView()
        setupClickListeners()
        observeUiState()
        requestNotificationPermission()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
    }

    private fun setupRecyclerView() {
        timelineAdapter = TimelineAdapter(
            onTaskClick = { task -> showEditTaskDialog(task) },
            onTaskLongClick = { task -> showDeleteConfirmation(task) },
            onTaskCompletedChange = { task, completed ->
                viewModel.toggleTaskCompleted(task.id, completed)
            }
        )

        binding.rvTimeline.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = timelineAdapter
        }
    }

    private fun setupClickListeners() {
        binding.btnPrevDay.setOnClickListener { viewModel.previousDay() }
        binding.btnNextDay.setOnClickListener { viewModel.nextDay() }

        binding.btnAddTask.setOnClickListener { showAddTaskDialog() }
        binding.btnDiary.setOnClickListener { showDiaryDialog() }
        binding.btnAiSort.setOnClickListener { performAiSort() }
    }

    private fun observeUiState() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    updateUi(state)
                }
            }
        }
    }

    private fun updateUi(state: UiState) {
        // Update date display
        binding.tvDate.text = DateUtils.formatForDisplay(state.selectedDate)

        // Update timeline
        val hourDataList = (0..23).map { hour ->
            HourData(
                hour = hour,
                tasks = state.tasksByHour[hour] ?: emptyList(),
                productivityLevel = state.productivity.find { it.hour == hour }?.level ?: 3
            )
        }
        timelineAdapter.submitList(hourDataList)

        // Update loading state
        binding.progressBar.isVisible = state.aiSortingInProgress
        binding.btnAiSort.isEnabled = !state.aiSortingInProgress

        // Show error message
        state.errorMessage?.let { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
            viewModel.clearMessages()
        }

        // Show success message
        state.successMessage?.let { message ->
            Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            viewModel.clearMessages()

            // Schedule notifications after successful AI sort
            if (message.contains("оптимизировано") || message.contains("резервный")) {
                scheduleNotifications(state.tasks, state.selectedDate)
            }
        }
    }

    private fun showAddTaskDialog() {
        TaskDialogFragment.newInstance(
            onSave = { name, type, complexity ->
                viewModel.addTask(name, type, complexity)
            }
        ).show(supportFragmentManager, "add_task")
    }

    private fun showEditTaskDialog(task: Task) {
        TaskDialogFragment.newInstance(
            task = task,
            onUpdate = { updatedTask -> viewModel.updateTask(updatedTask) },
            onDelete = { taskId -> viewModel.deleteTask(taskId) }
        ).show(supportFragmentManager, "edit_task")
    }

    private fun showDeleteConfirmation(task: Task) {
        MaterialAlertDialogBuilder(this)
            .setTitle("Удалить задачу?")
            .setMessage("Задача \"${task.name}\" будет удалена")
            .setPositiveButton("Удалить") { _, _ ->
                viewModel.deleteTask(task.id)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDiaryDialog() {
        val currentState = viewModel.uiState.value
        DiaryDialogFragment.newInstance(
            productivityList = currentState.productivity,
            onSave = { levels -> viewModel.saveProductivity(levels) }
        ).show(supportFragmentManager, "diary")
    }

    private fun performAiSort() {
        viewModel.sortWithAI()
    }

    private fun scheduleNotifications(tasks: List<Task>, date: String) {
        tasks.filter { !it.isCompleted }.forEach { task ->
            notificationScheduler.scheduleNotification(task, date)
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}