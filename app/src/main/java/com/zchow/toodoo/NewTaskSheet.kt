package com.zchow.toodoo

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.TextView
import android.widget.TimePicker
import androidx.annotation.Nullable
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.zchow.toodoo.databinding.FragmentNewTaskSheetBinding
import java.text.SimpleDateFormat
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.util.Calendar
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.zchow.toodoo.databinding.ActivityMainBinding
import com.zchow.toodoo.R

class NewTaskSheet(var taskItem: TaskItem?) : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNewTaskSheetBinding
    private lateinit var  taskViewModel: TaskViewModel
    var theTime: String = ""
    var theDate: String = ""
    
    override fun onStart() {
        super.onStart()
        val dialog = dialog as? BottomSheetDialog
        dialog?.let {
            val bottomSheet = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)//Sync gradle files if R unresolved reference.
            bottomSheet?.let { sheet ->
                val behavior = BottomSheetBehavior.from(sheet)
                behavior.peekHeight = 2000
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?){
        super.onViewCreated(view, savedInstanceState)
        val activity = requireActivity()
        val dueTime: Button = view.findViewById(R.id.dueTime)
        val dueDate: Button = view.findViewById(R.id.dueDate)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val tvTime: TextView = view.findViewById(R.id.tvTime)
        val tvIncomplete: TextView = view.findViewById(R.id.tvIncomplete)
        val clear: Button = view.findViewById(R.id.clear)
        val Cancel: ImageButton = view.findViewById(R.id.Cancel)

        dueTime.setOnClickListener{
           val cal = Calendar.getInstance()
           val timeSetListener = TimePickerDialog.OnTimeSetListener{view: TimePicker?, hour: Int, minute: Int ->
               cal.set(Calendar.HOUR_OF_DAY, hour)
               cal.set(Calendar.MINUTE, minute)
               theTime = SimpleDateFormat("HH:mm").format(cal.time)
               tvTime.text = theTime
           }
            TimePickerDialog(context,timeSetListener,cal.get(Calendar.HOUR_OF_DAY),cal.get(Calendar.MINUTE),true).show()
        }

        dueDate.setOnClickListener{
            val cal = Calendar.getInstance()
            val dateSetListener = DatePickerDialog.OnDateSetListener { view: DatePicker?, year, month, dayOfMonth ->
                cal.set(Calendar.YEAR, year)
                cal.set(Calendar.MONTH, month)
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                theDate = SimpleDateFormat("MM-dd-yyyy").format(cal.time)
                tvDate.text = theDate
            }
            context?.let {it1 -> DatePickerDialog(it1, dateSetListener, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show() }
        }

        if(taskItem != null){
            binding.taskTitle.text = "Edit Task"
            val editable = Editable.Factory.getInstance()
            binding.name.text = editable.newEditable(taskItem!!.name)
            binding.desc.text = editable.newEditable(taskItem!!.desc)
            tvTime.text = taskItem!!.dueTime
            tvDate.text = taskItem!!.dueDate
            theTime = taskItem!!.dueTime
            theDate = taskItem!!.dueDate
        }
        else{
            binding.taskTitle.text = "New Task"
        }

        taskViewModel = ViewModelProvider(activity).get(TaskViewModel::class.java)
        binding.saveButton.setOnClickListener{
            saveAction(tvIncomplete)
        }

        clear.setOnClickListener{
            theTime=""
            theDate=""
            tvTime.text = theTime
            tvDate.text = theDate
            binding.name.text = Editable.Factory.getInstance().newEditable("")
            binding.desc.text = Editable.Factory.getInstance().newEditable("")
        }

        Cancel.setOnClickListener{
            dismiss()
        }

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentNewTaskSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun saveAction(tvIncomplete: TextView) {
        val name = binding.name.text.toString()
        val desc = binding.desc.text.toString()

        if(taskItem == null){
            if(name.trim()==""){
                tvIncomplete.text = "Incomplete Task"
            }
            else{
                val newTask = TaskItem(name, desc, theTime, theDate, null)
                taskViewModel.addTaskItems(newTask)
                dismiss()
            }
        }
        else{
            if(name.trim()==""){
                tvIncomplete.text = "Incomplete Task"
            }
            else{
                taskViewModel.updateTaskItems(taskItem!!.id, name, desc, theTime, theDate)
                dismiss()
            }
        }
    }

}