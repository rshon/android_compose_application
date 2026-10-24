abstract class Student(
    protected val name: String,
    protected val major: String,
    protected val grade: Int
) {
    abstract fun introduce()

    fun study() {
        println("$name is studying $major.")
    }
}

class UndergraduateStudent(
    name: String,
    major: String,
    grade: Int
) : Student(name, major, grade) {
    override fun introduce() {
        println("name: $name (Undergraduate)")
        println("major: $major")
        println("grade: $grade")
    }
}

class GraduateStudent(
    name: String,
    major: String,
    grade: Int,
    val thesisTopic: String
) : Student(name, major, grade) {
    override fun introduce() {
        println("name: $name (Graduate)")
        println("major: $major")
        println("grade: $grade")
        println("thesis topic: $thesisTopic")
    }
}

fun main() {
    val undergraduateStudent = UndergraduateStudent("Foo", "Computer Science", 3)
    undergraduateStudent.introduce()
    undergraduateStudent.study()

    val graduateStudent = GraduateStudent("Bar", "Artificial Intelligence", 5, "Deep Learning")
    graduateStudent.introduce()
    graduateStudent.study()
}
