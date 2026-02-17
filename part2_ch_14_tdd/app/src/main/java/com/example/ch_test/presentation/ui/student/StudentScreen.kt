package com.example.ch_test.presentation.ui.student

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Alignment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.graphics.Color
import com.example.ch_test.domain.model.Student

@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (val state = uiState) {
            is StudentUiState.Loading -> {
                CircularProgressIndicator()
            }
            is StudentUiState.Success -> {
                StudentInfoCard(student = state.student)
            }
            is StudentUiState.Error -> {
                Text(text = "에러 발생: ${state.message}", color = Color.Red)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = { viewModel.getStudent("20250001") }) {
            Text("학생 정보 불러오기")
        }
    }
}

@Composable
fun Column(modifier: Any, verticalArrangement: Any, horizontalAlignment: Any, content: @Composable () -> Unit) {

}

@Composable
fun StudentInfoCard(student: Student) {
    Card(modifier = Modifier.padding(16.dp)) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = TODO(),
            horizontalAlignment = TODO()
        ) {
            Text(text = "이름: ${student.name}", style = MaterialTheme.typography.titleMedium)
            Text(text = "학번: ${student.studentId}")
            Text(text = "학년: ${student.grade}학년")
            Text(text = "전공: ${student.major}")
        }
    }
}