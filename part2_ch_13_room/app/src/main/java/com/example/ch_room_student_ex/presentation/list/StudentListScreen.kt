package com.example.ch_room_student_ex.presentation.list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.ch_room_student_ex.domain.model.Student

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudentListScreen(
    navController: NavController,
    viewModel: StudentListViewModel = hiltViewModel()
) {
    val students by viewModel.students.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var showDialog by remember { mutableStateOf(false) }
    var selectedStudent by remember { mutableStateOf<Student?>(null) }
    var updatedName by remember { mutableStateOf("") }
    var updatedStudentId by remember { mutableStateOf("") }
    var updatedMajor by remember { mutableStateOf("") }
    var updatedGrade by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Student List") },
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
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    viewModel.searchStudents(it)
                },
                label = { Text("Search by name or student ID") },
                modifier = Modifier.fillMaxWidth()
            )
            LazyColumn {
                items(students) { student ->
                    StudentItem(
                        student = student,
                        onDelete = { viewModel.deleteStudent(student) },
                        onEdit = {
                            selectedStudent = student
                            updatedName = student.name
                            updatedStudentId = student.studentId
                            updatedMajor = student.major
                            updatedGrade = student.grade.toString()
                            showDialog = true
                        }
                    )
                }
            }
        }
    }

    if (showDialog && selectedStudent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Edit Student") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = updatedName,
                        onValueChange = { updatedName = it },
                        label = { Text("Name") }
                    )
                    OutlinedTextField(
                        value = updatedStudentId,
                        onValueChange = { updatedStudentId = it },
                        label = { Text("Student ID") }
                    )
                    OutlinedTextField(
                        value = updatedMajor,
                        onValueChange = { updatedMajor = it },
                        label = { Text("Major") }
                    )
                    OutlinedTextField(
                        value = updatedGrade,
                        onValueChange = { updatedGrade = it },
                        label = { Text("Grade") }
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val gradeInt = updatedGrade.toIntOrNull() ?: selectedStudent!!.grade
                        viewModel.updateStudent(
                            selectedStudent!!.copy(
                                name = updatedName,
                                studentId = updatedStudentId,
                                major = updatedMajor,
                                grade = gradeInt
                            )
                        )
                        showDialog = false
                    },
                    enabled = updatedName.isNotBlank() && updatedStudentId.isNotBlank() && updatedMajor.isNotBlank()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun StudentItem(
    student: Student,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = student.name, style = MaterialTheme.typography.titleMedium)
                Text(text = "ID: ${student.studentId}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Major: ${student.major}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Grade: ${student.grade}", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Student"
                )
            }
        }
    }
}
