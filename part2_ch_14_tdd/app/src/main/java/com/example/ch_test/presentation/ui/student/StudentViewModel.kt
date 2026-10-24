package com.example.ch_test.presentation.ui.student

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch_test.domain.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class StudentViewModel(
    private val repository: StudentRepository // 생성자를 통해 의존성 주입
) : ViewModel() {

    private val _uiState = MutableStateFlow<StudentUiState>(StudentUiState.Loading)
    val uiState = _uiState.asStateFlow()

    fun getStudent(studentId: String) {
        // UI 상태를 Loading으로 먼저 변경
        _uiState.value = StudentUiState.Loading

        viewModelScope.launch {
            // Repository에 데이터 요청
            val result = repository.getStudent(studentId)

            // 결과에 따라 UI 상태 업데이트
            result.onSuccess { student ->
                _uiState.value = StudentUiState.Success(student)
            }.onFailure { exception ->
                _uiState.value = StudentUiState.Error(exception.message ?: "알 수 없는 에러 발생")
            }
        }
    }
}
