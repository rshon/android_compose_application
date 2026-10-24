package com.example.ch_android_architecture_student_ex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ch_android_architecture_student_ex.data.model.Student

@Database(entities = [Student::class], version = 1)
abstract class StudentDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}
