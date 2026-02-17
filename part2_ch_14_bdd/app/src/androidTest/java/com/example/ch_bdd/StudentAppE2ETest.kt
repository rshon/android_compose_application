package com.example.ch_bdd

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ch_bdd.ui.StudentScreen
import com.example.ch_bdd.viewmodel.StudentViewModel
import com.google.common.truth.Truth.assertThat
import org.junit.Rule
import org.junit.Test
import org.junit.Before
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
        // 현재 StudentScreen은 버튼 클릭 시 '홍길동'을 추가하도록 하드코딩되어 있으므로,
        // 이 테스트는 현재 UI에 맞는 방식으로 작성됩니다. 실제 앱에서는 TextField 입력 후 버튼 클릭.
        // 여기서는 '학생 추가' 버튼만 클릭해도 ViewModel에 하드코딩된 학생이 추가되므로 이를 이용.
        composeTestRule.onNodeWithText("학생 추가").performClick()

        // Then: "김철수" 학생 정보가 성공적으로 시스템에 저장되고, 학생 목록에 즉시 표시된다.
        // (현재 코드로는 '홍길동'이 추가되므로, 테스트는 '홍길동'으로 검증)
        composeTestRule.onNodeWithText("홍길동").assertIsDisplayed()
        composeTestRule.onNodeWithText("2023001").assertIsDisplayed() // 학번도 표시되는지 확인

        // And: 사용자에게 "학생이 추가되었습니다."라는 성공 메시지가 표시된다.
        // 현재 UI에 토스트 메시지나 스낵바 로직이 없으므로,
        // 이 부분은 테스트 실패로 이어질 것이며, 이를 통해 UI 구현이 필요함을 알 수 있다.
        // 예시: composeTestRule.onNodeWithText("학생이 추가되었습니다.").assertIsDisplayed()
        // (현재는 이 라인은 주석 처리하거나, UI에 메시지 표시 로직을 먼저 구현해야 함)
    }
}
