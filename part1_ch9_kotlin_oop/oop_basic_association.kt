open class Student(
    val name: String,
    var major: String,
    var grade: Int
) {
    val booksInRead: MutableList<Book> = mutableListOf()

    fun readBook(book: Book) {
        println("$name is reading '${book.title}'.")
        booksInRead.add(book)
    }

    fun listReadingBooks() {
        if (booksInRead.isEmpty()) {
            println("$name is not reading any books.")
        } else {
            println("$name is currently reading:")
            for (book in booksInRead) {
                println("- ${book.title} by ${book.author}")
            }
        }
    }
}

class Book(val title: String, val author: String) {
    fun printBookInfo() {
        println("Title: $title")
        println("Author: $author")
    }
}

fun main() {
    val student1 = Student("Foo", "Computer Science", 1)

    val book1 = Book("Algorithm inside with Python", "Son")
    val book2 = Book("Steve Jobs", "Walter Isaacson")
    book1.printBookInfo()
    book2.printBookInfo()
    println()

    student1.readBook(book1)
    student1.readBook(book2)
    student1.listReadingBooks()
    println()

    val student2 = Student("Bob", "Mathematics", 2)
    val book3 = Book("Calculus", "James Stewart")
    student2.readBook(book3)
    student2.listReadingBooks()
}
