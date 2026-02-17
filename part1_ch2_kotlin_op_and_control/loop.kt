

fun testForLoop() {
   println("iterates from 1 to 5")
   for (i in 1..5) {
       print("${i} ")
   }

   println("iterates from 5 to 1 with step -2")
   for (i in 5 downTo 1 step 2) {
       print("${i} ")
   }

   println("iterates from  1to 4")
   for (i in 1 until 5) {
       print("${i} ")
   }
}

fun testForLoopWithCollection() {
   println("iterates items")
   val grades = listOf("A", "B", "C")
   for (fruit in grades) {
       print("${fruit} ")
   }

   println("iterates with index")
   for ((index, fruit) in grades.withIndex()) {
       print("$index:$fruit ")
   }

   println("iterates items in map")
   val scores = mapOf("Foo" to 95, "Bar" to 87)
   for ((name, score) in scores) {
       print("$name:$score ")
   }
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
