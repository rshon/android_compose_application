interface YearCalculationStrategy {
    fun calculateRemainingYears(grade: Int, maxGrade: Int): Int
}

class DefaultYearCalculation : YearCalculationStrategy {
    override fun calculateRemainingYears(grade: Int, maxGrade: Int): Int {
        val remaining = maxGrade - grade
        return if (remaining < 0) 0 else remaining
    }
}

class UndergraduateYearCalculation(
    private val hasInternship: Boolean
) : YearCalculationStrategy {
    override fun calculateRemainingYears(grade: Int, maxGrade: Int): Int {
        val baseYears = DefaultYearCalculation().calculateRemainingYears(grade, maxGrade)
        return if (hasInternship) (baseYears - 1).coerceAtLeast(0) else baseYears
    }
}

class GraduateYearCalculation(
    private val thesisTitle: String
) : YearCalculationStrategy {
    override fun calculateRemainingYears(grade: Int, maxGrade: Int): Int {
        val baseYears = DefaultYearCalculation().calculateRemainingYears(grade, maxGrade)
        return if (thesisTitle.isNotEmpty()) baseYears else baseYears + 1
    }
}

open class Student(
    val name: String,
    val major: String,
    var grade: Int,
    val maxGrade: Int,
    private val calculationStrategy: YearCalculationStrategy
) {
    fun calculateRemainingYears(): Int {
        return calculationStrategy.calculateRemainingYears(grade, maxGrade)
    }

    open fun printInfo() {
        println("Name: $name")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $maxGrade")
    }
}

class UndergraduateStudent(
    name: String,
    major: String,
    grade: Int,
    protected val hasInternship: Boolean
) : Student(
    name,
    major,
    grade,
    maxGrade = 4,
    calculationStrategy = UndergraduateYearCalculation(hasInternship)
) {
    override fun printInfo() {
        println("Name: $name (Undergraduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $maxGrade")
        println("Has Internship: $hasInternship")
    }
}

class GraduateStudent(
    name: String,
    major: String,
    grade: Int,
    protected val thesisTitle: String
) : Student(
    name,
    major,
    grade,
    maxGrade = 2,
    calculationStrategy = GraduateYearCalculation(thesisTitle)
) {
    override fun printInfo() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $maxGrade")
        println("Thesis Title: $thesisTitle")
    }
}

class ExchangeYearCalculation(
    private val exchangeDuration: Int
) : YearCalculationStrategy {
    override fun calculateRemainingYears(grade: Int, maxGrade: Int): Int {
        val baseYears = DefaultYearCalculation().calculateRemainingYears(grade, maxGrade)
        return baseYears + exchangeDuration
    }
}

class ExchangeStudent(
    name: String,
    major: String,
    grade: Int,
    val exchangeDuration: Int
) : Student(
    name,
    major,
    grade,
    maxGrade = 3,
    calculationStrategy = ExchangeYearCalculation(exchangeDuration)
)

fun main() {
    val students: List<Student> = listOf(
        UndergraduateStudent("Foo", "Computer Science", 2, true),
        ExchangeStudent("Bar", "Physics", 1, 1),
        GraduateStudent("Boo", "Electrical Engineering", 1, "AI Energy Optimization")
    )
    for (student in students) {
        println("Remaining Years: ${student.calculateRemainingYears()}")
        student.printInfo()
        println()
    }
}
