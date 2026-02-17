
fun declareNumericTypes() {
    val byteValue: Byte = 10
    val shortValue: Short = 1000
    val intValue: Int = 100000
    val longValue: Long = 10000000000L

    val floatValue: Float = 3.14f
    val doubleValue: Double = 3.141592
}

fun testNumericTypes() {
    val original: Int = 65

    val b: Byte = original.toByte()
    val s: Short = original.toShort()
    val i: Int = original.toInt()
    val l: Long = original.toLong()
    val f: Float = original.toFloat()
    val d: Double = original.toDouble()
    val c: Char = original.toChar()

    println("$original")  // 65
    println("Byte: $b")  // 65
    println("Short: $s")  // 65
    println("Long: $l")  // 65
    println("Float: $f")  // 65.0
    println("Double: $d")  // 65.0
    println("Char: $c")  // A
}

fun testCharacterType() {
    val charA: Char = 'A'
    val newline: Char = '\n'
    val unicodeChar: Char = '\uD55C' // '한'

    println("charA: $charA")
    print(newline)
    println("unicodeChar: $unicodeChar")
}

fun testBooleanType() {
    val isLoggedIn = true

    if (isLoggedIn) {
        println("환영합니다!")
    } else {
        println("로그인이 필요합니다.")
    }
}

fun testStringType1() {

    val name = "Kotlin"
    println("안녕하세요, $name!")   // 출력: 안녕하세요, Kotlin!

    val num1 = 10
    val num2 = 20
    println("두 숫자의 합은 ${num1 + num2}입니다.")  // 출력: 두 숫자의 합은 30입니다.
}

fun testStringType2() {
    val name: String = "Kotlin"
    val greeting: String = "Hello, $name!"  // 문자열 템플릿
    println(greeting)  // Hello, Kotlin!

    val price = 100
    val tax = 10
    val total = "Total price: ${price + tax}"  // 표현식 사용
    println(total)  // Total price: 110

    val multilineString = """
        이것은 여러 줄의 문자열입니다.
        이스케이프 시퀀스 없이 작성할 수 있습니다.
        들여쓰기도 유지됩니다.
        """.trimIndent()  // 들여쓰기 제거
    println(multilineString)
}

fun main() {
    testNumericTypes()
}
