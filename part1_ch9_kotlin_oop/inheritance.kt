open class Student(
    name: String,
    major: String,
    grade: Int
) {
    val name: String = name
    val major: String = major
    var grade: Int = grade

    open fun calculateRemainingYears(maxGrade: Int = 4): Int {
        val remaining = maxGrade - grade
        return if (remaining < 0) 0 else remaining
    }

    open fun printInfo() {
        println("Name: $name")
        println("Major: $major")
        println("Grade: $grade")
    }
}

class UndergraduateStudent(
    name: String,
    major: String,
    grade: Int,
    val hasInternship: Boolean
) : Student(name, major, grade) {
    override fun calculateRemainingYears(maxGrade: Int): Int {
        val baseYears = super.calculateRemainingYears(maxGrade)
        return if (hasInternship) baseYears - 1 else baseYears
    }

    fun calculateUndergradYears() = calculateRemainingYears(4)

    override fun printInfo() {
        println("Name: $name (Undergraduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Has Internship: $hasInternship")
    }
}

class GraduateStudent(
    name: String,
    major: String,
    grade: Int,
    val thesisTitle: String
) : Student(name, major, grade) {
    override fun calculateRemainingYears(maxGrade: Int): Int {
        val baseYears = super.calculateRemainingYears(maxGrade)
        return if (thesisTitle.isNotEmpty()) baseYears else baseYears + 1
    }

    fun calculateGradYears() = calculateRemainingYears(2)

    override fun printInfo() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Thesis Title: $thesisTitle")
    }
}

fun main() {
    val undergrad = UndergraduateStudent("Foo", "Computer Science", 2, true)
    val grad = GraduateStudent("Boo", "Electrical Engineering", 1, "AI Energy Optimization")

    println("Undergraduate Remaining Years (default): ${undergrad.calculateRemainingYears()}")
    println("Undergraduate Remaining Years (specific): ${undergrad.calculateUndergradYears()}")
    undergrad.printInfo()
    println()

    println("Graduate Remaining Years (default): ${grad.calculateRemainingYears()}")
    println("Graduate Remaining Years (specific): ${grad.calculateGradYears()}")
    grad.printInfo()
}
