// build & test
// kotlinc collectionTest.kt -include-runtime -d main.jar && java -jar main.jar

fun arrayBasicMethods() {
    val nums = arrayOf(1, 2, 3, 4, 5)

    println(nums[0])  // 1

    nums[1] = 10

    println(nums[1])  // 10
    println(nums.size)
    println(nums.indices)
    println(2 in nums)
}

fun arrayAdvancedCollectionMethods() {
    val nums = arrayOf(1, 2, 3, 4, 5)
    println(nums.joinToString())
    println(nums.joinToString(separator = " "))
    println(nums.joinToString(prefix = "Numbers: ", postfix = "."))
    println(nums.map { it * 2 })
    println(nums.filter { it % 2 == 0 })
    nums.forEach { println(it) }
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

class DataProcessor {
    // 데이터가 없을 때 빈 리스트 반환
    fun processData(input: List<String>?): List<String> {
        return input ?: emptyList()
    }
}

class Repository {
    // 초기 상태를 명확하게 표현
    private var cachedResults: List<String> = emptyList()

    fun updateResults(newResults: List<String>?) {
        cachedResults = newResults ?: emptyList()
    }
}

fun testListMethods() {
    // listOf로 생성
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    println(days[0])  // Monday 출력
    // days[0] = "Sun"  // 오류 발생

    // 메서드 사용
    println(days.contains("Tuesday"))  // true 출력
    println(days.indexOf("Thursday"))  // 3을 출력

    // 반복
    days.forEach { day ->
        println("Day: $day")
    }

    // 필터링
    val filtered = days.filter { it.startsWith("T") }
    println(filtered)  // [Tuesday, Thursday]를 출력
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

fun testMap() {
    val scores = mapOf("Alice" to 95, "Bob" to 87)
    println(scores["Alice"])  // 95

    val mutableScores = mutableMapOf("Charlie" to 90)
    mutableScores["Dave"] = 85  // 추가
    println(mutableScores)  // {Charlie=90, Dave=85}
}

fun getLanguageSettings(languageType: String): Map<String, Int> {
    return if (languageType == "AI language") emptyMap() else mapOf("Kotlin" to 1, "Python" to 2)
}

fun startTestMap() {
    val settings = getLanguageSettings("AI language")
    println(settings)  // {}
    println(settings["theme"])  // null
}

fun testMapLoop() {
    val map = mutableMapOf<String, Int>()
    map["one"] = 1
    map["two"] = 2
    for ((key, value) in map) {
        println("$key -> $value")
    }

    val linkedHashMap: MutableMap<String, Int> = linkedMapOf()
    linkedHashMap["first"] = 1
    linkedHashMap["second"] = 2
    for ((key, value) in linkedHashMap) {
        println("$key -> $value")
    }

    val linkedHashMap2: MutableMap<String, Int> = LinkedHashMap()
    linkedHashMap2["one"] = 1
    linkedHashMap2["two"] = 2

    val hashMap: MutableMap<String, Int> = HashMap()
    hashMap["first"] = 1
    hashMap["second"] = 2
    for ((key, value) in hashMap) {
        println("$key -> $value")
    }
}

fun testSet() {
    val numbers = setOf(1, 2, 2, 3)
    println(numbers)  // [1, 2, 3]

    val mutableNumbers = mutableSetOf(4, 5)
    mutableNumbers.add(6)
    println(mutableNumbers)  // [4, 5, 6]
}

fun testEmptySet() {
    val empty = emptySet<String>()

    val languages = hashSetOf("Kotlin")
    languages.add("Python")
    languages.addAll(listOf("Java", "Kotlin"))
    println(languages)  // [Kotlin, Python, Java](순서 무작위)

    languages.remove("Python")
    println(languages)  // [Java, Kotlin]

    languages.removeAll(setOf("Kotlin", "Java"))
    println(languages)  // []

    languages.clear()  // 전체 삭제
    println(languages)  // 출력: []
}

fun testLinkedHashSet() {
    val languages = linkedSetOf("Kotlin")
    languages.add("Python")
    languages.addAll(listOf("Java", "Kotlin"))
    println(languages)  // [Kotlin, Python, Java](삽입 순서 유지)

    languages.remove("Python")
    println(languages)  // [Kotlin, Java]

    languages.removeAll(setOf("Kotlin", "Java"))
    println(languages)  // []

    languages.clear()
    println(languages)  // []
}

fun testSequence() {
    val numbers = listOf(1, 2, 3, 4, 5)
        .map { println("map: $it"); it * it }
        .filter { println("filter: $it"); it > 3 }
        .toList()

    val sequence = sequenceOf(1, 2, 3, 4, 5)
        .map { println("map: $it"); it * it }
        .filter { println("filter: $it"); it > 3 }

    val result = sequence.toList()
}

fun main() {
    // arrayBasicMethods()
    // testListMethods()
    // testMutableList()
    // startTestMap()
    // testMapLoop()
    testSequence()
}
