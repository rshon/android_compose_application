
interface Studyable {
    fun study()
}

open class Student(
    val name: String,
    var major: String,
    var grade: Int
) : Studyable {
    override fun study() {
        println("$name in $major department is studying in basic way.")
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
    grade: Int
) : Student(name, major, grade) {
    override fun study() {
        println("$name is studying undergraduate courses")
    }
}

class ExchangedStudent(
    name: String,
    major: String,
    grade: Int,
    val homeUniversity: String
) : Student(name, major, grade) {
    override fun study() {
        println("exchanged student $name takes classes, experience the local culture, and study the language.")
    }

    override fun printInfo() {
        super.printInfo()
        println("Home University: $homeUniversity")
    }
}

class GraduateStudent(
    name: String,
    major: String,
    grade: Int,
    val researchTopic: String
) : Student(name, major, grade) {
    override fun study() {
        println("$name conducts in-depth research and focus on academic papers")
    }

    override fun printInfo() {
        super.printInfo()
        println("Research Topic: $researchTopic")
    }
}

fun main() {
    val undergrad = UndergraduateStudent("Foo", "Computer Science", 1)
    val exchange = ExchangedStudent("Bar", "Mathmatics", 4, "University of Sydney")
    val grad = GraduateStudent("Boo", "Computer Science", 2, "Recommender System for OTT")

    println("--- Way of studying  ---")
    val studyingPeople: List<Studyable> = listOf(undergrad, exchange, grad)

    for (person in studyingPeople) {
        person.study()
    }
}
