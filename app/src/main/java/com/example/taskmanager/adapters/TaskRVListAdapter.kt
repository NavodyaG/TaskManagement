package com.example.taskmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ViewTaskListBinding
import com.example.taskmanager.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRVListAdapter(
    private val deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
) :
    ListAdapter<Task, TaskRVListAdapter.ViewHolder>(DiffCallback()){
    class ViewHolder(val viewTaskLayoutBinding: ViewTaskListBinding) :
        RecyclerView.ViewHolder(viewTaskLayoutBinding.root)


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        return ViewHolder(
            ViewTaskListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = getItem(position)

        holder.viewTaskLayoutBinding.txtTitle.text = task.title
        holder.viewTaskLayoutBinding.txtDesc.text = task.description

        val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

        holder.viewTaskLayoutBinding.txtDate.text = dateFormat.format(task.date)

        holder.viewTaskLayoutBinding.imgDel.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("delete", holder.adapterPosition, task)
            }
        }
        holder.viewTaskLayoutBinding.imgEdit.setOnClickListener {
            if (holder.adapterPosition != -1) {
                deleteUpdateCallback("update", holder.adapterPosition, task)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }

    }
}