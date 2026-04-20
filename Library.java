// ─────────────────────────────────────────────────────────────────────────────
// Library.java
// OOP Concepts:
//   • Implements LibraryOperations interface (Abstraction)
//   • Encapsulates the book list (Encapsulation)
//   • Method overloading for search (Polymorphism)
//   • Input validation + custom exception (BookNotFoundException)
//   • File persistence: save / load from library_data.txt
// ─────────────────────────────────────────────────────────────────────────────

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class Library implements LibraryOperations {

    // Internal book store — private to enforce encapsulation
    private final List<Book> books = new ArrayList<>();

    // Tracks last-action metadata used by the stats panel
    private String lastAddedTitle   = "—";
    private String lastDeletedTitle = "—";
    private String lastSearchQuery  = "—";

    // ── File path for persistence ────────────────────────────────────────────
    private static final String DATA_FILE = "library_data.txt";

    // ════════════════════════════════════════════════════════════════════════
    //  CRUD OPERATIONS
    // ════════════════════════════════════════════════════════════════════════

    /** Add a validated Book to the library. */
    @Override
    public void addBook(Book book) {
        validateBook(book);           // throws IllegalArgumentException on bad input
        books.add(book);
        lastAddedTitle = book.getTitle();
    }

    /** Delete the first book whose title matches (case-insensitive). */
    @Override
    public void deleteBookByTitle(String title) throws BookNotFoundException {
        if (title == null || title.isBlank())
            throw new IllegalArgumentException("Title cannot be empty.");

        Book target = books.stream()
                           .filter(b -> b.getTitle().equalsIgnoreCase(title.trim()))
                           .findFirst()
                           .orElseThrow(() -> new BookNotFoundException(title.trim()));

        books.remove(target);
        lastDeletedTitle = target.getTitle();
    }

    // ════════════════════════════════════════════════════════════════════════
    //  SEARCH — overloaded methods (Polymorphism)
    // ════════════════════════════════════════════════════════════════════════

    @Override
    public List<Book> searchByTitle(String title) {
        lastSearchQuery = "Title: \"" + title + "\"";
        return books.stream()
                    .filter(b -> b.getTitle().toLowerCase()
                                  .contains(title.toLowerCase().trim()))
                    .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByAuthor(String author) {
        lastSearchQuery = "Author: \"" + author + "\"";
        return books.stream()
                    .filter(b -> b.getAuthor().toLowerCase()
                                  .contains(author.toLowerCase().trim()))
                    .collect(Collectors.toList());
    }

    @Override
    public List<Book> searchByPublishYear(int year) {
        lastSearchQuery = "Year: " + year;
        return books.stream()
                    .filter(b -> b.getPublishYear() == year)
                    .collect(Collectors.toList());
    }

    // ════════════════════════════════════════════════════════════════════════
    //  STATS HELPERS — all return computed values, no stored state
    // ════════════════════════════════════════════════════════════════════════

    public List<Book> getAllBooks()           { return Collections.unmodifiableList(books); }
    public int        getTotalBooks()         { return books.size(); }
    public String     getLastAddedTitle()     { return lastAddedTitle; }
    public String     getLastDeletedTitle()   { return lastDeletedTitle; }
    public String     getLastSearchQuery()    { return lastSearchQuery; }

    /** Number of distinct author names. */
    public long getUniqueAuthorCount() {
        return books.stream()
                    .map(b -> b.getAuthor().toLowerCase())
                    .distinct()
                    .count();
    }

    /** Book with the highest publish year. */
    public Optional<Book> getNewestBook() {
        return books.stream().max(Comparator.comparingInt(Book::getPublishYear));
    }

    /** Book with the lowest publish year. */
    public Optional<Book> getOldestBook() {
        return books.stream().min(Comparator.comparingInt(Book::getPublishYear));
    }

    /**
     * Publish year that appears most frequently.
     * Returns "—" if the library is empty.
     */
    public String getMostCommonYear() {
        return books.stream()
                    .collect(Collectors.groupingBy(Book::getPublishYear, Collectors.counting()))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(e -> String.valueOf(e.getKey()))
                    .orElse("—");
    }

    // ════════════════════════════════════════════════════════════════════════
    //  FILE PERSISTENCE
    // ════════════════════════════════════════════════════════════════════════

    /**
     * Save all books to library_data.txt.
     * Format: title|author|year|isbn  (one book per line)
     */
    public void saveToFile() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (Book b : books) {
                bw.write(b.getTitle()       + "|"
                       + b.getAuthor()      + "|"
                       + b.getPublishYear() + "|"
                       + b.getIsbn());
                bw.newLine();
            }
        } catch (IOException e) {
            System.err.println("Save failed: " + e.getMessage());
        }
    }

    /**
     * Load books from library_data.txt on startup.
     * Silently skips malformed lines.
     */
    public void loadFromFile() {
        File file = new File(DATA_FILE);
        if (!file.exists()) return;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split("\\|", 4);
                if (parts.length == 4) {
                    try {
                        int year = Integer.parseInt(parts[2].trim());
                        books.add(new Book(parts[0], parts[1], year, parts[3]));
                    } catch (NumberFormatException ignored) { /* skip bad lines */ }
                }
            }
            if (!books.isEmpty())
                lastAddedTitle = books.get(books.size() - 1).getTitle();
        } catch (IOException e) {
            System.err.println("Load failed: " + e.getMessage());
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    //  VALIDATION (private helper)
    // ════════════════════════════════════════════════════════════════════════

    private void validateBook(Book b) {
        if (b.getTitle().isBlank())
            throw new IllegalArgumentException("Title cannot be empty.");
        if (b.getAuthor().isBlank())
            throw new IllegalArgumentException("Author cannot be empty.");
        if (b.getIsbn().isBlank())
            throw new IllegalArgumentException("ISBN cannot be empty.");
        if (b.getPublishYear() <= 1000)
            throw new IllegalArgumentException("Publish year must be greater than 1000.");
    }
}
