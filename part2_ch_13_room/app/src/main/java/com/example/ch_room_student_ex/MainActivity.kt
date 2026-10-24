package com.example.ch_room_student_ex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ch_room_student_ex.presentation.MainScreen
import com.example.ch_room_student_ex.presentation.add.AddStudentScreen
import com.example.ch_room_student_ex.presentation.list.StudentListScreen
import com.example.ch_room_student_ex.ui.theme.Ch_room_student_exTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Ch_room_student_exTheme {
                Surface {
                    val navController = rememberNavController()
                    NavHost(navController = navController, startDestination = "main") {
                        composable("main") {
                            MainScreen(navController = navController)
                        }
                        composable("student_list") {
                            StudentListScreen(navController = navController)
                        }
                        composable("add_student") {
                            AddStudentScreen(navController = navController)
                        }
                    }
                }
            }
        }
    }
}
