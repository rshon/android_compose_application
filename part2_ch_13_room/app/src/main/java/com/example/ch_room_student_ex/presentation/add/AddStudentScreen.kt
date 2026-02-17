package com.example.ch_room_student_ex.presentation.add

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddStudentScreen(
    navController: NavController,
    viewModel: AddStudentViewModel = hiltViewModel()
) {
    var name by remember { mutableStateOf("") }
    var studentId by remember { mutableStateOf("") }
    var major by remember { mutableStateOf("") }
    var grade by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Student") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigate("main") }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = studentId,
                onValueChange = { studentId = it },
                label = { Text("Student ID") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = major,
                onValueChange = { major = it },
                label = { Text("Major") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = grade,
                onValueChange = { grade = it },
                label = { Text("Grade") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    val gradeInt = grade.toIntOrNull() ?: 1
                    viewModel.addStudent(name, studentId, major, gradeInt)
                    navController.navigate("main")
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = name.isNotBlank() && studentId.isNotBlank() && major.isNotBlank() && grade.isNotBlank()
            ) {
                Text("Add Student")
            }
        }
    }
}