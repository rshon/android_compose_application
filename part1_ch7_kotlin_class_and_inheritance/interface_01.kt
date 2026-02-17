interface Student {
    val name: String
    val major: String
    val grade: Int
    fun introduce()
}

class UndergraduateStudent(
    override val name: String, 
    override val major: String, 
    override val grade: Int
) : Student {
    override fun introduce() {
        println("Name: $name (Undergraduate)")
        println("Major: $major")
        println("Grade: $grade")
    }
}

class GraduateStudent(
    override val name: String, 
    override val major: String, 
    override val grade: Int, 
    val thesis_topic: String
) : Student {
    override fun introduce() {
        println("Name: $name (Graduate)")
        println("Major: $major")
        println("Grade: $grade")
        println("Thesis Topic: $thesis_topic")
    }
}

fun main() {
    val undergraduateStudent = UndergraduateStudent("Foo", "Computer Science", 3)
    undergraduateStudent.introduce()

    val graduateStudent = GraduateStudent("Bar", "Artificial Intelligence", 5, "Deep Learning")
    graduateStudent.introduce()
}


// A 모듈에 있는 코드
internal class InternalClass {
    internal val prop = "Internal Property"
    internal fun doSomething() = "Something"
}

// A 모듈 내 다른 파일
fun useInternal() {
    val obj = InternalClass()  // 접근 가능
    println(obj.prop)          // 접근 가능
    obj.doSomething()          // 접근 가능
}

// 다른 모듈의 코드에서는 접근 불가
