package com.example.ch_room_student_ex.presentation.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch_room_student_ex.domain.model.Student
import com.example.ch_room_student_ex.domain.usecase.DeleteStudentUseCase
import com.example.ch_room_student_ex.domain.usecase.GetStudentsUseCase
import com.example.ch_room_student_ex.domain.usecase.SearchStudentsUseCase
import com.example.ch_room_student_ex.domain.usecase.UpdateStudentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StudentListViewModel @Inject constructor(
    private val getStudentsUseCase: GetStudentsUseCase,
    private val searchStudentsUseCase: SearchStudentsUseCase,
    private val deleteStudentUseCase: DeleteStudentUseCase,
    private val updateStudentUseCase: UpdateStudentUseCase
) : ViewModel() {
    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> = _students.asStateFlow()

    private var collectJob: Job? = null

    init {
        collectJob = viewModelScope.launch {
            getStudentsUseCase().collect { students ->
                _students.value = students
            }
        }
    }

    fun searchStudents(query: String) {
        collectJob?.cancel()
        collectJob = viewModelScope.launch {
            if (query.isBlank()) {
                getStudentsUseCase().collect { students ->
                    _students.value = students
                }
            } else {
                searchStudentsUseCase(query).collect { students ->
                    _students.value = students
                }
            }
        }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            deleteStudentUseCase(student)
        }
    }

    fun updateStudent(student: Student) {
        viewModelScope.launch {
            updateStudentUseCase(student)
        }
    }
}
