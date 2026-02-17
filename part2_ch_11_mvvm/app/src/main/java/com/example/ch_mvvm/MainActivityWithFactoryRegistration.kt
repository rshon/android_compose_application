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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ch_mvvm.data.RepositoryKey
import com.example.ch_mvvm.data.RepositoryRegistry
import com.example.ch_mvvm.data.StudentRepository
import com.example.ch_mvvm.data.StudentRepositoryImpl
import com.example.ch_mvvm.data.StudentRepositoryInterface
import com.example.ch_mvvm.domain.Student
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


class MainActivityWithFactoryRegistration : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentRepositoryImplFactoryForAutoRegistration()

            val repository: StudentRepositoryInterface = RepositoryRegistry.create(RepositoryKey.STUDENT_IMPL)
            StudentScreenWithInterface(repository = repository)
        }
    }
}

class StudentRepositoryImplFactoryForAutoRegistration : RepositoryFactory {
    init {
        RepositoryRegistry.registerFactory<StudentRepositoryInterface>(RepositoryKey.STUDENT_IMPL) { StudentRepositoryImpl() }
    }
    override fun createStudentRepository(): StudentRepositoryInterface {
        return StudentRepositoryImpl()
    }
}

// ViewModel: UI 상태 관리 및 Repository와 상호작용
class StudentViewModelWithInterface(private val repository: StudentRepositoryInterface) : ViewModel() {
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name

    private val _major = MutableStateFlow("")
    val major: StateFlow<String> = _major

    private val _grade = MutableStateFlow("")
    val grade: StateFlow<String> = _grade

    private val _studentInfo = MutableStateFlow<Student?>(null)
    val studentInfo: StateFlow<Student?> = _studentInfo

    fun updateName(input: String) {
        _name.value = input
    }

    fun updateMajor(input: String) {
        _major.value = input
    }

    fun updateGrade(input: String) {
        _grade.value = input
    }

    fun displayStudentInfo() {
        viewModelScope.launch {
            val student = repository.getStudent(_name.value, _major.value, _grade.value)
            _studentInfo.value = student
        }
    }
}

class StudentViewModelFactoryWithInterface(private val repository: StudentRepositoryInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StudentViewModelWithInterface::class.java)) {
            return StudentViewModelWithInterface(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

@Composable
fun StudentScreenWithInterface(repository: StudentRepositoryInterface) {
    val viewModel: StudentViewModelWithInterface = viewModel(factory = StudentViewModelFactoryWithInterface(repository = repository))
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

@Preview(showBackground = true)
@Composable
fun StudentPreview() {
    val repository = StudentRepository()
    StudentScreen(repository = repository)
}