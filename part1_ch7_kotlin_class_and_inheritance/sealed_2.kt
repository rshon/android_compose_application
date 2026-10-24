sealed class Student(
    open val id: Int,
    open val name: String,
    open val major: String,
    open val grade: Double
) {
    fun isPassing(): Boolean = grade >= 2.0

    open fun summary(): String =
        "[$id] $name ($major) - 평점: %.1f".format(grade)

    data class Undergraduate(
        override val id: Int,
        override val name: String,
        override val major: String,
        override val grade: Double,
        val year: Int
    ) : Student(id, name, major, grade) {
        fun isSenior(): Boolean = year >= 4

        override fun summary(): String =
            super.summary() + " / 학년: $year 학년"
    }

    data class Graduate(
        override val id: Int,
        override val name: String,
        override val major: String,
        override val grade: Double,
        val thesisTitle: String
    ) : Student(id, name, major, grade) {
        fun thesisInfo(): String = "논문: $thesisTitle"

        override fun summary(): String =
            super.summary() + " / ${thesisInfo()}"
    }

    // 휴학 학생
    object OnLeave : Student(
        id = -1,
        name = "휴학생",
        major = "N/A",
        grade = 0.0
    ) {
        fun leaveMessage(): String = "현재 휴학 중"

        override fun summary(): String =
            "[$name] ${leaveMessage()}"
    }
}

fun main() {
    val students: List<Student> = listOf(
        Student.Undergraduate(1, "Foo", "Computer Science", 3.5, year = 4),
        Student.Undergraduate(2, "Bar", "AI", 1.8, year = 2),
        Student.Graduate(3, "Boo", "Electronics", 4.0, thesisTitle = "Power Electronics & Power Systems"),
        Student.OnLeave
    )

    students.forEach { student ->
        println(student.summary())

        println("합격 여부: ${if (student.isPassing()) "Pass" else "Fail"}")

        when (student) {
            is Student.Undergraduate ->
                println("최종학년 여부: ${student.isSenior()}")
            is Student.Graduate ->
                println(student.thesisInfo())
            Student.OnLeave ->
                println(Student.OnLeave.leaveMessage())
        }
    }
}
