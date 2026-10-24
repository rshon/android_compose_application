data class Book(val title: String, val author: String)

open class Student(
    name: String,
    major: String,
    grade: Int
) {
    val name: String = name
    val major: String = major
    var grade: Int = grade
    private val borrowedBooks: MutableList<Book> = mutableListOf()

    fun borrowBook(library: Library, bookTitle: String) {
        println("Student $name is trying to borrow '$bookTitle' from the library.")
        val book = library.lendBook(this, bookTitle)
        book?.let {
            borrowedBooks.add(it)
        }
    }

    fun returnBook(library: Library, book: Book) {
        println("Student $name is returning '${book.title}'.")
        library.receiveReturnedBook(book)
        borrowedBooks.remove(book)
    }

    fun listBorrowedBooks() {
        if (borrowedBooks.isEmpty()) {
            println("Student $name has not borrowed any books.")
        } else {
            println("Student $name has borrowed the following books:")
            for (book in borrowedBooks) {
                println("- ${book.title}")
            }
        }
    }
}

class Library {
    private val availableBooks: MutableList<Book> = mutableListOf()
    private val borrowedRecords: MutableMap<Book, Student> = mutableMapOf()

    init {
        availableBooks.add(Book("Algorithm inside with Python", "Son"))
        availableBooks.add(Book("Steve Jobs", "Walter Isaacson"))
    }

    fun lendBook(student: Student, bookTitle: String): Book? {
        val bookToLend = availableBooks.find { it.title == bookTitle && !borrowedRecords.containsKey(it) }
        if (bookToLend != null) {
            borrowedRecords[bookToLend] = student
            println("Library: ${student.name} has borrowed '${bookToLend.title}'.")
            return bookToLend
        } else {
            println("Library: Cannot lend '$bookTitle'. It might be unavailable or already borrowed.")
            return null
        }
    }

    fun receiveReturnedBook(book: Book) {
        borrowedRecords.remove(book)
        println("Library: '${book.title}' has been returned.")
    }
}

fun main() {
    val student = Student("Foo", "Computer Science", 1)
    val universityLibrary = Library()
    student.borrowBook(universityLibrary, "Algorithm inside with Python")
}
