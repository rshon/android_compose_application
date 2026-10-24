class GraduateStudent(
    private var studentId: String,
    private var name: String,
    private var year: Int,
    private var major: String,
    private var researchTopic: String
) {
    // 학사 관리 관련 메서드
    fun calculateGPA(): Double {
        // 복잡한 GPA 계산 로직
        return 4.0 // 예시로 4.0 반환
    }

    // 수강신청 관련 메서드
    fun registerForCourses() {
        // 수강신청 로직
        // - 학과 서버와 통신
        // - 선수과목 확인
        // - 수강인원 확인
    }

    // 데이터베이스 관련 메서드
    fun saveToDatabase() {
        // DB 연결 및 저장 로직
        // - 연결 설정
        // - SQL 쿼리 실행
        // - 에러 처리
    }

    // 성적표 관련 메서드
    fun generateTranscript(): String {
        // 성적표 생성 로직
        // - 이수 과목 로드
        // - 포맷팅
        // - PDF 생성
        return ""
    }

    // 장학금 관련 메서드 (변경된 로직)
    fun applyForScholarship(): Boolean {
        // 1. 자격 확인: GPA 3.5 이상, 재학 연수 2년 이상
        val gpa = calculateGPA()
        val hasEnoughYears = year >= 2
        val isEligible = gpa >= 3.5 && hasEnoughYears

        // 2. 자격 충족 여부에 따라 신청 처리
        return if (isEligible) {
            // 신청서 제출 (가정: 외부 시스템 호출)
            println("Submitting scholarship application for $name (Student ID: $studentId)")
            println("Eligibility: GPA = $gpa, Years = $year")

            // 3. 신청 결과 처리 (가정: 90% 확률로 승인)
            val isApproved = (0..100).random() <= 90
            if (isApproved) {
                println("Scholarship application approved for $name!")
            } else {
                println("Scholarship application rejected for $name.")
            }
            isApproved
        } else {
            // 자격 미충족 시
            println("Scholarship application failed for $name. Not eligible.")
            println("Eligibility: GPA = $gpa (required: 3.5), Years = $year (required: 2)")
            false
        }
    }
}
