/** 학적 상태가 ‘닫힌(closed)’ 학생 계층 */
sealed class Student(
    open val name: String,
    open val major: String,
    open val grade: Double
) {
    data class Undergraduate(
        override val name: String,
        override val major: String,
        override val grade: Double,
        val year: Int                        // 1~4학년
    ) : Student(name, major, grade)

    data class Graduate(
        override val name: String,
        override val major: String,
        override val grade: Double,
        val thesisTitle: String
    ) : Student(name, major, grade)

    data object OnLeave : Student(
        name = "미등록",
        major = "미등록",
        grade = 0.0
    )
}

fun describe(student: Student): String = when (student) {
    is Student.Undergraduate ->
        "${student.name} — grade ${student.year} (${student.major}, average ${student.grade})"
    is Student.Graduate ->
        "${student.name} — graduate (${student.major}), thesis: ${student.thesisTitle}"
    Student.OnLeave -> "on leave(N/A)"
}

sealed class StudentEvent {
    data class Enroll(val student: Student) : StudentEvent()
    data class Graduate(val studentId: Int) : StudentEvent()
    object Dropout : StudentEvent()
}

fun handleEvent(event: StudentEvent) {
    when (event) {
        is StudentEvent.Enroll -> println("${event.student.name} 학생 등록")
        is StudentEvent.Graduate -> println("ID ${event.studentId} 학생 졸업 처리")
        StudentEvent.Dropout -> println("학생 중도 탈락 처리")
    }
}

fun testStudentEvent() {
    val alice = Student.Undergraduate("Foo", "Computer Science", 3.8, year = 2)
    val bob = Student.Graduate("Bar", "AI", 4.3, thesisTitle = "LLM optimization")

    // 다양한 이벤트 시나리오를 리스트로 준비
    val events: List<StudentEvent> = listOf(
        StudentEvent.Enroll(alice),
        StudentEvent.Enroll(bob),
        StudentEvent.Graduate(1),
        StudentEvent.Dropout,
        StudentEvent.Graduate(2)
    )

    // 하나씩 꺼내서 handleEvent 로 처리
    events.forEach { event ->
        handleEvent(event)
    }
}

fun testStudentType() {
    val roster = listOf(
        Student.Undergraduate("Foo", "Computer Science", 3.8, year = 2),
        Student.Graduate("Bar", "AI", 4.3, thesisTitle = "LLM optimization"),
        Student.OnLeave
    )

    // 전체 정보 출력
    roster.forEach { println(describe(it)) }

    // 학점(GPA) 필터 예: 4.0 이상 학생만 추출
    val honors = roster.filter { it.grade >= 4.0 }
    println("\n📋 4.0 평점 이상 학생:")
    honors.forEach { println("• ${it.name} (${it.grade})") }
}

fun main() {
    // testStudentType()

    testStudentEvent()
}
