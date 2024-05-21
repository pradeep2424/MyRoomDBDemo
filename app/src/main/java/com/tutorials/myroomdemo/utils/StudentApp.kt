package com.tutorials.myroomdemo.utils

import android.app.Application
import com.tutorials.myroomdemo.data.StudentDatabase

class StudentApp : Application() {
    val db by lazy {
        StudentDatabase.getInstance(this)
    }
}