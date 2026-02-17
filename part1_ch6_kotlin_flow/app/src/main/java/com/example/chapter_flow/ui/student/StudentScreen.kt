package com.example.chapter_flow.ui.student

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue // by를 사용하기 위함
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chapter_flow.viewmodel.StudentViewModel


@Composable
fun StudentScreen(viewModel: StudentViewModel = viewModel()) {
    val studentState by viewModel.studentState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when {
            studentState.isLoading -> {
                CircularProgressIndicator()
                Text("Loading student data...")
            }

            studentState.student != null -> {
                val student = studentState.student
                if (student != null) {
                    Text("Student Name: ${student.name}")
                    Text("Major: ${student.major}")
                    Text("Grade: ${student.grade}")
                }
            }
            studentState.errorMessage != null -> {
                Text("Error: ${studentState.errorMessage}")
                Button(onClick = { viewModel.fetchStudentData() }) {
                    Text("Retry")
                }
            }
            else -> {
                Text("No student data available. Press retry to load.")
                Button(onClick = { viewModel.fetchStudentData() }) {
                    Text("Load Student Data")
                }
            }
        }
    }
}