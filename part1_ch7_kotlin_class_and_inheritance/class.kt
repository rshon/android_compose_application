class Student(name: String, major: String, grade: Int) {
    val name: String = name
    val major: String = major
    var grade: Int = grade

    fun printInfo() {
        println("name: ${this.name}")
        println("major: ${this.major}")
        println("grade: ${this.grade}")
    }
}

class Student1(name: String, major: String, grade: Int)

class Student2(var name: String, var major: String, var grade: Int)

class Student3(name: String, major: String, grade: Int) {
    val name: String = name
    val major: String = major
    var grade: Int = grade

    fun increaseGrade() {
        grade++
        println("${this.name} 학생의 학년이 ${this.grade}학년으로 증가했습니다.")
    }

    fun printInfo() {
        println("name: ${this.name}")
        println("major: ${this.major}")
        println("grade: ${this.grade}")
    }
}

class Student4(name: String, major: String, grade: Int) {
    val name: String = name
        get() {
            println("Getting name: $field")
            return field.uppercase()
        }

    var major: String = major
        set(value) {
            println("Setting major from $field to $value")
            field = value.trim()
        }

    var grade: Int = grade
        get() {
            println("Getting grade: $field")
            return field
        }
        set(value) {
            println("Setting grade from $field to $value")
            if (value in 1..4) {
                field = value
            } else {
                println("grade must be between 1 and 4.")
            }
        }
}

class Student5(name: String, major: String, initialGrade: Int) {
    val name: String
    val major: String
    var grade: Int

    init {
        println("start to init Student5")
        this.name = name.trim()
        this.major = major.trim()
        this.grade = if (initialGrade in 1..4) {
            initialGrade
        } else {
            println("grade must be between 1 and 4! set 1 as default value")
            1
        }
        println("Student5 done")
    }

    fun printInfo() {
        println("name: $name")
        println("major: $major")
        println("grade: $grade")
    }
}

class Student7(val name: String, val major: String) { // primary constructor
    var grade: Int

    // init block
    init {
        println("starts init block")
        this.grade = 1  // default value
    }

    // secondary constructor
    constructor(name: String, major: String, grade: Int) : this(name, major) {
        println("starts secondary constructor")
        this.grade = if (grade in 1..4) {
            grade
        } else {
            println("grade must be between 1 and 4! set 1 as default value")
            1
        }
    }

    fun printInfo() {
        println("name: $name")
        println("major: $major")
        println("grade: $grade")
    }
}

class Student8(val name: String) {
    var age: Int = 0
    var major: String? = null
    var grade: Int = 1

    init {
        println("Student8 init block: name - $name")
    }

    constructor(name: String, age: Int) : this(name) {
        println("Student8 secondary constructor 1: name - $name, age - $age")
        this.age = age
    }

    constructor(name: String, major: String, grade: Int) : this(name, 0) {
        println("Student8 secondary constructor 2: name - $name, major - $major, grade - $grade")
        this.major = major
        this.grade = grade
    }

    constructor(name: String, major: String) : this(name) {
        println("Student8 secondary constructor 3: name - $name, major - $major (default value)")
        this.major = major
    }

    fun printInfo() {
        println("name: $name")
        println("age: $age")
        println("major: ${major ?: "Not determined"}")
        println("grade: $grade")
    }
}

data class Student9(
    val id: Int,
    var name: String,
    var major: String,
    var grade: Int
)

fun testDataclass() {
    val student1 = Student9(1, "Alice", "Computer Science", 2)
    val student2 = Student9(2, "Bob", "Physics", 3)

    println("----- data class info -----")
    println(student1)  // automatically generates toString()
    println(student2)

    println("\n----- (equals()) -----")
    val student3 = Student9(1, "Alice", "Computer Science", 2)
    println("student1 == student2: ${student1 == student2}") // comparison
    println("student1 == student3: ${student1 == student3}")

    println("\n----- (hashCode()) -----")
    println("student1 hashCode: ${student1.hashCode()}")
    println("student2 hashCode: ${student2.hashCode()}")
    println("student3 hashCode: ${student3.hashCode()}")

    val students = setOf(student1, student2, student3)
    println("Set 크기: ${students.size}") // remove redundant objects

    println("\n----- (copy()) -----")
    val student4 = student1.copy(name = "Alicia", grade = 3)
    println("student1: $student1")
    println("student4 (name, grade are modified): $student4")

    println("\n----- (componentN()) -----")
    val (id, name, major, grade) = student1
    println("ID: $id, Name: $name, Major: $major, Grade: $grade")

    val idOnly = student1.component1()
    val nameAndMajor = Pair(student1.component2(), student1.component3())
    println("extracting only ID: $idOnly")
    println("extracting name and major: $nameAndMajor")
}

class Student10(val id: Int, var name: String) {
    companion object {
        var nextId: Int = 1

        fun create(name: String): Student10 {
            return Student10(nextId++, name)
        }
    }

    fun printInfo() {
        println("ID: $id, Name: $name")
    }
}

fun testCompanionObject() {
    val student1 = Student10.create("Alice")
    val student2 = Student10.create("Bob")
    val student3 = Student10.create("Charlie")

    student1.printInfo()
    student2.printInfo()
    student3.printInfo()

    println("Next ID: ${Student10.nextId}") // Companion Object 프로퍼티 접근
}

object StudentManager {
    private val students = mutableListOf<Student>()

    fun addStudent(student: Student) {
        students.add(student)
        println("${student.name} is registered")
    }

    fun getStudentByName(name: String): Student? {
        return students.find { it.name == name }
    }

    fun getAllStudents(): List<Student> {
        return students.toList()
    }

    fun updateStudentGrade(name: String, newGrade: Int) {
        val student = getStudentByName(name)
        student?.let {
            it.grade = newGrade
            println("grade of the student ${it.name} is changed as ${it.grade}.")
        } ?: println("Couldn't find student $name.")
    }
}

fun testObject() {
    val student1 = Student("Foo", "Computer Science", 3)
    val student2 = Student("Bar", "Math", 2)

    StudentManager.addStudent(student1)
    StudentManager.addStudent(student2)

    println(StudentManager.getAllStudents())

    StudentManager.updateStudentGrade("Foo", 4)

    val foundStudent = StudentManager.getStudentByName("밥")
    println(foundStudent)
}

fun main() {
    testObject()
    // testCompanionObject()

    // val student1 = Student3("Kale", "Computer Science", 2)
    // val student2 = Student3("Pole", "Electronic Engineering", 4)

    // student1.printInfo()
    // student2.printInfo()

    // val student3 = Student2("Torvald", "Computer Science", 3)
    // println(student3.name)

    // val student4 = Student4("Zerkerbug", "Computer Science", 1)
    // println(student4.name)

    // val student5 = Student5("Eric", "Computer Science", 5)
    // println(student5.printInfo())

    // println()

    // val student6 = Student7("Jobs", "N/A", 5)
    // println(student6.printInfo())

    // println()
    // println()

    // val student7 = Student8("Alice")
    // val student8 = Student8("Bob", 20)
    // val student9 = Student8("Charlie", "Computer Science", 3)
    // val student10 = Student8("David", "Physics")

    // student7.printInfo()
    // student8.printInfo()
    // student9.printInfo()
    // student10.printInfo()

    // testDataclass()
}
