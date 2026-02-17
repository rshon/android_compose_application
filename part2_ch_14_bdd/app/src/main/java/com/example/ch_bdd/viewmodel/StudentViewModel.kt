package com.example.ch_bdd.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch_bdd.domain.model.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class StudentUiState(
    val studentId: String = "",
    val name: String = "",
    val grade: String = "",
    val major: String = "",
    val students: List<Student> = emptyList(), // 학생 목록 관리
    val successMessage: String? = null,
    val errorMessage: String? = null
)

class StudentViewModel : ViewModel() { // HiltViewModel 적용 시 @Inject constructor(...) 필요
    private val _uiState = MutableStateFlow(StudentUiState())
    val uiState: StateFlow<StudentUiState> = _uiState.asStateFlow()

    init {
        // 초기 학생 목록 로드 (실제 앱에서는 Repository에서 로드)
        _uiState.update { it.copy(students = mutableStateListOf()) }
    }

    fun onStudentIdChange(value: String) { _uiState.update { it.copy(studentId = value, errorMessage = null) } }
    fun onNameChange(value: String) { _uiState.update { it.copy(name = value, errorMessage = null) } }
    fun onGradeChange(value: String) { _uiState.update { it.copy(grade = value, errorMessage = null) } }
    fun onMajorChange(value: String) { _uiState.update { it.copy(major = value, errorMessage = null) } }

    fun addStudent() {
        viewModelScope.launch {
            if (_uiState.value.studentId.isBlank()) { // 시나리오 3을 위한 유효성 검사
                _uiState.update { it.copy(errorMessage = "학번을 입력해주세요.") }
                return@launch
            }
            try {
                val newStudent = Student(
                    name = _uiState.value.name,
                    year = _uiState.value.grade.toIntOrNull() ?: 0, // 유효성 검사 필요
                    department = _uiState.value.major,
                    id = _uiState.value.studentId
                )
                val currentStudents = _uiState.value.students.toMutableList()
                currentStudents.add(newStudent)
                _uiState.update {
                    it.copy(
                        students = currentStudents,
                        successMessage = "학생이 추가되었습니다.",
                        studentId = "", name = "", grade = "", major = "" // 입력 필드 초기화
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(errorMessage = "학생 추가 중 오류 발생: ${e.message}") }
            }
        }
    }

    fun clearMessages() {
        _uiState.update { it.copy(successMessage = null, errorMessage = null) }
    }

    fun deleteStudent(student: Student) {
        viewModelScope.launch {
            val currentStudents = _uiState.value.students.toMutableList()
            if (currentStudents.remove(student)) {
                _uiState.update { it.copy(students = currentStudents, successMessage = "학생이 삭제되었습니다.") }
            } else {
                _uiState.update { it.copy(errorMessage = "학생 삭제 중 오류 발생: 학생을 찾을 수 없습니다.") }
            }
        }
    }

    fun clearStudents() {
        _uiState.update { it.copy(students = emptyList()) }
    }
}
