open class Student(
    name: String,
    major: String,
    grade: Int
) {
    val name: String = name
    val major: String = major
    var grade: Int = grade

    open fun calculateRemainingYears(max_grade: Int = 4): Int {
        val remaining = max_grade - grade
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
    val student2 = Student("Bar", "Mathmatics", 4)
    val student3 = Student("Boo", "Computer Science", 2)

    val cs_dept = Department("Computer Science")
    val electronics_dept = Department("Electronics")

    cs_dept.register(student1)
    cs_dept.register(student3)
    electronics_dept.register(student2)

    cs_dept.printStudents()
    electronics_dept.printStudents()
}
