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

class Department(val name: String) {
    private val students: MutableList<Student> = mutableListOf()

    fun register(student: Student) {
        students.add(student)
        println("${student.name} is registered to the department ${this.name}.")
    }

    fun printStudents() {
        println("\n--- ${this.name} student list ---")
        if (students.isEmpty()) {
            println("There is no student.")
            return
        }
        students.forEach { it.printInfo() }
    }
}

fun main() {
    val student1 = Student("Foo", "Computer Science", 1)
    val student2 = Student("Bar", "Mathematics", 4)
    val student3 = Student("Boo", "Computer Science", 2)

    val csDept = Department("Computer Science")
    val electronicsDept = Department("Electronics")

    csDept.register(student1)
    csDept.register(student3)
    electronicsDept.register(student2)

    csDept.printStudents()
    electronicsDept.printStudents()
}
