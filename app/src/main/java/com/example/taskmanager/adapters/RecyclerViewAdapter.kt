package com.example.taskmanager.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.R
import com.example.taskmanager.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class RecyclerViewAdapter(
    private val deleteUpdateCallback : (type:String,position: Int, task: Task) -> Unit
) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>(){

    private val taskList = arrayListOf<Task>()


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTxt: TextView = itemView.findViewById(R.id.txtTitle)
        val descrTxt: TextView = itemView.findViewById(R.id.txtDesc)
        val dateTxt: TextView = itemView.findViewById(R.id.txtDate)

        val deleteImg : ImageView = itemView.findViewById(R.id.imgDel)
        val editImg : ImageView = itemView.findViewById(R.id.imgEdit)

    }

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
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_task_list, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        holder.titleTxt.text = task.title
        holder.descrTxt.text = task.description

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

        holder.dateTxt.text = dateFormat.format(task.date)

        holder.deleteImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("delete",holder.adapterPosition, task)
            }
        }
        holder.editImg.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("update",holder.adapterPosition, task)
            }
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }
}