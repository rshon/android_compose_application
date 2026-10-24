package com.example.ch_bdd.ui

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ch_bdd.viewmodel.StudentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentScreen(viewModel: StudentViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle() // ViewModel 상태 관찰
    val context = LocalContext.current

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessages()
        }
    }
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearMessages()
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        OutlinedTextField(value = uiState.studentId, onValueChange = viewModel::onStudentIdChange, label = { Text("학번") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.name, onValueChange = viewModel::onNameChange, label = { Text("이름") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.grade, onValueChange = viewModel::onGradeChange, label = { Text("학년") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = uiState.major, onValueChange = viewModel::onMajorChange, label = { Text("학과") }, modifier = Modifier.fillMaxWidth())

        Spacer(Modifier.height(16.dp))

        Button(onClick = viewModel::addStudent, modifier = Modifier.fillMaxWidth(),
            enabled = uiState.studentId.isNotBlank() && uiState.name.isNotBlank() && uiState.grade.isNotBlank() && uiState.major.isNotBlank()) {
            Text("학생 추가")
        }
        Spacer(Modifier.height(8.dp))
        // 오류 메시지 표시
        if (uiState.errorMessage != null && uiState.errorMessage!!.isNotBlank()) {
            Text(uiState.errorMessage!!, color = MaterialTheme.colorScheme.error)
        }

        Spacer(Modifier.height(16.dp))
        Text("등록된 학생:")
        LazyColumn {
            items(uiState.students) { student -> // uiState.students를 관찰
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("${student.name} (${student.id})")
                    Button(onClick = { viewModel.deleteStudent(student) }) {
                        Text("삭제")
                    }
                }
            }
        }
    }
}
