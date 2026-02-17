import kotlin.reflect.KClass

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

interface StudentFormatter {
    fun format(student: Student): String
}

data class StudentData(
    val name: String,
    val major: String,
    var grade: Int,
    val max_grade: Int
)

class YearCalculator(
    private val data: StudentData,
    private val strategy: YearCalculationStrategy
) {
    fun calculate_remaining_years(): Int {
        return strategy.calculate_remaining_years(data.grade, data.max_grade)
    }
}

open class Student(
    val data: StudentData,
    val calculator: YearCalculator
)

class UndergraduateFormatter : StudentFormatter {
    override fun format(student: Student): String {
        require(student is UndergraduateStudent) { "UndergraduateFormatter can only format UndergraduateStudent" }
        return """
            name: ${student.data.name}
            major: ${student.data.major}
            grade: ${student.data.grade}
            max_grade: ${student.data.max_grade}
            has_internship: ${student.has_internship}
        """.trimIndent()
    }
}

class GraduateFormatter : StudentFormatter {
    override fun format(student: Student): String {
        require(student is GraduateStudent) { "GraduateFormatter can only format GraduateStudent" }
        return """
            name: ${student.data.name}
            major: ${student.data.major}
            grade: ${student.data.grade}
            max_grade: ${student.data.max_grade}
            thesis_title: ${student.thesis_title}
        """.trimIndent()
    }
}

class ExchangeFormatter : StudentFormatter {
    override fun format(student: Student): String {
        require(student is ExchangeStudent) { "ExchangeFormatter can only format ExchangeStudent" }
        return """
            name: ${student.data.name}
            major: ${student.data.major}
            grade: ${student.data.grade}
            max_grade: ${student.data.max_grade}
            exchange_duration: ${student.exchange_duration}
        """.trimIndent()
    }
}

class DefaultFormatter : StudentFormatter {
    override fun format(student: Student): String {
        return """
            name: ${student.data.name}
            major: ${student.data.major}
            grade: ${student.data.grade}
            max_grade: ${student.data.max_grade}
        """.trimIndent()
    }
}

class StudentPrinter(
    private val formatters: Map<KClass<out Student>, StudentFormatter>,
    private val default_formatter: StudentFormatter = DefaultFormatter()
) {
    fun print(student: Student) {
        val formatter = formatters[student::class] ?: default_formatter
        println(formatter.format(student))
    }
}

class UndergraduateStudent(
    data: StudentData,
    calculator: YearCalculator,
    val has_internship: Boolean
) : Student(data, calculator), InternshipCertifiable {
    override fun certify_internship(): String {
        return if (has_internship) "Internship Certified" else "No Internship"
    }
}

class GraduateStudent(
    data: StudentData,
    calculator: YearCalculator,
    val thesis_title: String
) : Student(data, calculator), ThesisVerifiable {
    override fun verify_thesis_status(): String {
        return if (thesis_title.isNotEmpty()) "Thesis Submitted" else "Thesis Pending"
    }
}

class ExchangeStudent(
    data: StudentData,
    calculator: YearCalculator,
    val exchange_duration: Int
) : Student(data, calculator)

interface StudentFactory {
    fun create_undergraduate_student(name: String, major: String, grade: Int, has_internship: Boolean): UndergraduateStudent
    fun create_graduate_student(name: String, major: String, grade: Int, thesis_title: String): GraduateStudent
    fun create_exchange_student(name: String, major: String, grade: Int, exchange_duration: Int): ExchangeStudent
}

class DefaultStudentFactory : StudentFactory {
    override fun create_undergraduate_student(name: String, major: String, grade: Int, has_internship: Boolean): UndergraduateStudent {
        val data = StudentData(name, major, grade, 4)
        val calculator = YearCalculator(data, UndergraduateYearCalculation(has_internship))
        return UndergraduateStudent(data, calculator, has_internship)
    }

    override fun create_graduate_student(name: String, major: String, grade: Int, thesis_title: String): GraduateStudent {
        val data = StudentData(name, major, grade, 2)
        val calculator = YearCalculator(data, GraduateYearCalculation(thesis_title))
        return GraduateStudent(data, calculator, thesis_title)
    }

    override fun create_exchange_student(name: String, major: String, grade: Int, exchange_duration: Int): ExchangeStudent {
        val data = StudentData(name, major, grade, 3)
        val calculator = YearCalculator(data, ExchangeYearCalculation(exchange_duration))
        return ExchangeStudent(data, calculator, exchange_duration)
    }
}

fun main() {
    val factory: StudentFactory = DefaultStudentFactory()
    val students: List<Student> = listOf(
        factory.create_undergraduate_student("Foo", "Computer Science", 2, true),
        factory.create_exchange_student("Bar", "Physics", 1, 1),
        factory.create_graduate_student("Boo", "Electrical Engineering", 1, "AI Energy Optimization")
    )

    val printer = StudentPrinter(
        formatters = mapOf(
            UndergraduateStudent::class to UndergraduateFormatter(),
            GraduateStudent::class to GraduateFormatter(),
            ExchangeStudent::class to ExchangeFormatter()
        )
    )

    for (student in students) {
        println("remaining_years: ${student.calculator.calculate_remaining_years()}")
        printer.print(student)
        println()
    }

    val undergrads: List<InternshipCertifiable> = students.filterIsInstance<UndergraduateStudent>()
    for (student in undergrads) {
        println(student.certify_internship())
    }

    val grads: List<ThesisVerifiable> = students.filterIsInstance<GraduateStudent>()
    for (student in grads) {
        println(student.verify_thesis_status())
    }
}


