package com.example.taskmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ViewTaskListBinding
import com.example.taskmanager.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRVBindingAdapter(
    private val deleteUpdateCallback : (type:String,position: Int, task: Task) -> Unit
) :
    RecyclerView.Adapter<TaskRVBindingAdapter.ViewHolder>(){
    private val taskList = arrayListOf<Task>()


    class ViewHolder(val viewTaskLayoutBinding: ViewTaskListBinding)
        : RecyclerView.ViewHolder(viewTaskLayoutBinding.root)

    fun addAllTask(newTaskList : List<Task>){
        taskList.clear()
        taskList.addAll(newTaskList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            ViewTaskListBinding.inflate(LayoutInflater.from(parent.context),parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        holder.viewTaskLayoutBinding.txtTitle.text = task.title
        holder.viewTaskLayoutBinding.txtDesc.text = task.description

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

        holder.viewTaskLayoutBinding.txtDate.text = dateFormat.format(task.date)

        holder.viewTaskLayoutBinding.imgDel.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("delete",holder.adapterPosition, task)
            }
        }
        holder.viewTaskLayoutBinding.imgEdit.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("update",holder.adapterPosition, task)
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }


}