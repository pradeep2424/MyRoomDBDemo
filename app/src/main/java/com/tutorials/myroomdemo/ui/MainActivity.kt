package com.tutorials.myroomdemo.ui

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.tutorials.myroomdemo.R
import com.tutorials.myroomdemo.adapter.ItemAdapter
import com.tutorials.myroomdemo.data.StudentDao
import com.tutorials.myroomdemo.data.StudentEntity
import com.tutorials.myroomdemo.databinding.ActivityMainBinding
import com.tutorials.myroomdemo.databinding.LayoutCustomDialogUpdateBinding
import com.tutorials.myroomdemo.utils.StudentApp
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var binding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val studentDao = (application as StudentApp).db.studentDao()
        lifecycleScope.launch {
            studentDao.getAllStudents().collect {
                val studentList = ArrayList(it)
                setupStudentList(studentList, studentDao)
            }
        }

        binding?.btnAdd?.setOnClickListener {
            addStudentRecord(studentDao)
        }
    }

    fun addStudentRecord(studentDao: StudentDao) {
        val name = binding?.etName?.text.toString()
        val email = binding?.etEmail?.text.toString()

        if (name.isNotBlank() && email.isNotBlank()) {
            lifecycleScope.launch {
                studentDao.insertStudent(StudentEntity(name = name, email = email))
                clearInputEditTexts()
                Toast.makeText(applicationContext, "Student Added", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(applicationContext, "Name or Email cannot be blank", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun clearInputEditTexts() {
        binding?.etName?.text?.clear()
        binding?.etEmail?.text?.clear()
    }

    private fun setupStudentList(
        studentList: ArrayList<StudentEntity>,
        studentDao: StudentDao
    ) {
        if (studentList.isNotEmpty()) {
            val itemAdapter = ItemAdapter(studentList,
                { updateId ->
                    updateRecordDialog(updateId, studentDao)
                },
                { deleteId ->
                    deleteRecordAlertDialog(deleteId, studentDao)
                }
            )
            binding?.recyclerview?.layoutManager = LinearLayoutManager(this)
            binding?.recyclerview?.adapter = itemAdapter
            binding?.recyclerview?.visibility = View.VISIBLE
            binding?.tvNoRecords?.visibility = View.GONE
        } else {
            binding?.recyclerview?.visibility = View.GONE
            binding?.tvNoRecords?.visibility = View.VISIBLE
        }

    }

    private fun updateRecordDialog(id: Int, studentDao: StudentDao) {
        val updateDialog =
            Dialog(this, R.style.Theme_Dialog)
        updateDialog.setCancelable(false)
        val binding = LayoutCustomDialogUpdateBinding.inflate(layoutInflater)
        updateDialog.setContentView(binding.root)

        lifecycleScope.launch {
            studentDao.getStudentById(id).collect {
                binding.etUpdateName.setText(it.name)
                binding.etUpdateEmailId.setText(it.email)
            }
        }

        binding.tvUpdate.setOnClickListener {
            val name = binding.etUpdateName.text.toString()
            val email = binding.etUpdateEmailId.text.toString()

            if (name.isNotEmpty() && email.isNotEmpty()) {
                lifecycleScope.launch {
                    studentDao.updateStudent(StudentEntity(name = name, email = email))
                    Toast.makeText(applicationContext, "Student Updated", Toast.LENGTH_SHORT).show()
                    updateDialog.dismiss()
                }
            } else {
                Toast.makeText(
                    applicationContext,
                    "Name or Email cannot be blank",
                    Toast.LENGTH_SHORT
                ).show()
            }

            binding.tvCancel.setOnClickListener {
                updateDialog.dismiss()
            }

            updateDialog.show()
        }
    }

    private fun deleteRecordAlertDialog(id: Int, studentDao: StudentDao) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Delete Record")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        lifecycleScope.launch {
            studentDao.getStudentById(id).collect {
                builder.setMessage("Are you sure you want to delete ${it.name}?")
            }
        }

        builder.setPositiveButton("Yes") { dialogInterface, _ ->
            lifecycleScope.launch {
                studentDao.deleteStudent(StudentEntity(id))
                Toast.makeText(applicationContext, "Student Deleted", Toast.LENGTH_SHORT).show()
            }
            dialogInterface.dismiss()
        }

        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }
}