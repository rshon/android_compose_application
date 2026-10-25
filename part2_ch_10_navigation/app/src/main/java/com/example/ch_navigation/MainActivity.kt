package com.example.ch_navigation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch_navigation.ui.theme.ChapterNavigationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChapterNavigationTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    StudentInfoApp(
                        innerPadding
                    )
                }
            }
        }
    }
}

data class Student(
    val id: String,
    val name: String,
    val major: String,
    val grade: Int,
    val gpa: Double
)

val students = listOf(
    Student(id = "001", name = "Foo", major = "Computer Science", grade = 3, gpa = 3.8),
    Student(id = "002", name = "Bar", major = "AI", grade = 1, gpa = 3.5),
    Student(id = "003", name = "Boo", major = "Electronics", grade = 4, gpa = 3.75)
)

@Composable
fun StudentInfoApp(innerPadding: PaddingValues) {
    val navController = rememberNavController()
    MaterialTheme {
        NavHost(
            navController = navController,
            startDestination = "main"
        ) {
            composable("main") {
                MainScreen(
                    innerPadding,
                    onEnterClick = {
                        navController.navigate("student_list") {
                            popUpTo("main") { inclusive = true }
                        }
                    }
                )
            }

            composable("student_list") {
                StudentListScreen(
                    innerPadding,
                    students = students,
                    onStudentClick = { studentId ->
                        navController.navigate("student_detail/$studentId")
                    }
                )
            }

            composable(
                route = "student_detail/{studentId}",
                arguments = listOf(navArgument("studentId") { type = NavType.StringType })
            ) { backStackEntry ->
                val studentId = backStackEntry.arguments?.getString("studentId") ?: ""
                val student = students.find { it.id == studentId }
                if (student != null) {
                    StudentDetailScreen(
                        innerPadding,
                        student = student,
                        onBackClick = { navController.popBackStack() }
                    )
                } else {
                    Text("Student not found", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

@Composable
fun MainScreen(paddingValues: PaddingValues, onEnterClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "학생 정보 조회 시스템",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onEnterClick) {
            Text("Enter")
        }
    }
}

@Composable
fun StudentListScreen(paddingValues: PaddingValues, students: List<Student>, onStudentClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        Text(
            text = "학생 목록",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn {
            items(students) { student ->
                StudentListItem(
                    student = student,
                    onClick = { onStudentClick(student.id) }
                )
            }
        }
    }
}

@Composable
fun StudentListItem(student: Student, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ID: ${student.id}, Name: ${student.name}",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
fun StudentDetailScreen(paddingValues: PaddingValues, student: Student, onBackClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "학생 정보",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "ID: ${student.id}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Name: ${student.name}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Major: ${student.major}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Grade: ${student.grade}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "GPA: ${student.gpa}/4.0", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onBackClick) {
            Text("Back to List")
        }
    }
}
