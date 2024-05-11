package com.example.taskmanager.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.taskmanager.databinding.ViewTaskGridBinding
import com.example.taskmanager.databinding.ViewTaskListBinding
import com.example.taskmanager.models.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskRVBListAdapter(
    private val isList: MutableLiveData<Boolean>,
    private val deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
) :
    ListAdapter<Task, RecyclerView.ViewHolder>(DiffCallback()) {
    class ListTaskViewHolder(private val viewTaskListLayoutBinding: ViewTaskListBinding) :
        RecyclerView.ViewHolder(viewTaskListLayoutBinding.root) {

        fun bind(
            task: Task,
            deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
        ) {
            viewTaskListLayoutBinding.txtTitle.text = task.title
            viewTaskListLayoutBinding.txtDesc.text = task.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

            viewTaskListLayoutBinding.txtDate.text = dateFormat.format(task.date)

            viewTaskListLayoutBinding.imgDel.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("delete", adapterPosition, task)
                }
            }
            viewTaskListLayoutBinding.imgEdit.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("update", adapterPosition, task)
                }
            }
        }
    }

    class GridTaskViewHolder(private val viewTaskGridLayoutBinding: ViewTaskGridBinding) :
        RecyclerView.ViewHolder(viewTaskGridLayoutBinding.root) {

        fun bind(
            task: Task,
            deleteUpdateCallback: (type: String, position: Int, task: Task) -> Unit,
        ) {
            viewTaskGridLayoutBinding.txtTitle.text = task.title
            viewTaskGridLayoutBinding.txtDesc.text = task.description

            val dateFormat = SimpleDateFormat("dd-MMM-yyyy HH:mm:ss a", Locale.getDefault())

            viewTaskGridLayoutBinding.txtDate.text = dateFormat.format(task.date)

            viewTaskGridLayoutBinding.imgDel.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("delete", adapterPosition, task)
                }
            }
            viewTaskGridLayoutBinding.imgEdit.setOnClickListener {
                if (adapterPosition != -1) {
                    deleteUpdateCallback("update", adapterPosition, task)
                }
            }
        }
    }


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): RecyclerView.ViewHolder {
        return if (viewType == 1){  // Grid_Item
            GridTaskViewHolder(
                ViewTaskGridBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }else{  // List_Item
            ListTaskViewHolder(
                ViewTaskListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val task = getItem(position)

        if (isList.value!!){
            (holder as ListTaskViewHolder).bind(task,deleteUpdateCallback)
        }else{
            (holder as GridTaskViewHolder).bind(task,deleteUpdateCallback)
        }

    }

    override fun getItemViewType(position: Int): Int {
        return if (isList.value!!){
            0 // List_Item
        }else{
            1 // Grid_Item
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