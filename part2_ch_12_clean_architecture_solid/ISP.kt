
interface YearCalculationStrategy {
    fun calculate_remaining_years(grade: Int, max_grade: Int): Int
}

class DefaultYearCalculation : YearCalculationStrategy {
    override fun calculate_remaining_years(grade: Int, max_grade: Int): Int {
        val remaining = max_grade - grade
        return if (remaining < 0) 0 else remaining
    }
}

class UndergraduateYearCalculation(
    private val has_internship: Boolean
) : YearCalculationStrategy {
    override fun calculate_remaining_years(grade: Int, max_grade: Int): Int {
        val base_years = DefaultYearCalculation().calculate_remaining_years(grade, max_grade)
        return if (has_internship) base_years - 1 else base_years
    }
}

class GraduateYearCalculation(
    private val thesis_title: String
) : YearCalculationStrategy {
    override fun calculate_remaining_years(grade: Int, max_grade: Int): Int {
        val base_years = DefaultYearCalculation().calculate_remaining_years(grade, max_grade)
        return if (thesis_title.isNotEmpty()) base_years else base_years + 1
    }
}

class ExchangeYearCalculation(
    private val exchange_duration: Int
) : YearCalculationStrategy {
    override fun calculate_remaining_years(grade: Int, max_grade: Int): Int {
        val base_years = DefaultYearCalculation().calculate_remaining_years(grade, max_grade)
        return base_years + exchange_duration
    }
}

interface InternshipCertifiable {
    fun certify_internship(): String
}

interface ThesisVerifiable {
    fun verify_thesis_status(): String
}

open class Student(
    protected val name: String,
    protected val major: String,
    protected var grade: Int,
    protected val max_grade: Int,
    private val calculation_strategy: YearCalculationStrategy
) {
    open fun calculate_remaining_years(): Int {
        return calculation_strategy.calculate_remaining_years(grade, max_grade)
    }

    open fun print_info() {
        println("Name: $name")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $max_grade")
    }
}

class UndergraduateStudent(
    name: String,
    major: String,
    grade: Int,
    protected val has_internship: Boolean
) : Student(
    name,
    major,
    grade,
    max_grade = 4,
    calculation_strategy = UndergraduateYearCalculation(has_internship)
), InternshipCertifiable {
    override fun print_info() {
        println("Name: $name (Undergraduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $max_grade")
        println("Has Internship: $has_internship")
    }

    override fun certify_internship(): String {
        return if (has_internship) "Internship Certified" else "No Internship"
    }
}

class GraduateStudent(
    name: String,
    major: String,
    grade: Int,
    protected val thesis_title: String
) : Student(
    name,
    major,
    grade,
    max_grade = 2,
    calculation_strategy = GraduateYearCalculation(thesis_title)
), ThesisVerifiable {
    override fun print_info() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $max_grade")
        println("Thesis Title: $thesis_title")
    }

    override fun verify_thesis_status(): String {
        return if (thesis_title.isNotEmpty()) "Thesis Submitted" else "Thesis Pending"
    }
}

class ExchangeStudent(
    name: String,
    major: String,
    grade: Int,
    protected val exchange_duration: Int
) : Student(
    name,
    major,
    grade,
    max_grade = 3,
    calculation_strategy = ExchangeYearCalculation(exchange_duration)
) {
    override fun print_info() {
        println("Name: $name (Exchange)")
        println("Major: $major")
        println("Grade: $grade")
        println("Max Grade: $max_grade")
        println("Exchange Duration: $exchange_duration")
    }
}

fun main() {
    val students: List<Student> = listOf(
        UndergraduateStudent("Foo", "Computer Science", 2, true),
        ExchangeStudent("Bar", "Physics", 1, 1),
        GraduateStudent("Boo", "Electrical Engineering", 1, "AI Energy Optimization")
    )

    for (student in students) {
        println("Remaining Years: ${student.calculate_remaining_years()}")
        student.print_info()
        println()
    }

    val undergrads: List<InternshipCertifiable> = students.filterIsInstance<UndergraduateStudent>()
    for (student in undergrads) {
        println("${student.certify_internship()}")
    }

    val grads: List<ThesisVerifiable> = students.filterIsInstance<GraduateStudent>()
    for (student in grads) {
        println("${student.verify_thesis_status()}")
    }
}
