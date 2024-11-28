package com.zchow.toodoo

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.time.LocalDate
import java.time.LocalDate.now
import java.time.LocalTime
import java.util.UUID
import android.app.Application
import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskViewModel(private val repository: TaskItemRepository): ViewModel(){

    var taskItems: LiveData<List<TaskItem>> = repository.allTaskItems.asLiveData()

    fun addTaskItems(newTask: TaskItem) = viewModelScope.launch{
        val list = taskItems.value ?: mutableListOf()
        repository.insertTaskItem(newTask)
    }

    fun updateTaskItems(id: Int, name: String, desc: String, dueTime: String, dueDate: String) = viewModelScope.launch{
        val list = taskItems.value
        val task = list!!.find{it.id==id}!!
        task.name = name
        task.desc = desc
        task.dueTime = dueTime
        task.dueDate = dueDate
        repository.updateTaskItem(task)
    }

    fun deleteTaskItems(taskItem: TaskItem) = viewModelScope.launch{
        val newList = taskItems.value?.toMutableList()
        newList?.remove(taskItem)
        repository.deleteTaskItem(taskItem)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setCompleted(taskItem: TaskItem) = viewModelScope.launch{
        val list = taskItems.value
        val task = list!!.find{it.id==taskItem.id}!!
        if(task.completedDate==null){
            taskItem.completedDate = LocalDate.now().toString()
        }
        if(!taskItem.isCompleted()){
            taskItem.completedDate = TaskItem.dateFormatter.format(LocalDate.now())
        }
        repository.updateTaskItem(taskItem)
    }
}

class TaskItemModelFactory(private val repository: TaskItemRepository): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(TaskViewModel::class.java)){
            return TaskViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown Class for View Model")
    }
}