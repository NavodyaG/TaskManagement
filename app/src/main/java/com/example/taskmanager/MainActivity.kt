package com.example.taskmanager

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.ImageView
import androidx.core.view.ViewCompat
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.taskmanager.adapters.TaskRVBListAdapter
import com.example.taskmanager.databinding.ActivityMainBinding
import com.example.taskmanager.models.Task
import com.example.taskmanager.utils.Status
import com.example.taskmanager.utils.StatusResult
import com.example.taskmanager.utils.clearEditText
import com.example.taskmanager.utils.hideKeyBoard
import com.example.taskmanager.utils.longToastShow
import com.example.taskmanager.utils.setupDialog
import com.example.taskmanager.utils.validateEditText
import com.example.taskmanager.viewmodels.TaskViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID

class MainActivity : AppCompatActivity() {

    private val mainBinding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val addTaskDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.add_task)
        }
    }

    private val editTaskDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.edit_task)
        }
    }

    private val loadTaskDialog: Dialog by lazy {
        Dialog(this, R.style.DialogCustomTheme).apply {
            setupDialog(R.layout.load_task)
        }
    }

    private val taskViewModel: TaskViewModel by lazy {
        ViewModelProvider(this)[TaskViewModel::class.java]
    }

    private val isListMutableLiveData = MutableLiveData<Boolean>().apply {
        postValue(true)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(mainBinding.root)

        //starting add task
        val addClose = addTaskDialog.findViewById<ImageView>(R.id.imgClose)
        addClose.setOnClickListener{addTaskDialog.dismiss()}

        val addTitle = addTaskDialog.findViewById<TextInputEditText>(R.id.taskTitle)
        val addTaskTitle = addTaskDialog.findViewById<TextInputLayout>(R.id.txtTaskTitle)

        addTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addTitle, addTaskTitle)
            }
        })

        val addDesc = addTaskDialog.findViewById<TextInputEditText>(R.id.taskDesc)
        val addTaskDesc = addTaskDialog.findViewById<TextInputLayout>(R.id.txtTaskDesc)

        addDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(addDesc, addTaskDesc)
            }
        })

        mainBinding.addBtn.setOnClickListener {
            clearEditText(addTitle, addTaskTitle)
            clearEditText(addDesc, addTaskDesc)
            addTaskDialog.show()
        }

        val saveTask = addTaskDialog.findViewById<Button>(R.id.saveBtn)
        saveTask.setOnClickListener {
            if (validateEditText(addTitle, addTaskTitle)
                && validateEditText(addDesc, addTaskDesc)
            ) {
                 val newTask = Task(
                      UUID.randomUUID().toString(),
                      addTitle.text.toString().trim(),
                      addDesc.text.toString().trim(),
                      Date()
                  )
                  hideKeyBoard(it)
                  addTaskDialog.dismiss()
                  taskViewModel.insertTask(newTask)
            }
        }

        //starting edit task
        val editClose = editTaskDialog.findViewById<ImageView>(R.id.imgClose)
        editClose.setOnClickListener{editTaskDialog.dismiss()}

        val editTitle = editTaskDialog.findViewById<TextInputEditText>(R.id.taskTitle)
        val editTaskTitle = editTaskDialog.findViewById<TextInputLayout>(R.id.txtTaskTitle)

        editTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(editTitle, editTaskTitle)
            }
        })

        val editDesc = editTaskDialog.findViewById<TextInputEditText>(R.id.taskDesc)
        val editTaskDesc = editTaskDialog.findViewById<TextInputLayout>(R.id.txtTaskDesc)

        editDesc.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(s: Editable) {
                validateEditText(editDesc, editTaskDesc)
            }
        })

        val editTask = editTaskDialog.findViewById<Button>(R.id.editBtn)

        isListMutableLiveData.observe(this){
            if (it){
                mainBinding.taskList.layoutManager = LinearLayoutManager(
                    this,LinearLayoutManager.VERTICAL,false
                )
                mainBinding.imgList.setImageResource(R.drawable.view_module)
            }else{
                mainBinding.taskList.layoutManager = StaggeredGridLayoutManager(
                    2,LinearLayoutManager.VERTICAL
                )
                mainBinding.imgList.setImageResource(R.drawable.view_list)
            }
        }

        mainBinding.imgList.setOnClickListener {
            isListMutableLiveData.postValue(!isListMutableLiveData.value!!)
        }


        val taskRVVBListAdapter = TaskRVBListAdapter(isListMutableLiveData ) { type, position, task ->
            if (type == "delete") {
                taskViewModel
                    // Deleted Task
                    .deleteTaskUsingId(task.id)

                // Restore Deleted task
                restoreDeletedTask(task)
            } else if (type == "update") {
                editTitle.setText(task.title)
                editDesc.setText(task.description)
                editTask.setOnClickListener {
                    if (validateEditText(editTitle, editTaskTitle)
                        && validateEditText(editDesc, editTaskDesc)
                    ) {
                        val updateTask = Task(
                            task.id,
                            editTitle.text.toString().trim(),
                            editDesc.text.toString().trim(),
                            Date()
                        )
                        hideKeyBoard(it)
                        loadTaskDialog.dismiss()
                        taskViewModel
                            .updateTask(updateTask)
//                            .updateTaskPaticularField(
//                                task.id,
//                                updateETTitle.text.toString().trim(),
//                                updateETDesc.text.toString().trim()
//                            )
                    }
                }
                loadTaskDialog.show()
            }
        }
        mainBinding.taskList.adapter = taskRVVBListAdapter
        ViewCompat.setNestedScrollingEnabled(mainBinding.taskList,false)
        taskRVVBListAdapter.registerAdapterDataObserver(object :
            RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                mainBinding.nestedScrollView.smoothScrollTo(0,positionStart)
            }
        })
        callGetTaskList(taskRVVBListAdapter)
        callSortByLiveData()
        statusCallback()
        callSearch()

    }

    private fun restoreDeletedTask(deletedTask : Task){
        val snackBar = Snackbar.make(
            mainBinding.root, "Deleted '${deletedTask.title}'",
            Snackbar.LENGTH_LONG
        )
        snackBar.setAction("Undo"){
            taskViewModel.insertTask(deletedTask)
        }
        snackBar.show()
    }

    private fun callSearch() {
        mainBinding.txtSearch.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(query: Editable) {
                if (query.toString().isNotEmpty()){
                    taskViewModel.searchTaskList(query.toString())
                }else{
                    callSortByLiveData()
                }
            }
        })

        mainBinding.txtSearch.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                hideKeyBoard(v)
                return@setOnEditorActionListener true
            }
            false
        }

        callSortByDialog()
    }
    private fun callSortByLiveData(){
        taskViewModel.sortByLiveData.observe(this){
            taskViewModel.getTaskList(it.second,it.first)
        }
    }

    private fun callSortByDialog() {
        var checkedItem = 0   // 2 is default item set
        val items = arrayOf("Title Ascending", "Title Descending","Date Ascending","Date Descending")

        mainBinding.imgSort.setOnClickListener {
            MaterialAlertDialogBuilder(this)
                .setTitle("Sort By")
                .setPositiveButton("Ok") { _, _ ->
                    when (checkedItem) {
                        0 -> {
                            taskViewModel.setSortBy(Pair("title",true))
                        }
                        1 -> {
                            taskViewModel.setSortBy(Pair("title",false))
                        }
                        2 -> {
                            taskViewModel.setSortBy(Pair("date",true))
                        }
                        else -> {
                            taskViewModel.setSortBy(Pair("date",false))
                        }
                    }
                }
                .setSingleChoiceItems(items, checkedItem) { _, selectedItemIndex ->
                    checkedItem = selectedItemIndex
                }
                .setCancelable(false)
                .show()
        }
    }

    private fun statusCallback() {
        taskViewModel
            .statusLiveData
            .observe(this) {
                when (it.status) {
                    Status.LOADING -> {
                        loadTaskDialog.show()
                    }

                    Status.SUCCESS -> {
                        loadTaskDialog.dismiss()
                        when (it.data as StatusResult) {
                            StatusResult.Added -> {
                                Log.d("StatusResult", "Added")
                            }

                            StatusResult.Deleted -> {
                                Log.d("StatusResult", "Deleted")

                            }

                            StatusResult.Updated -> {
                                Log.d("StatusResult", "Updated")

                            }
                        }
                        it.message?.let { it1 -> longToastShow(it1) }
                    }

                    Status.ERROR -> {
                        loadTaskDialog.dismiss()
                        it.message?.let { it1 -> longToastShow(it1) }
                    }
                }
            }
    }

    private fun callGetTaskList(taskRecyclerViewAdapter: TaskRVBListAdapter) {

        CoroutineScope(Dispatchers.Main).launch {
            taskViewModel
                .taskStateFlow
                .collectLatest {
                    Log.d("status", it.status.toString())

                    when (it.status) {
                        Status.LOADING -> {
                            loadTaskDialog.show()
                        }

                        Status.SUCCESS -> {
                            loadTaskDialog.dismiss()
                            it.data?.collect { taskList ->
                                taskRecyclerViewAdapter.submitList(taskList)
                            }
                        }

                        Status.ERROR -> {
                            loadTaskDialog.dismiss()
                            it.message?.let { it1 -> longToastShow(it1) }
                        }
                    }

                }
        }
    }
}