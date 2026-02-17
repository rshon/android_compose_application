package com.example.ch_test.presentation.ui.student

import com.example.ch_test.domain.model.Student

sealed interface StudentUiState {
    data object Loading : StudentUiState
    data class Success(val student: Student) : StudentUiState
    data class Error(val message: String) : StudentUiState
}