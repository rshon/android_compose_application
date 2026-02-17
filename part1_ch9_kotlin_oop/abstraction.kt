class Student(val name: String, val major: String, var grade: Int) {
    fun introduce() {
        println("name: $name")
        println("major: $major")
        println("grade: $grade")
    }
}

fun main() {
    val student1 = Student("Foo", "Computer Science", 3)
    student1.introduce()
}
