package com.example.chapter_flow.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chapter_flow.data.model.Student
import com.example.chapter_flow.data.model.StudentState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel : ViewModel() {
    private val _studentState = MutableStateFlow(StudentState(student = null, isLoading = false))
    val studentState: StateFlow<StudentState> = _studentState.asStateFlow()

    init {
        fetchStudentData()
    }

    fun fetchStudentData() {
        viewModelScope.launch {
            _studentState.value = StudentState(student = null, isLoading = true)
            delay(1000)  // delay for simulating loading sequence

            try {
                // 1st student
                _studentState.value = StudentState(student = Student("Foo", "Computer Science", 1),
                    isLoading = false)
                delay(1000)

                // set the same data as 1st student (distinctUntilChanged() for StateFlow does not trigger recomposition)
                _studentState.value = StudentState(student = Student("Foo", "Computer Science", 1),
                    isLoading = false)
                delay(1000)

                // 2nd student
                _studentState.value = StudentState(student = Student("Bar", "Data Science", 2),
                    isLoading = false)
                delay(1000)

                // notifies error status
                _studentState.value = StudentState(student = null, isLoading = false,
                    errorMessage = "Student data is not available anymore!")

            } catch (e: Exception) {
                _studentState.value = StudentState(student = null, isLoading = false,
                    errorMessage = e.message)
            }
        }
    }
}
