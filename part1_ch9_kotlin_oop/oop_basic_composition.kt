data class PersonalInfo(val firstName: String, val lastName: String, var age: Int) {
    val fullName: String
        get() = "$firstName $lastName"
}

data class AcademicInfo(var major: String, var grade: Int)

class Student(
    val personalInfo: PersonalInfo,
    val academicInfo: AcademicInfo
) {
    val fullName: String
        get() = personalInfo.fullName

    val age: Int
        get() = personalInfo.age

    fun printInfo() {
        println("Name: $fullName")
        println("Age: $age")
        println("Major: ${academicInfo.major}")
        println("Grade Level: ${academicInfo.grade}")
    }

    fun changeMajor(newMajor: String) {
        academicInfo.major = newMajor
        println("$fullName's major changed to $newMajor.")
    }

    fun promoteGrade() {
        academicInfo.grade++
        println("$fullName is now in grade ${academicInfo.grade}.")
    }

    fun increaseAge() {
        personalInfo.age++
        println("$fullName's age increased to ${personalInfo.age}.")
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
