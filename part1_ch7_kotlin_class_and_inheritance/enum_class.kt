data class Student(
    val id: String,
    val name: String,
    val grade: Int,
    val major: String
)

enum class Grade(val value: Int, val description: String) {
    FRESHMAN(1, "1"),
    SOPHOMORE(2, "2"),
    JUNIOR(3, "3"),
    SENIOR(4, "4");

    companion object {
        fun fromValue(value: Int): Grade? = values().find { it.value == value }
    }
}

fun printGradeInfo(grade: Grade) {
    when (grade) {
        Grade.FRESHMAN -> {
            println("Grade: Freshman, Value: ${grade.value}, Description: ${grade.description}")
            println("Tip: Focus on adapting to university life.")
        }
        Grade.SOPHOMORE -> {
            println("Grade: Sophomore, Value: ${grade.value}, Description: ${grade.description}")
            println("Tip: Start exploring major-specific courses.")
        }
        Grade.JUNIOR -> {
            println("Grade: Junior, Value: ${grade.value}, Description: ${grade.description}")
            println("Tip: Consider internships or research.")
        }
        Grade.SENIOR -> {
            println("Grade: Senior, Value: ${grade.value}, Description: ${grade.description}")
            println("Tip: Prepare for graduation and future career/studies.")
        }
    }
}

fun processGradeByValue(value: Int) {
    val grade = Grade.fromValue(value) // Int 값을 enum 객체로 변환

    when (grade) {
        Grade.FRESHMAN -> println("Processing Freshman (value $value)")
        Grade.SOPHOMORE -> println("Processing Sophomore (value $value)")
        Grade.JUNIOR -> println("Processing Junior (value $value)")
        Grade.SENIOR -> println("Processing Senior (value $value)")
        null -> println("Unknown grade value: $value") // fromValue가 null을 반환할 경우 처리
    }
}

fun testGradeEnum() {
    val myGrade = Grade.JUNIOR
    printGradeInfo(myGrade)

    println("\n---")

    val anotherGrade = Grade.FRESHMAN
    printGradeInfo(anotherGrade)
}

fun testGradeProcess() {
    processGradeByValue(2) // SOPHOMORE
    processGradeByValue(4) // SENIOR
    processGradeByValue(5) // Unknown value
}

fun testBasicEnum() {
    val student = Student("20210001", "Foo", 2, "Computer Science")
    val studentGrade = Grade.fromValue(student.grade)
    println("${student.name} is ${studentGrade?.description}-year student")
}

enum class Major(val code: String, val fullName: String) {
    COMPUTER_SCIENCE("CS", "Computer Science"),
    ELECTRICAL_ENGINEERING("EE", "Electrical Engineering"),
    MECHANICAL_ENGINEERING("ME", "Mechanical Engineering"),
    BUSINESS("BIZ", "Business");

    companion object {
        fun fromCode(code: String): Major? = values().find { it.code == code }
        fun fromFullName(name: String): Major? = values().find { it.fullName == name }
    }
}

fun testMajorEnum() {
    val majorCode = Major.fromFullName("Computer Science")
    println("Major code: ${majorCode?.code}")
}

enum class StudentStatus(val code: Int) {
    ACTIVE(1) {
        override fun getDescription() = "Attending"
    },
    ON_LEAVE(2) {
        override fun getDescription() = "Leave of absence"  // 휴학
    },
    GRADUATED(3) {
        override fun getDescription() = "Graduated"
    },
    WITHDRAWN(4) {
        override fun getDescription() = "Dropped out"  // 자퇴
    };

    abstract fun getDescription(): String
}

data class ExtendedStudent(
    val id: String,
    val name: String,
    val grade: Int,
    val major: String,
    val status: StudentStatus
)

fun testStudentStatus() {
    val student = ExtendedStudent("20210001", "Foo", 2, "Computer Science", StudentStatus.ACTIVE)
    println("${student.name}'s status is ${student.status.getDescription()}")
}

enum class MaxCredits(val grade: Int, val credits: Int) {
    FRESHMAN(1, 18),
    SOPHOMORE(2, 21),
    JUNIOR(3, 24),
    SENIOR(4, 24);

    companion object {
        fun getMaxCreditsByGrade(grade: Int): Int {
            return values().find { it.grade == grade }?.credits ?: 18
        }
    }
}

fun testMaximumPoint() {
    val student = Student("20210001", "Foo", 2, "Computer Science")
    val maxCredits = MaxCredits.getMaxCreditsByGrade(student.grade)
    println("The maximum number of credits for ${student.grade} year is $maxCredits")
}

fun main() {
    testBasicEnum()
    testMajorEnum()
    testStudentStatus()
    testMaximumPoint()
}
