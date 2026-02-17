data class PersonalInfo(val firstName: String, val lastName: String, var age: Int) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class AcademicInfo(var major: String, var grade: Int)

class Student(
    val personal_info: PersonalInfo,
    val academic_info: AcademicInfo
) {
    val fullName: String
        get() = personal_info.fullName

    val age: Int
        get() = personal_info.age

    fun printInfo() {
        println("Name: $fullName")
        println("Age: $age")
        println("Major: ${academic_info.major}")
        println("Grade Level: ${academic_info.grade}")
    }

    fun changeMajor(newMajor: String) {
        academic_info.major = newMajor
        println("$fullName's major changed to $newMajor.")
    }

    fun promoteGrade() {
        academic_info.grade++
        println("$fullName is now in grade ${academic_info.grade}.")
    }

    fun increaseAge() {
        personal_info.age++
        println("$fullName's age increased to ${personal_info.age}.")
    }
}

interface Studyable {
    fun study()
}

fun main() {
    val student1 = Student(PersonalInfo("Foo", "Bar", 20), AcademicInfo("Computer Science", 1))
    student1.printInfo()
    student1.changeMajor("Software Engineering")
    student1.promoteGrade()
    student1.increaseAge()
    student1.printInfo()
    println()
}
