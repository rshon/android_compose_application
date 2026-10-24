package com.example.ch_android_architecture_student_ex.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.ch_android_architecture_student_ex.data.local.StudentDao
import com.example.ch_android_architecture_student_ex.data.local.StudentDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideStudentDatabase(
        @ApplicationContext app: Context
    ): StudentDatabase {
        lateinit var dbInstance: StudentDatabase

        val callback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                CoroutineScope(Dispatchers.IO).launch {
                    dbInstance.clearAllTables()
                }
            }
        }

        dbInstance = Room.databaseBuilder(
            app,
            StudentDatabase::class.java,
            "student-db"
        )
            .fallbackToDestructiveMigration()
            .addCallback(callback)
            .build()

        return dbInstance
    }

    @Provides
    fun provideStudentDao(db: StudentDatabase): StudentDao =
        db.studentDao()
}
