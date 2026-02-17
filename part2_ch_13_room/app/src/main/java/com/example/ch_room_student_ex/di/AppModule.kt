package com.example.ch_room_student_ex.di

import android.content.Context
import androidx.room.Room
import com.example.ch_room_student_ex.data.local.dao.StudentDao
import com.example.ch_room_student_ex.data.local.database.AppDatabase
import com.example.ch_room_student_ex.data.repository.StudentRepositoryImpl
import com.example.ch_room_student_ex.domain.repository.StudentRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "student_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideStudentDao(database: AppDatabase): StudentDao {
        return database.studentDao()
    }

    @Provides
    @Singleton
    fun provideStudentRepository(studentDao: StudentDao): StudentRepository {
        return StudentRepositoryImpl(studentDao)
    }
}


