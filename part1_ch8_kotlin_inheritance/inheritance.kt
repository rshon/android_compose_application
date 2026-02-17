

open class Student(
    protected val name: String, 
    protected var major: String, 
    protected var grade: Int
){
    open fun introduce() {
        println("name: ${name}")
        println("major: ${major}")
        println("grade: ${grade}")
    }
}

class GraduateStudent(
    name: String, 
    major: String, 
    grade: Int, 
    protected var thesis_topic: String
) : Student(name, major, grade) {
    override fun introduce() {
        super.introduce()
        println("thesis_topic: ${thesis_topic}")
    }
}

fun main() {
    val student = Student("Foo", "Computer Science", 2)
    val gradStudent = GraduateStudent("Bar", "Electronics", 4, "Reinforcement Learning for Electron Dynamics")
    
    student.introduce() 
    gradStudent.introduce()
}
