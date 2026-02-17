// build & test
// kotlinc kotlin/collection/collection.kt -include-runtime -d main.jar && java -jar main.jar

fun testConditionalStatements() {
    val a = 1
    val b = 2

    if (a > b) {
        println("a is greater than b")
    } else {
        println("a is not greater than b")
    }
}

fun testConditionalExpression() {
    val num = 1
    val result = if (num > 0) "positive" else "negative or zero"
    println(result)
}

fun testMultipleConditions() {
    val score = 95

    val grade = if (score >= 90) "A"
    else if (score >= 80) "B"
    else if (score >= 70) "C"
    else "D"

    println("grade: ${grade}")
}

fun testWhen() {
    val score = 95

    when {
        score >= 90 -> println("A")
        score >= 80 -> println("B")
        score >= 70 -> println("C")
        score >= 60 -> println("D")
        else -> println("F")
    }

    val grade = when (score) {
        in 90..100 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        in 60..69 -> "D"
        else -> "F"
    }
    println("grade : $grade")
}

fun testWhenWithMultipleTypes() {
    val value: Any = "A"

    when (value) { // 주의: value를 when의 인자로 넣어야 아래 조건들이 올바르게 작동합니다.
        is String -> println("It's a string")
        in 1..10 -> println("It's in range")
        10.0 -> println("It's Kotlin") // value == 10.0 과 같은 의미
        else -> println("Unknown")
    }
}

fun testForLoop() {
    println("iterates from 1 to 5")
    for (i in 1..5) {
        print("${i} ")
    }
    println("")

    println("iterates from 5 to 1 with step -2")
    for (i in 5 downTo 1 step 2) {
        print("${i} ")
    }
    println("")

    println("iterates from 1 to 4")
    for (i in 1 until 5) {
        print("${i} ")
    }
    println("")

    // break 예제
    for (i in 1..5) {
        if (i == 3)
            break

        print("$i ")
    }
    println("")

    // continue 예제
    for (i in 1..5) {
        if (i == 3)
            continue

        print("$i ")
    }
    println("")

    // label(레이블)을 이용한 break
    outer@ for (i in 1..3) {
        for (j in 1..3) {
            if (j == 2)
                break@outer
            print("($i, $j) ")
        }
    }
}

fun testForLoopWithCollection() {
    println("iterates items")
    val grades = listOf("A", "B", "C")
    for (grade in grades) { // 변수명을 fruit -> grade 로 문맥에 맞게 수정했습니다.
        print("${grade} ")
    }
    println("")

    println("iterates with index")
    for ((index, grade) in grades.withIndex()) {
        print("$index:$grade ")
    }
    println("")

    println("iterates items in map")
    val scores = mapOf("Foo" to 95, "Bar" to 87)
    for ((name, score) in scores) {
        print("$name:$score ")
    }
    println("")
}

fun testWhile() {
    var num = 0

    while (num < 3) {
        println("num is $num")
        num++
    }
}

fun testDoWhile() {
    var num = 3

    do {
        println("Num is $num")
        num--
    } while (num > 0)
}

fun main() {
    // testConditionalStatements()
    // testConditionalExpression()
    // testMultipleConditions()
    // testWhen()
    // testWhenWithMultipleTypes()

    testForLoop()
    // testForLoopWithCollection()
    // testWhile()
    // testDoWhile()
}
