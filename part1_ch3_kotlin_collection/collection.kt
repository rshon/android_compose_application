// kotlinc collection.kt -include-runtime -d main.jar && java -jar main.jar

fun compareArraySpeed(arraySize: Int) {
    val objectArray = Array(arraySize) { index -> index }
    val intArray = IntArray(arraySize) { index -> index }

    // Performance measurement for IntArray
    val startInt = System.nanoTime()
    val intMapped = intArray.map { it * 2 }
    val endInt = System.nanoTime()
    val durationIntNs = endInt - startInt
    val durationIntMs = durationIntNs / 1_000_000.0 // Convert nanoseconds to milliseconds

    // Performance measurement for Array<Int>
    val startObj = System.nanoTime()
    val objMapped = objectArray.map { it as Int * 2 }
    val endObj = System.nanoTime()
    val durationObjNs = endObj - startObj
    val durationObjMs = durationObjNs / 1_000_000.0 // Convert nanoseconds to milliseconds

    println(intMapped.size)
    println(objMapped.size)

    println("IntArray operation time: ${String.format("%.3f", durationIntMs)} ms")
    println("Array<Int> operation time: ${String.format("%.3f", durationObjMs)} ms")
}

fun traverseArray() {
    val numbers = Array(5) { index -> index }

    for (number in numbers) {
        println("number = $number")
    }
}

fun arrayBasicMethods() {
    val numbers = arrayOf(1, 2, 3, 4, 5)
    println(numbers[0])  // 1
    numbers[1] = 10
    println(numbers[1])  // 10
    println(numbers.size)
    println(numbers.indices)
    println(2 in numbers)
}

fun arrayAdvancedCollectionMethods() {
    val numbers = arrayOf(1, 2, 3, 4, 5)
    println(numbers.joinToString())
    println(numbers.joinToString(separator = " "))
    println(numbers.joinToString(prefix = "Numbers: ", postfix = "."))

    println(numbers.map { it * 2 })
    println(numbers.filter { it % 2 == 0 })

    numbers.forEach { println(it) }
}

fun arrayAdvancedMethods() {
    val names = arrayOf("Richard", "Rick", "Kavin", "Atreides", "Harkonnen")

    println("names.contains = ${names.contains("Kavin")}")
    println("names.filter = ${names.filter { it.startsWith("R") }}")
    println("names.map = ${names.map { it.length }}")
    println("names.any = ${names.any { it.startsWith("R") }}")
    println("names.all = ${names.all { it.length > 3 }}")
    println("names.count = ${names.count { it.startsWith("R") }}")
    println("names.find = ${names.find { it.startsWith("R") }}")
}

fun testListMethods() {
    // Created with listOf
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    println(days[0])           // Output: Monday
    // days[0] = "Sun"         // Error: immutable

    // Method usage
    println(days.contains("Tuesday"))  // Output: true
    println(days.indexOf("Thursday"))  // Output: 3

    // Iteration
    days.forEach { day ->
        println("Day: $day")
    }

    // Filtering
    val filtered = days.filter { it.startsWith("T") }
    println(filtered) // Output: [Tuesday, Thursday]
}

fun testMutableList() {
    val days = mutableListOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday")

    days[0] = "Sun"
    println("days[0] = ${days[0]}")

    days.add("Saturday")
    println("days[5] = ${days[5]}")

    days.removeAt(0)
    println("days[0] = ${days[0]}")

    days.remove("Sunday")
    println("days[0] = ${days[0]}")

    days.sort()
    println("days = $days")

    days.reverse()
    println("days = $days")

    days.clear()
    println("days.size = ${days.size}")
}

fun testSet() {
    val numbers = setOf(1, 2, 2, 3)
    println(numbers)          // [1, 2, 3]

    val mutableNumbers = mutableSetOf(4, 5)
    mutableNumbers.add(6)
    println(mutableNumbers)   // [4, 5, 6]
}

fun testMap() {
    val scores = mapOf("Alice" to 95, "Bob" to 87)
    println(scores["Alice"])      // Output: 95

    val mutableScores = mutableMapOf("Charlie" to 90)
    mutableScores["Dave"] = 85    // Add
    println(mutableScores)        // Output: {Charlie=90, Dave=85}

    val emptyMap = emptyMap<String, Int>()

    val mutableMap = mutableMapOf("Kotlin" to 1, "Python" to 2)
    println(mutableMap.containsKey("Kotlin"))
    println(mutableMap.containsValue(1))

    mutableMap["Java"] = 3  // Preferred over put()
    println(mutableMap)

    mutableMap.remove("Python")
    println(mutableMap)

    mutableMap.clear()
    println(mutableMap)

    mutableMap.putAll(mapOf("Kotlin" to 1, "Python" to 2))
    println(mutableMap)

    mutableMap.forEach { (key, value) ->
        println("key: $key, value: $value")
    }

    val doubledValues = mutableMap.map { it.key to it.value * 2 }
    println(doubledValues)
    println(mutableMap)
}

fun testMapTypes() {
    val linkedHashMap1 = linkedMapOf<String, Int>()
    linkedHashMap1["one"] = 1
    linkedHashMap1["two"] = 2

    val linkedHashMap2: MutableMap<String, Int> = LinkedHashMap()
    linkedHashMap2["one"] = 1
    linkedHashMap2["two"] = 2
}

fun getLanguageSettings(languageType: String): Map<String, Int> {
    return if (languageType == "AI language") {
        emptyMap()
    } else {
        mapOf("Kotlin" to 1, "Python" to 2)
    }
}

fun compareEagerVsLazy() {
    println("=== Eager Evaluation ===")
    val numbers = listOf(1, 2, 3, 4, 5)
        .map { println("map: $it"); it * it }
        .filter { println("filter: $it"); it > 3 }
        .toList()

    println("\n=== Lazy Evaluation ===")
    val sequence = sequenceOf(1, 2, 3, 4, 5)
        .map { println("map: $it"); it * it }
        .filter { println("filter: $it"); it > 3 }

    val result = sequence.toList()
}

fun testHashSet() {
    val languages = hashSetOf("Kotlin")
    languages.add("Python")
    languages.addAll(listOf("Java", "Kotlin"))
    println(languages)                         // [Kotlin, Python, Java] (random order)

    languages.remove("Python")
    println(languages)                         // [Java, Kotlin]

    languages.removeAll(setOf("Kotlin", "Java"))
    println(languages)                         // []

    languages.clear()                          // Clear all
    println(languages)                         // Output: []
}

fun testLinkedHashSet() {
    val languages = linkedSetOf("Kotlin")
    languages.add("Python")
    languages.addAll(listOf("Java", "Kotlin"))
    println(languages)                         // [Kotlin, Python, Java] (insertion order maintained)

    languages.remove("Python")
    println(languages)                         // [Kotlin, Java]

    languages.removeAll(setOf("Kotlin", "Java"))
    println(languages)                         // []

    languages.clear()
    println(languages)                         // []
}

fun main() {
    // testHashSet()
    // testLinkedHashSet()

    testMutableList()

    // val squares = Array(5) { i -> i * i }
    // println(squares.joinToString())

    // val numbers = Array(5) { i -> i + 1 }
    // println(numbers.joinToString())

    // compareArraySpeed(1000)
    // compareArraySpeed(10000)
    // compareArraySpeed(100000)
    // compareArraySpeed(1000000)
    // compareArraySpeed(10000000)

    // traverseArray()

    // arrayBasicMethods()

    // arrayAdvancedCollectionMethods()

    // testListMethods()

    // testSet()

    // testMap()
    // testMapTypes()

    // compareEagerVsLazy()
}
