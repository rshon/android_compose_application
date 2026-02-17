package com.example.ch_room_student_ex.presentation.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch_room_student_ex.domain.model.Student
import com.example.ch_room_student_ex.domain.usecase.AddStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddStudentViewModel @Inject constructor(
    private val addStudentUseCase: AddStudentUseCase
) : ViewModel() {
    fun addStudent(name: String, studentId: String, major: String, grade: Int) {
        viewModelScope.launch {
            addStudentUseCase(Student(name = name, studentId = studentId, major = major, grade = grade))
        }
    }
}