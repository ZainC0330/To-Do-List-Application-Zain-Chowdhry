package com.zchow.toodoo

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.zchow.toodoo.databinding.TaskItemCellBinding

class TaskItemAdapter(
    private val taskItems: MutableList<TaskItem>,
    private val clickListener: TaskItemClickListener,
    private var  taskViewModel: TaskViewModel
): RecyclerView.Adapter<TaskItemViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskItemViewHolder {
        val from = LayoutInflater.from(parent.context)
        val binding = TaskItemCellBinding.inflate(from, parent, false)
        return TaskItemViewHolder(parent.context, binding, clickListener, taskViewModel)
    }

    override fun getItemCount(): Int = taskItems.size

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: TaskItemViewHolder, position: Int) {
        holder.bindTaskItem(taskItems[position])
    }

    fun deleteTaskItem(taskItem: TaskItem) {
        val index = taskItems.indexOf(taskItem)
        if (index != -1) {
            taskItems.removeAt(index)
            notifyItemRemoved(index)
            taskViewModel.deleteTaskItems(taskItem)
        }
    }
}