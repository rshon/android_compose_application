fun testImmutableVariables() {
    val message = "Hello, Kotlin!"  // message에 "Hello, Kotlin!" 할당
    println(message)  // 출력: Hello, Kotlin!

    // message = "Hello, World!"   // 컴파일 오류! val 변수는 재할당할 수 없다.

    val pi = 3.14159  // 원주율 pi는 변하지 않는 값
    println(pi)
}

fun testMutableVariables() {
    var count = 10  // count에 10 할당
    println(count)  // 출력: 10

    count = 20  // count에 새로운 값 20 재할당
    println(count)  // 출력: 20

    var name = "Foo"  // name에 "Foo" 할당
    println(name)  // 출력: Foo

    name = "Bar"  // name에 "Bar" 재할당
    println(name)  // 출력: Bar
}

val message: String by lazy {
    println("initializing...")
    "hello!"
}

fun testLazyInitialization() {
    println(message)  // 여기서 최초로 초기화
    println(message)  // 두 번째부터는 캐싱된 값 사용
}

class Example {
    lateinit var name: String

    fun initializeName() {
        name = "Foo"  // 나중에 초기화
    }

    fun printName() {
        if (::name.isInitialized) {  // 초기화 여부 확인
            println("Name: $name")
        } else {
            println("not initialized")
        }
    }
}

fun testLateInit() {
    val example = Example()
    example.printName()  // 출력: not initialized
    example.initializeName()
    example.printName()  // 출력: Name: Foo
}

var name: String? = null

fun initName() {
    name = "Foo"
}

fun printName() {
    if (name != null) {
        println("Name: $name")
    } else {
        println("not initialized")
    }
}

fun testNullableType() {
    printName()  // 출력: not initialized
    initName()
    printName()  // 출력: Name: Foo
}

fun main() {
    testNullableType()
}
