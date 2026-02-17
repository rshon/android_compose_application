package com.example.ch_android_architecture_student_ex.di

import com.example.ch_android_architecture_student_ex.data.local.StudentDao
import com.example.ch_android_architecture_student_ex.data.repository.StudentRepository
import com.example.ch_android_architecture_student_ex.data.repository.StudentRepositoryImpl
import com.example.ch_android_architecture_student_ex.domain.GetStudentsUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object StudentModule {

    @Provides
    fun provideStudentRepository(dao: StudentDao): StudentRepository =
        StudentRepositoryImpl(dao)

    @Provides
    fun provideGetStudentsUseCase(repo: StudentRepository): GetStudentsUseCase =
        GetStudentsUseCase(repo)
}
