interface Student {
    var name: String
    var major: String
    var grade: Int
    fun introduce()
}

class UndergraduateStudent(
    nameParam: String,
    majorParam: String,
    gradeParam: Int
) : Student {

    override var name: String = nameParam
        get() = "Undergrad-$field"
        set(value) { field = value.removePrefix("Undergrad-") }

    override var major: String = majorParam
        get() = field.uppercase()
        set(value) { field = value.lowercase() }

    override var grade: Int = gradeParam
        get() = if (field < 0) 0 else field
        set(value) { field = if (value > 4) 4 else value }

    override fun introduce() {
        println("Name: $name (Undergraduate)")
        println("Major: $major")
        println("Grade: $grade")
    }
}

class GraduateStudent(
    nameParam: String,
    majorParam: String,
    graduateYearParam: Int,
    val thesisTopic: String
) : Student {

    private var graduateYear: Int = graduateYearParam

    override var name: String = nameParam
        get() = "Grad-$field"
        set(value) { field = value.removePrefix("Grad-") }

    override var major: String = majorParam
        get() = field.lowercase()
        set(value) { field = value.uppercase() }

    override var grade: Int
        get() = graduateYear + BASE_UNDERGRAD_YEARS
        set(value) {
            graduateYear = value - BASE_UNDERGRAD_YEARS
        }

    override fun introduce() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Thesis Topic: $thesisTopic")
    }

    companion object {
        const val BASE_UNDERGRAD_YEARS = 2
    }
}

fun main() {
    val undergrad1 = UndergraduateStudent("Foo", "Computer Science", 2)
    val undergrad2 = UndergraduateStudent("Bar", "Mathematics", 3)
    val grad = GraduateStudent("Boo", "Electrical Engineering", 2, "AI Energy Optimization")

    undergrad1.introduce()
    undergrad1.name = "Undergrad-Bar"  // Update Foo to Bar
    undergrad1.grade = 5              // Limited to 4
    println("After update:")
    undergrad1.introduce()

    println("---")
    undergrad2.introduce()

    println("---")
    grad.introduce()
    grad.major = "PHYSICS"            // Update major
    println("After update:")
    grad.introduce()
}
