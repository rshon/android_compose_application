import kotlin.reflect.KClass

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

class ExchangeYearCalculation(
    private val exchangeDuration: Int
) : YearCalculationStrategy {
    override fun calculateRemainingYears(grade: Int, maxGrade: Int): Int {
        val baseYears = DefaultYearCalculation().calculateRemainingYears(grade, maxGrade)
        return baseYears + exchangeDuration
    }
}

interface InternshipCertifiable {
    fun certifyInternship(): String
}

interface ThesisVerifiable {
    fun verifyThesisStatus(): String
}

interface StudentFormatter {
    fun format(student: Student): String
}

data class StudentData(
    val name: String,
    val major: String,
    var grade: Int,
    val maxGrade: Int
)

class YearCalculator(
    private val data: StudentData,
    private val strategy: YearCalculationStrategy
) {
    fun calculateRemainingYears(): Int {
        return strategy.calculateRemainingYears(data.grade, data.maxGrade)
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
            max_grade: ${student.data.maxGrade}
            has_internship: ${student.hasInternship}
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
            max_grade: ${student.data.maxGrade}
            thesis_title: ${student.thesisTitle}
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
            max_grade: ${student.data.maxGrade}
            exchange_duration: ${student.exchangeDuration}
        """.trimIndent()
    }
}

class DefaultFormatter : StudentFormatter {
    override fun format(student: Student): String {
        return """
            name: ${student.data.name}
            major: ${student.data.major}
            grade: ${student.data.grade}
            max_grade: ${student.data.maxGrade}
        """.trimIndent()
    }
}

class StudentPrinter(
    private val formatters: Map<KClass<out Student>, StudentFormatter>,
    private val defaultFormatter: StudentFormatter = DefaultFormatter()
) {
    fun print(student: Student) {
        val formatter = formatters[student::class] ?: defaultFormatter
        println(formatter.format(student))
    }
}

class UndergraduateStudent(
    data: StudentData,
    calculator: YearCalculator,
    val hasInternship: Boolean
) : Student(data, calculator), InternshipCertifiable {
    override fun certifyInternship(): String {
        return if (hasInternship) "Internship Certified" else "No Internship"
    }
}

class GraduateStudent(
    data: StudentData,
    calculator: YearCalculator,
    val thesisTitle: String
) : Student(data, calculator), ThesisVerifiable {
    override fun verifyThesisStatus(): String {
        return if (thesisTitle.isNotEmpty()) "Thesis Submitted" else "Thesis Pending"
    }
}

class ExchangeStudent(
    data: StudentData,
    calculator: YearCalculator,
    val exchangeDuration: Int
) : Student(data, calculator)

interface StudentFactory {
    fun createUndergraduateStudent(name: String, major: String, grade: Int, hasInternship: Boolean): UndergraduateStudent
    fun createGraduateStudent(name: String, major: String, grade: Int, thesisTitle: String): GraduateStudent
    fun createExchangeStudent(name: String, major: String, grade: Int, exchangeDuration: Int): ExchangeStudent
}

class DefaultStudentFactory : StudentFactory {
    override fun createUndergraduateStudent(name: String, major: String, grade: Int, hasInternship: Boolean): UndergraduateStudent {
        val data = StudentData(name, major, grade, 4)
        val calculator = YearCalculator(data, UndergraduateYearCalculation(hasInternship))
        return UndergraduateStudent(data, calculator, hasInternship)
    }

    override fun createGraduateStudent(name: String, major: String, grade: Int, thesisTitle: String): GraduateStudent {
        val data = StudentData(name, major, grade, 2)
        val calculator = YearCalculator(data, GraduateYearCalculation(thesisTitle))
        return GraduateStudent(data, calculator, thesisTitle)
    }

    override fun createExchangeStudent(name: String, major: String, grade: Int, exchangeDuration: Int): ExchangeStudent {
        val data = StudentData(name, major, grade, 3)
        val calculator = YearCalculator(data, ExchangeYearCalculation(exchangeDuration))
        return ExchangeStudent(data, calculator, exchangeDuration)
    }
}

fun main() {
    val factory: StudentFactory = DefaultStudentFactory()
    val students: List<Student> = listOf(
        factory.createUndergraduateStudent("Foo", "Computer Science", 2, true),
        factory.createExchangeStudent("Bar", "Physics", 1, 1),
        factory.createGraduateStudent("Boo", "Electrical Engineering", 1, "AI Energy Optimization")
    )

    val printer = StudentPrinter(
        formatters = mapOf(
            UndergraduateStudent::class to UndergraduateFormatter(),
            GraduateStudent::class to GraduateFormatter(),
            ExchangeStudent::class to ExchangeFormatter()
        )
    )

    for (student in students) {
        println("remaining_years: ${student.calculator.calculateRemainingYears()}")
        printer.print(student)
        println()
    }

    val undergrads: List<InternshipCertifiable> = students.filterIsInstance<UndergraduateStudent>()
    for (student in undergrads) {
        println(student.certifyInternship())
    }

    val grads: List<ThesisVerifiable> = students.filterIsInstance<GraduateStudent>()
    for (student in grads) {
        println(student.verifyThesisStatus())
    }
}
