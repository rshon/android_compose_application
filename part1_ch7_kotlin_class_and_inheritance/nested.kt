class Student(val name: String, val major: String, var grade: Int) {
    class Address(val street: String, val city: String, val zipCode: String) {
        fun getAddress() = "$street, $city, $zipCode"
    }

    var address: Address? = null

    fun printInfo() {
        println("name: ${this.name}")
        println("major: ${this.major}")
        println("grade: ${this.grade}")
        address?.let {
            println("address: ${it.getAddress()}")
        }
    }
}

fun main() {
    val student1 = Student("Foo", "Computer Science", 3)

    val studentAddress = Student.Address("12 Heolleung-ro St", "Seoul", "06797")
    student1.address = studentAddress
    student1.printInfo()

    val student2 = Student("Bar", "Math", 2)
    student2.printInfo()
}
