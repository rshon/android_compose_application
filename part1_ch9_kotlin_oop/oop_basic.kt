
open class Person(val name: String) {
    open fun introduce() {
        println("Name: $name")
    }
}

class Student(name: String, val major: String, val grade: Int) : Person(name) {
    override fun introduce() {
        super.introduce()
        println("Major: $major")
        println("Grade: $grade")
    }
}

fun testGeneralization() {
    val student = Student("Foo", "Computer Science", 2)
    student.introduce()
}

fun main() {
    testGeneralization()
}
