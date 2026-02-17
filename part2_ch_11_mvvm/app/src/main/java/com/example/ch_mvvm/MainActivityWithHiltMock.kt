package com.example.ch_mvvm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.ch_mvvm.data.StudentRepositoryInterface
import dagger.hilt.android.AndroidEntryPoint
import com.example.ch_mvvm.data.MockRepository
import javax.inject.Inject

@AndroidEntryPoint
class MainActivityWithHiltMock : ComponentActivity() {
    @Inject
    @MockRepository // MockRepository Qualifier를 사용하여 Mock 객체 주입
    lateinit var repository: StudentRepositoryInterface

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            StudentScreenWithInterface(repository = repository)
        }
    }
}





