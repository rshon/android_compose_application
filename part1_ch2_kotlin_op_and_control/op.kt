fun testArithmeticOperators() {
    val a = 10
    val b = 20

    println("a + b = ${a + b}")
    println("a - b = ${a - b}")
    println("a * b = ${a * b}")
    println("a / b = ${a / b}")
    println("a % b = ${a % b}")
    println("a == b = ${a == b}")
    println("a != b = ${a != b}")
    println("a > b = ${a > b}")
}

fun testIncrementAndDecrement() {
    var num = 10
    println("num++ = ${num++}")
    println("num = $num")
    println("++num = ${++num}")
    println("num = $num")

    println("num-- = ${num--}")
    println("num = $num")
    println("--num = ${--num}")
    println("num = $num")
}

fun testComparisonOperators() {
    val x = 2
    val y = 3

    println("x == y: ${x == y}")
    println("x != y: ${x != y}")
    println("x > y: ${x > y}")
    println("x < y: ${x < y}")
    println("x >= y: ${x >= y}")
    println("x <= y: ${x <= y}")
}

fun testAssignmentOperators() {
    var num = 10
    num += 1
    println("num = $num")
    num -= 1
    println("num = $num")
    num *= 2
    println("num = $num")
    num /= 2
    println("num = $num")
    num %= 3
    println("num = $num")
}

fun testBitOperators() {
    println("3 and 1 = ${3 and 1}")
    println("2 or 1 = ${2 or 1}")
    println("3 xor 2 = ${3 xor 2}")
    println("3 shl 2 = ${3 shl 2}")
    println("12 shr 2 = ${12 shr 2}")
    println("10 ushr 2 = ${10 ushr 2}")
    println("3.inv() = ${3.inv()}")
}

fun testLogicalOperators() {
    val a = true
    val b = false

    println("a && b: ${a && b}")
    println("a || b: ${a || b}")

    println("!a: ${!a}")
    println("!b: ${!b}")
}

fun testRange() {
    println("1..5")
    for (i in 1..5) {
        print("$i ")
    }

    println("")
    println("5 downTo 1")
    for (i in 5 downTo 1) {
        print("$i ")
    }

    println("")
    println("1..5 step 2")
    for (i in 1..5 step 2) {
        print("$i ")
    }

    println("")
    println("5 downTo 1 step 2")
    for (i in 5 downTo 1 step 2) {
        print("$i ")
    }

    println("")
    println("1 until 5")
    for (i in 1 until 5) {
        print("$i ")
    }
}

fun testLet() {
    val name = "Hello"
    val length = name.let {
        println("Name: $it")
        it.length
    }
    println("Length: $length")

    val text: String? = null
    val result = text?.let {
        "Text is: $it"
    } ?: "No text"
    println(result)

    val validText: String? = "Hello"
    val validResult = validText?.let {
        "Text is: $it"
    }
    println(validResult)
}

data class Person(var name: String, var age: Int)

fun testRunWith() {
    val person = Person("Turing", 30)

    val descriptionRun = person.run {
        name = "Oppenheimer"
        age += 1
        "Name: $name, Age: $age"
    }
    println("Run result: $descriptionRun")

    val descriptionWith = with(person) {
        name = "Maxwell"
        age -= 2
        "Name: $name, Age: $age"
    }
    println("With result: $descriptionWith")
}

fun testAccessOperators() {
    val str = "Hello"
    println("점 연산자: ${str.length}")

    val nullableStr: String? = null
    println("안전 호출 연산자: ${nullableStr?.length}")

    val result = nullableStr?.length ?: -1
    println("엘비스 연산자: $result")

    val nonNullStr: String? = "Kotlin"
    println("단언 연산자: ${nonNullStr!!.length}")

    val lengthFunction = String::length
    println("멤버 참조 연산자: ${lengthFunction("Kotlin")}")
}

fun testTypeCastingOperator() {
    open class Animal
    class Dog : Animal()

    val myAnimal: Animal = Dog()  // Dog 객체를 Animal 타입으로 저장

    // 확실히 Dog으로 변환 가능하므로 OK
    val myDog: Dog = myAnimal as Dog
    println("성공: $myDog")

    val someObject: Any = "Hello"  // String 객체를 Any 타입으로 저장
    // val num: Int = someObject as Int  // !! 위험: 실행 시 ClassCastException 발생(String은 Int가 아님)
}

fun testTypeCastingOperator2() {
    open class Animal

    class Cat : Animal()

    class Dog : Animal()

    val myAnimal: Animal = Cat()  // Cat 객체를 Animal 타입으로 저장

    // Dog으로 변환 시도. Cat이므로 null이 반환된다.
    val myDog: Dog? = myAnimal as? Dog
    println("실패 시 null: $myDog") // 출력: 실패 시 null: null

    // null이 아닐 때만 코드 실행(엘비스 연산자 ?: 와 함께 사용하면 편리)
    val actualDog: Dog = (myAnimal as? Dog) ?: Dog()  // Dog으로 캐스팅 못하면 새 Dog 객체 생성
    println("안전한 처리: $actualDog")
}

fun testNumericConversion() {
    val intValue: Int = 10

    // Int를 Long으로 변환(확장 변환: 데이터 손실 없음)
    val longValue: Long = intValue.toLong()
    println("Int (10) to Long: $longValue") // 출력: 10

    val doubleValue: Double = 12.78

    // Double을 Int로 변환(축소 변환: 소수점 이하 버림 발생)
    val intFromDouble: Int = doubleValue.toInt()
    println("Double (12.78) to Int: $intFromDouble")  // 출력: 12

    val charValue: Char = 'B'
    val intFromChar: Int = charValue.code  // Char의 ASCII/Unicode 값으로 변환
    println("Char ('B') to Int: $intFromChar")  // 출력: 66

    val stringNumber: String = "456"
    // 문자열 "456"을 Int로 변환(변환 실패 시 NumberFormatException 발생 가능성 있음)
    val numFromString: Int = stringNumber.toInt()
    println("String (\"456\") to Int: $numFromString") // 출력: 456

    val invalidString: String = "hello"
    // 문자열 "hello"를 Int로 변환 시도. 실패하면 null 반환(NumberFormatException 방지)
    val safeNumFromString: Int? = invalidString.toIntOrNull()
    println("String (\"hello\") to Int (안전하게): $safeNumFromString")  // 출력: null
}

fun main() {
    // testArithmeticOperators()
    // testIncrementAndDecrement()
    // testComparisonOperators()
    // testAssignmentOperators()
    // testLogicalOperators()
    // testBitOperators()
    // testRange()
    // testLet()
    // testRunWith()
    // testAccessOperators()
    // testTypeCastingOperator()
    // testTypeCastingOperator2()
    testNumericConversion()
}
