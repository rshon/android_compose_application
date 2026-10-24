package com.example.chapter_flow

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

data class StudentState(
    val student: Student?,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

fun getStudentState(): Flow<StudentState> = flow {
    emit(StudentState(student = null, isLoading = true))
    delay(200)
    emit(StudentState(student = Student("Foo", "Computer Science", 1), isLoading = false))
    delay(200)
    emit(StudentState(student = Student("Foo", "Computer Science", 1), isLoading = false))
    delay(200)
    emit(StudentState(student = Student("Foo", "Computer Science", 2), isLoading = false))
    delay(200)
    emit(StudentState(student = null, isLoading = false, errorMessage = "Student data is not available anymore!"))
}

fun test_state_flow() = runBlocking {
    getStudentState()
        .distinctUntilChanged()
        .collect { state ->
            when {
                state.isLoading -> println("Loading student data...")
                state.student != null -> println("Student: ${state.student.name}, Major: ${state.student.major}, Grade: ${state.student.grade}")
                state.errorMessage != null -> println("Error: ${state.errorMessage}")
            }
        }
}

sealed class StudentEvent {
    data class UpdateGrade(val student: Student) : StudentEvent()
    data class ShowMessage(val message: String) : StudentEvent()
    object DeleteStudent : StudentEvent()
}

fun getStudentEvents(): Flow<StudentEvent> = flow {
    delay(300)
    emit(StudentEvent.UpdateGrade(Student("Bar", "Mathematics", 3)))
    delay(300)
    emit(StudentEvent.ShowMessage("Student updated successfully"))
    delay(300)
    emit(StudentEvent.DeleteStudent)
}

fun test_event_flow() = runBlocking {
    getStudentEvents()
        .collect { event ->
            when (event) {
                is StudentEvent.UpdateGrade ->
                    println("Grade updated: ${event.student.name}, Grade: ${event.student.grade}")
                is StudentEvent.ShowMessage -> println("Message: ${event.message}")
                is StudentEvent.DeleteStudent -> println("Student deleted")
            }
        }
}

fun main() {
//    test_state_flow()
    test_event_flow()
}
