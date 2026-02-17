package com.example.ch_room_student_ex.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ch_room_student_ex.data.local.dao.StudentDao
import com.example.ch_room_student_ex.data.local.entity.StudentEntity

@Database(entities = [StudentEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studentDao(): StudentDao
}