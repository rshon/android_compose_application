package com.example.ch_test

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.ch_test.domain.repository.StudentRepository
import com.example.ch_test.presentation.ui.student.StudentViewModel
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Test
import org.junit.Before
import org.junit.After
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import io.mockk.mockk
import io.mockk.coEvery
import io.mockk.coVerify
import kotlinx.coroutines.test.runTest
import com.example.ch_test.domain.model.Student
import com.example.ch_test.presentation.ui.student.StudentUiState
import org.junit.Assert.assertEquals

@ExperimentalCoroutinesApi
class StudentViewModelTest {

    // Main Dispatcher를 테스트용으로 교체하기 위한 규칙
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: StudentViewModel
    private lateinit var repository: StudentRepository  // Mock으로 대체될 의존성
    private lateinit var testDispatcher: TestDispatcher

    @Before
    fun setUp() {
        testDispatcher = UnconfinedTestDispatcher()
        Dispatchers.setMain(testDispatcher)  // Main Dispatcher를 테스트용으로 설정

        // 1. Repository 의존성을 Mock 객체로 생성
        repository = mockk()

        // 2. ViewModel에 Mock 객체를 주입 (아직 StudentViewModel은 없음)
        viewModel = StudentViewModel(repository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain() // 테스트 후 Main Dispatcher 원상 복구
    }

    @Test
    fun `getStudent - Repository가 학생 데이터를 성공적으로 반환하면, UI 상태는 Success가 된다`(): Unit = runTest {
        // --- 1. Arrange (준비): Mock 객체의 행동 정의 (coEvery 사용) ---
        val studentId = "20250001"
        val dummyStudent = Student("홍길동", studentId, 3, "컴퓨터공학과")

        // repository.getStudent(studentId)가 호출되면, 성공(Result.success)을 반환하도록 설정
        coEvery { repository.getStudent(studentId) } returns Result.success(dummyStudent)

        // --- 2. Act (실행): 테스트 대상 메소드 호출 ---
        viewModel.getStudent(studentId)

        // --- 3. Assert (단언): 결과 검증 ---
        // viewModel의 uiState가 예상대로 변경되었는지 확인
        val actualState = viewModel.uiState.first() // StateFlow의 첫 번째 값을 가져옴
        val expectedState = StudentUiState.Success(dummyStudent)
        assertEquals(expectedState, actualState)

        // --- 4. Verify (검증): 상호작용 검증 (coVerify 사용) ---
        // repository.getStudent가 정확히 한 번 호출되었는지 확인
        coVerify(exactly = 1) { repository.getStudent(studentId) }
    }

    @Test
    fun `getStudent - Repository가 실패를 반환하면, UI 상태는 Error가 된다`() = runTest {
        // --- 1. Arrange ---
        val studentId = "99999999"
        val errorMessage = "학생을 찾을 수 없습니다."

        // repository.getStudent(studentId)가 호출되면, 실패(Result.failure)를 반환하도록 설정
        coEvery { repository.getStudent(studentId) } returns Result.failure(Exception(errorMessage))

        // --- 2. Act ---
        viewModel.getStudent(studentId)

        // --- 3. Assert ---
        val actualState = viewModel.uiState.first()
        val expectedState = StudentUiState.Error(errorMessage)
        assertEquals(expectedState, actualState)
    }
}
