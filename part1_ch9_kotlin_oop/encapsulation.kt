
class Student(val name: String, val major: String, private var grade: Int) {
    fun update_grade(new_grade: Int) {
        if (new_grade !in 0..4) {
            println("grade must be between 0 and 4!")
            return
        }

        grade = new_grade
    }

    fun get_grade(): Int {
        return grade
    }

    fun print_info() {
        println("name: ${this.name}")
        println("major: ${this.major}")
        println("grade: ${this.grade}")
    }
}

fun main() {
    val student1 = Student("Foo", "Computer Science", 3)

    // student1.grade = 5       // error!
    student1.update_grade(4)
    println("grade: ${student1.get_grade()}")
    student1.print_info()
}
