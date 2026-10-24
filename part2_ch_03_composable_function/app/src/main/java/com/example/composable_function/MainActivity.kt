package com.example.composable_function

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column {
//                SlotAPIEx1()
//                SlotAPIEx2()
//                NonHoistingEx()
//                HoistingEx()
                StudentApp()
            }
        }
    }
}

data class Student(
    val id: Int,
    val name: String,
    val grade: Int
)

val LocalStudentList = compositionLocalOf { emptyList<Student>() }

@Composable
fun StudentApp() {
    val students = listOf(
        Student(1, "Foo", 2),
        Student(2, "Bar", 3),
        Student(3, "Boo", 1)
    )

    CompositionLocalProvider(
        LocalStudentList.provides(students)
    ) {
        StudentScreen()
    }
}

@Composable
fun StudentScreen() {
    val students = LocalStudentList.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Student list", style = MaterialTheme.typography.headlineSmall)
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(students, key = { it.id }) { student ->
                StudentRow(student)
            }
        }
    }
}

@Composable
fun StudentRow(student: Student) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "${student.id}. ${student.name}")
        Text(text = "Grade ${student.grade}")
    }
}

@Composable
fun HoistingEx() {
    var count by remember { mutableStateOf(0) }

    Column {
        CounterIncButton(
            count = count,
            onCountChange = { newCount -> count = newCount }
        )

        if (count >= 3) {
            Text("You've clicked 3 times!")
        }

        ResetButton(onReset = { count = 0 })
    }
}

@Composable
fun CounterIncButton(count: Int, onCountChange: (Int) -> Unit) {
    Column {
        Button(onClick = { onCountChange(count + 1) }) {
            Text("Clicked $count times!")
        }
    }
}

@Composable
fun ResetButton(onReset: () -> Unit) {
    Button(onClick = { onReset() }) {
        Text("Reset Count")
    }
}

@Composable
fun NonHoistingEx() {
    var showCounter by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 10.dp)
    ) {
        Button(onClick = { showCounter = !showCounter }) {
            Text("Toggle Counter")
        }

        if (showCounter) {
            val count = CounterButton()

            if (count >= 3) {
                Text("🎉 You've clicked 3 times!")
            }
        }
    }
}

@Composable
fun CounterButton(): Int {
    var count by remember { mutableStateOf(0) }

    Column {
        Button(onClick = { count++ }) {
            Text("Clicked $count times")
        }

//        if (count >= 3) {
//            Text("🎉 You've clicked 3 times!")
//        }
    }

    return count
}

@Composable
fun HelloApp() {
    Column {
        Text(text = "Hello, Jetpack Compose!")
        Button(onClick = {
            println("button is clicked")
        }) {
            Text(text = "Click Me")
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Composable
fun RememberClickCount() {
    var count by mutableIntStateOf(0)
    var rememberedCount by remember { mutableIntStateOf(0) }

    Column {
        Text(text = "Click Count (no remember): $count")
        Button(onClick = { count++ }) {
            Text(text = "Click Count + (no remember)")
        }
        Text(text = "Click Count (with remember): $rememberedCount")
        Button(onClick = { rememberedCount++ }) {
            Text(text = "Click Count + (with remember)")
        }
    }
}

@Composable
fun RememberSaveableClickCount() {
    var counter by rememberSaveable { mutableIntStateOf(0) }

    Column {
        Text(text = "Counter: $counter")
        Button(onClick = { counter++ }) {
            Text(text = "Counter +")
        }
    }
}

class LoginViewModel : ViewModel() {
    val userName = mutableStateOf<String?>(null)
}

@Composable
fun LoginScreen(viewModel: LoginViewModel = viewModel()) {
    val name = rememberSaveable { mutableStateOf("") }
    val result by viewModel.userName
    TextField(value = name.value, onValueChange = { name.value = it })
    result?.let { Text(it) }
}

@Composable
fun FunctionA() {
    var stateA by remember { mutableStateOf("Initial State") }

    val lambdaFunction: () -> Unit = {
        stateA = "Updated State"
        println("State in Function A: $stateA")
    }

    FunctionB(lambdaFunction)
}

@Composable
fun FunctionB(lambdaFunctionPrototype: () -> Unit) {
    Button(onClick = lambdaFunctionPrototype) { // 전달받은 람다 함수 호출
        Text("Call Lambda Function")
    }
}

@Composable
fun MyCustomButton(onClick: () -> Unit, content: @Composable () -> Unit) {
    Button(onClick = onClick) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
fun SlotAPIEx1() {
    MyCustomButton(onClick = {}) {
        Text(text = "This is text!")
    }
}

@Preview(showBackground = true)
@Composable
fun SlotAPIEx2() {
    MyCustomButton(onClick = {}) {
        Button(onClick = {}) {
            Text(text = "This is button")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RememberClickCount()
}
