class Student(val name: String, val major: String, private var grade: Int) {
    fun updateGrade(newGrade: Int) {
        if (newGrade !in 0..4) {
            println("grade must be between 0 and 4!")
            return
        }

        grade = newGrade
    }

    fun getGrade(): Int {
        return grade
    }

    fun printInfo() {
        println("name: ${this.name}")
        println("major: ${this.major}")
        println("grade: ${this.grade}")
    }
}

fun main() {
    val student1 = Student("Foo", "Computer Science", 3)

    // student1.grade = 5       // error!
    student1.updateGrade(4)
    println("grade: ${student1.getGrade()}")
    student1.printInfo()
}
