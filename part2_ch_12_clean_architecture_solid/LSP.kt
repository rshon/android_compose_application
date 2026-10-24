

interface Animal {
    fun makeSound()
}

class Dog: Animal {
    override fun makeSound() {
        println("Woof")
    }
}

class Cat: Animal {
    override fun makeSound() {
        println("Meow")
    }
}

fun testAnimal() {
    var animal: Animal

    animal = Dog()
    animal.makeSound()
    
    animal = Cat()
    animal.makeSound()
}


abstract class Student(
    protected val name: String,
    protected val major: String,
    protected var grade: Int
) {
    protected open fun calculateBaseYears(maxGrade: Int): Int {
        val remaining = maxGrade - grade
        return if (remaining < 0) 0 else remaining
    }

    abstract fun calculateRemainingYears(): Int

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
    override fun calculateRemainingYears(): Int {
        val baseYears = calculateBaseYears(4)
        return if (hasInternship) (baseYears - 1).coerceAtLeast(0) else baseYears
    }

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
    override fun calculateRemainingYears(): Int {
        val baseYears = calculateBaseYears(2)
        return if (thesisTitle.isNotEmpty()) baseYears else baseYears + 1
    }

    override fun printInfo() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Thesis Title: $thesisTitle")
    }
}

fun main() {
    val undergrad = UndergraduateStudent("Foo", "Computer Science", 2, true)
    val gradWithThesis = GraduateStudent("Bob", "Physics", 1, "")
    val gradNoThesis = GraduateStudent("Boo", "Electrical Engineering", 1, "AI Energy Optimization")

    val students: List<Student> = listOf(undergrad, gradWithThesis, gradNoThesis)
    for (student in students) {
        println("Remaining Years: ${student.calculateRemainingYears()}")
        student.printInfo()
        println()
    }
}



