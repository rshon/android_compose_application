fun hello(name: String): String = "Hello $name"

fun foo() = println("bar")

fun functionWithoutParameter() {
    println("no param")
}

fun functionWithParameter(name: String) {
    println("Hello $name")
}

fun functionWithDefaultParameter(name: String = "Eric") {
    println("Hello $name")
}

fun functionWithVarArgs(vararg names: String) {
    names.forEach { name ->
        println("Hello $name")
    }
}

fun functionWithNamedParameter(name: String, age: Int) {
    println("Name: $name, Age: $age")
}

fun applyOperation(x: Int, y: Int, operation: (Int, Int) -> Int): Int {
    return operation(x, y)
}

val add: (Int, Int) -> Int = { x, y -> x + y }

inline fun inlineFunction(crossinline body: () -> Unit) {
    body()
}

infix fun Int.addTo(n: Int): Int = this + n

tailrec fun factorial(n: Int, accumulator: Int = 1): Int {
    return if (n <= 1) accumulator else factorial(n - 1, n * accumulator)
}

fun add(a: Int, b: Int) = a + b
fun add(a: Double, b: Double) = a + b

fun <T> printItem(item: T) {
    println(item)
}

fun process(action: () -> Unit) {
    action()
}

fun main() {
    val message = hello("Eric")
    println(message)

    foo()

    functionWithoutParameter()

    functionWithParameter("Eric")

    functionWithDefaultParameter()
    functionWithDefaultParameter("Eric")

    functionWithVarArgs("Eric", "John", "Jane")

    functionWithVarArgs(*arrayOf("Eric", "John", "Jane"))

    functionWithNamedParameter(name = "Eric", age = 20)
    functionWithNamedParameter(age = 20, name = "Eric")

    val sum = applyOperation(3, 4) { a, b -> a + b }
    println(sum)

    println(add(1, 2))

    inlineFunction {
        println("Inline function")
    }

    println(5 addTo 3)

    println(factorial(5)) // Output: 120

    println(add(1, 2))      // Output: 3
    println(add(1.5, 2.5))  // Output: 4.0

    printItem("hello")
    printItem(77)

    process { println("Hello") }

    process { // Lambda expression outside parentheses
        println("Hello")
    }
}
