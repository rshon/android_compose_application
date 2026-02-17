open class Student(
    name: String,
    major: String,
    grade: Int
) {
    val name: String = name
    val major: String = major
    var grade: Int = grade

    open fun calculate_remaining_years(max_grade: Int = 4): Int {
        val remaining = max_grade - grade
        return if (remaining < 0) 0 else remaining
    }

    open fun print_info() {
        println("Name: $name")
        println("Major: $major")
        println("Grade: $grade")
    }
}

class UndergraduateStudent(
    name: String,
    major: String,
    grade: Int,
    val has_internship: Boolean
) : Student(name, major, grade) {
    override fun calculate_remaining_years(max_grade: Int): Int {
        val base_years = super.calculate_remaining_years(max_grade)
        return if (has_internship) base_years - 1 else base_years
    }

    fun calculate_undergrad_years() = calculate_remaining_years(4)

    override fun print_info() {
        println("Name: $name (Undergraduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Has Internship: $has_internship")
    }
}

class GraduateStudent(
    name: String,
    major: String,
    grade: Int,
    val thesis_title: String
) : Student(name, major, grade) {
    override fun calculate_remaining_years(max_grade: Int): Int {
        val base_years = super.calculate_remaining_years(max_grade)
        return if (thesis_title.isNotEmpty()) base_years else base_years + 1
    }

    fun calculate_grad_years() = calculate_remaining_years(2)

    override fun print_info() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Thesis Title: $thesis_title")
    }
}

fun main() {
    val undergrad = UndergraduateStudent("Foo", "Computer Science", 2, true)
    val grad = GraduateStudent("Boo", "Electrical Engineering", 1, "AI Energy Optimization")

    println("Undergraduate Remaining Years (default): ${undergrad.calculate_remaining_years()}")
    println("Undergraduate Remaining Years (specific): ${undergrad.calculate_undergrad_years()}")
    undergrad.print_info()
    println()

    println("Graduate Remaining Years (default): ${grad.calculate_remaining_years()}")
    println("Graduate Remaining Years (specific): ${grad.calculate_grad_years()}")
    grad.print_info()
}
