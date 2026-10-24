interface Bird {
    fun fly()
}

class Duck : Bird {
    override fun fly() {
        println("오리가 힘차게 날아갑니다!")
    }
}

class Sparrow : Bird {
    override fun fly() {
        println("참새가 날아갑니다!")
    }
}

class Penguin : Bird {
    override fun fly() {
        // 방법 1: 예외 발생 -> LSP 위반
        // throw UnsupportedOperationException("펭귄은 날 수 없어요!")

        // 방법 2: 아무것도 안 함 -> 역시 LSP 위반
        // println("펭귄은 날지 못합니다...") // 혹은 아무 동작도 안 함
    }
    // ...
}

fun makeBirdFly(bird: Bird) {
    println("새를 날려보자!")
    bird.fly() // Bird 타입의 객체는 당연히 fly()가 가능할 것으로 기대
}

fun main() {
    val sparrow = Sparrow()
    val penguin = Penguin()

    makeBirdFly(sparrow) // OK

    // makeBirdFly(penguin) // 행위 위반! (LSP 위반)
    // 예외가 발생하거나, 아무 일도 없거나, 예상과 다른 행동을 할 수 있음.
}
