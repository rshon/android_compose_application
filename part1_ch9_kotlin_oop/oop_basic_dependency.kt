
data class Book(val title: String, val author: String)
    
open class Student(
    name: String,
    major: String,
    grade: Int
) {
    val name: String = name
    val major: String = major
    var grade: Int = grade
    private val borrowed_books: MutableList<Book> = mutableListOf()

    fun borrow_book(library: Library, bookTitle: String) {
        println("Student $name is trying to borrow '$bookTitle' from the library.")
        val book = library.lend_book(this, bookTitle)
        book?.let {
            borrowed_books.add(it)
        }
    }

    fun return_book(library: Library, book: Book) {
        println("Student $name is returning '${book.title}'.")
        library.receive_returned_book(book)
        borrowed_books.remove(book)
    }

    fun list_borrowed_books() {
        if (borrowed_books.isEmpty()) {
            println("Student $name has not borrowed any books.")
        } else {
            println("Student $name has borrowed the following books:")
            for (book in borrowed_books) {
                println("- ${book.title}")
            }
        }
    }
}

class Library {
    private val available_books: MutableList<Book> = mutableListOf()
    private val borrowed_records: MutableMap<Book, Student> = mutableMapOf()

    init {
        available_books.add(Book("Algorithm inside with Python", "Son"))
        available_books.add(Book("Steve Jobs", "Walter Isaacson"))
    }

    fun lend_book(student: Student, bookTitle: String): Book? {
        val book_to_lend = available_books.find { it.title == bookTitle && !borrowed_records.containsKey(it) }
        if (book_to_lend != null) {
            borrowed_records[book_to_lend] = student
            println("Library: ${student.name} has borrowed '${book_to_lend.title}'.")
            return book_to_lend
        } else {
            println("Library: Cannot lend '$bookTitle'. It might be unavailable or already borrowed.")
            return null
        }
    }

    fun receive_returned_book(book: Book) {
        borrowed_records.remove(book)
        println("Library: '${book.title}' has been returned.")
    }
}

fun main() {
    val student = Student("Foo", "Computer Science", 1)
    val universityLibrary = Library()
    student.borrow_book(universityLibrary, "Algorithm inside with Python")
}
