package com.example.petcare

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.example.petcare.databinding.FragmentAddToDoPopupBinding
import com.google.android.material.textfield.TextInputEditText
import java.time.LocalTime
import java.util.*

class AddToDoPopupFragment : DialogFragment() , DatePickerDialog.OnDateSetListener{
    private lateinit var binding: FragmentAddToDoPopupBinding
    private lateinit var listener : DialogNextBtnClickListener
    private var toDoData: ToDoData?= null
    private var dueTime: LocalTime? = null
    fun setListener(listener: DialogNextBtnClickListener){
        this.listener= listener
    }
    companion object{
        const val TAG = "AddToDoPopupFragment"
        @JvmStatic
        fun newInstance(taskId: String, task: String)= AddToDoPopupFragment().apply {
            arguments= Bundle().apply {
                putString("TaskId", taskId)
                putString("Task", task)
            }
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding= FragmentAddToDoPopupBinding.inflate(inflater,container, false)
        return binding.root

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null)
        {
            toDoData= ToDoData(arguments?.getString("TaskId", "Task").toString(), arguments?.getString("Task").toString())
        }
        binding.todoEt.setText(toDoData?.Task)
        registerevents()
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerevents() {
        binding.todoDateBtn.setOnClickListener {
            showDatePicker()
        }
        binding.todoTimeBtn.setOnClickListener {
            showTimePicker()
        }
        binding.todoNextBtn.setOnClickListener {
            val todoTask = binding.textViewDate.text.toString()+" "+ binding.textViewTime.text.toString()+" "+
                    binding.todoEt.text.toString()
            if (todoTask.isNotEmpty()) {
                if (toDoData==null) {
                    listener.onSaveTask(todoTask, binding.todoEt)
                }
                else{
                    toDoData?.Task= todoTask
                    listener.onUpdateTask(toDoData!!, binding.todoEt)
                }
            } else {
                Toast.makeText(context, "Please type some task", Toast.LENGTH_SHORT).show()
            }
            binding.todoClose.setOnClickListener {
                dismiss()
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun showTimePicker() {
        if(dueTime == null)
            dueTime = LocalTime.now()
        val listener = TimePickerDialog.OnTimeSetListener{ _, selectedHour, selectedMinute ->
            dueTime = LocalTime.of(selectedHour, selectedMinute)
            updateTimeButtonText()
        }
        val dialog = TimePickerDialog(activity, listener, dueTime!!.hour, dueTime!!.minute, true)
        dialog.setTitle("Task Due")
        dialog.show()

    }
    @RequiresApi(Build.VERSION_CODES.O)
    private fun updateTimeButtonText() {
        binding.todoTimeBtn.text = String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
        binding.textViewTime.text= String.format("%02d:%02d",dueTime!!.hour,dueTime!!.minute)
    }
    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)

        val datePicker = DatePickerDialog(
            requireActivity(),
            DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                binding.textViewDate.setText("" + dayOfMonth + "/" + month + "/" + year)
                binding.todoDateBtn.setText("" + dayOfMonth + "/" + month + "/" + year)
            },
            year,
            month,
            dayOfMonth
        )
        datePicker.show()
    }
    override fun onDateSet(p0: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month)
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
    }
}
interface DialogNextBtnClickListener{
    fun onSaveTask(todo: String, todoEt: TextInputEditText)
    fun onUpdateTask(ToDoData: ToDoData, todoEt: TextInputEditText)
}