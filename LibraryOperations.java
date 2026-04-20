// ─────────────────────────────────────────────────────────────────────────────
// LibraryOperations.java
// OOP Concept: ABSTRACTION — interface defines a contract without implementation
// ─────────────────────────────────────────────────────────────────────────────

import java.util.List;

public interface LibraryOperations {

    // Add a book to the library
    void addBook(Book book);

    // Delete a book by its exact title
    void deleteBookByTitle(String title) throws BookNotFoundException;

    // Search overloads — OOP Concept: POLYMORPHISM (method overloading)
    List<Book> searchByTitle(String title);
    List<Book> searchByAuthor(String author);
    List<Book> searchByPublishYear(int year);
}
