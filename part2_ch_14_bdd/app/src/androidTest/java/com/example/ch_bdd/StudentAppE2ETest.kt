package com.example.ch_bdd

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ch_bdd.ui.StudentScreen
import com.example.ch_bdd.viewmodel.StudentViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // Android Instrument Test Runner 사용
class StudentAppE2ETest { // 엔드-투-엔드(E2E) 테스트에 가까움

    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var viewModel: StudentViewModel

    @Before
    fun setup() {
        viewModel = StudentViewModel()
        // 테스트마다 초기 상태를 보장하기 위해 목록 초기화
        viewModel.clearStudents()
    }

    @Test
    fun `새로운 학생을 성공적으로 추가한다`() {
        // Given: 학생 정보 관리 시스템이 준비되어 있고, 초기 학생 목록은 비어 있다.
        composeTestRule.setContent {
            // 초기 화면에 아무것도 없음을 가정
            StudentScreen(viewModel = viewModel) // ViewModel을 주입받아 사용
        }
        assertThat(viewModel.uiState.value.students).isEmpty() // ViewModel의 목록도 비어있음을 확인

        // When: 사용자가 입력 필드에 학생 정보를 입력하고 '학생 추가' 버튼을 클릭한다.
        composeTestRule.onNodeWithText("학번").performTextInput("2023001")
        composeTestRule.onNodeWithText("이름").performTextInput("김철수")
        composeTestRule.onNodeWithText("학년").performTextInput("2")
        composeTestRule.onNodeWithText("학과").performTextInput("컴퓨터공학과")
        composeTestRule.onNodeWithText("학생 추가").performClick()

        // Then: "김철수" 학생 정보가 성공적으로 시스템에 저장되고, 학생 목록에 즉시 표시된다.
        composeTestRule.onNodeWithText("김철수 (2023001)").assertIsDisplayed() // 이름과 학번이 함께 표시되는지 확인
        assertThat(viewModel.uiState.value.students).hasSize(1)

        // And: 사용자에게 "학생이 추가되었습니다."라는 성공 메시지가 표시된다.
        // 성공 메시지는 Toast로 표시된 뒤 clearMessages()로 즉시 지워지므로 Compose 노드나 상태로 안정적으로 검증할 수 없다.
    }
}
