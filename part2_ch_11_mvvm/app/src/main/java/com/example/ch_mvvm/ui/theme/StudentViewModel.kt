package com.example.ch_mvvm.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch_mvvm.data.StudentRepository
import com.example.ch_mvvm.domain.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel: UI 상태 관리 및 Repository와 상호작용
class StudentViewModel(private val repository: StudentRepository) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _major = MutableStateFlow("")
    val major: StateFlow<String> = _major

    private val _grade = MutableStateFlow("")
    val grade: StateFlow<String> = _grade

    private val _studentInfo = MutableStateFlow<Student?>(null)
    val studentInfo: StateFlow<Student?> = _studentInfo

    fun updateName(input: String) {
        _name.value = input
    }

    fun updateMajor(input: String) {
        _major.value = input
    }

    fun updateGrade(input: String) {
        _grade.value = input
    }

    fun displayStudentInfo() {
        viewModelScope.launch {
            val student = repository.getStudent(_name.value, _major.value, _grade.value)
            _studentInfo.value = student
        }
    }
}