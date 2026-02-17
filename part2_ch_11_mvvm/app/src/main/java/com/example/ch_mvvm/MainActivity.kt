package com.example.ch_mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ch_mvvm.data.StudentRepository
import com.example.ch_mvvm.data.StudentRepositoryImpl
import com.example.ch_mvvm.data.StudentRepositoryInterface
import com.example.ch_mvvm.domain.Student
import com.example.ch_mvvm.ui.theme.StudentViewModel
import com.example.ch_mvvm.ui.theme.StudentViewModelFactory


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val repository = StudentRepository()
            StudentScreen(repository = repository)
        }
    }
}

@Composable
fun StudentScreen(repository: StudentRepository) {
    val viewModel: StudentViewModel = viewModel(factory = StudentViewModelFactory(repository = repository))
    val name by viewModel.name.collectAsState()
    val major by viewModel.major.collectAsState()
    val grade by viewModel.grade.collectAsState()
    val studentInfo by viewModel.studentInfo.collectAsState()

    Scaffold { paddingValues ->  // receives paddingValues
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues) // paddingValues 적용
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = name,
                onValueChange = viewModel::updateName,
                label = { Text("name") }
            )
            TextField(
                value = major,
                onValueChange = viewModel::updateMajor,
                label = { Text("major") }
            )
            TextField(
                value = grade,
                onValueChange = viewModel::updateGrade,
                label = { Text("grade") }
            )
            Button(onClick = viewModel::displayStudentInfo) {
                Text("student info")
            }
            Spacer(modifier = Modifier.height(16.dp))
            studentInfo?.let { student ->
                Text("name: ${student.name}")
                Text("major: ${student.major}")
                Text("grade: ${student.grade}")
            }
        }
    }
}

//@Preview(showBackground = true)
//@Composable
//fun StudentPreview() {
//    val repository = StudentRepository()
//    StudentScreen(repository = repository)
//}

//---------------------------------------------------------------
// EX 2 - depends on abstraction
//---------------------------------------------------------------


interface RepositoryFactory {
    fun createStudentRepository(): StudentRepositoryInterface
}

class StudentRepositoryImplFactory : RepositoryFactory {
    override fun createStudentRepository(): StudentRepositoryInterface {
        return StudentRepositoryImpl()
    }
}

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val useMockRepository = false

            val repositoryFactory: RepositoryFactory = if (useMockRepository) {
                object : RepositoryFactory {
                    override fun createStudentRepository(): StudentRepositoryInterface {
                        return object : StudentRepositoryInterface {
                            override fun getStudent(name: String, major: String, grade: String): Student? {
                                return Student("Mock Name", "Mock Major", "Mock Grade")
                            }
                        }
                    }
                }
            } else {
                StudentRepositoryImplFactory() // 실제 Repository를 생성하는 Factory
            }

            val repository = repositoryFactory.createStudentRepository()
            StudentScreenWithInterface(repository = repository)
        }
    }
}
