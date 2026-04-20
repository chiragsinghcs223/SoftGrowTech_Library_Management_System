// ─────────────────────────────────────────────────────────────────────────────
// BookNotFoundException.java
// OOP Concept: Custom Exception — meaningful error message for missing books
// ─────────────────────────────────────────────────────────────────────────────

public class BookNotFoundException extends Exception {

    // Store the query that failed so callers can display it
    private final String query;

    public BookNotFoundException(String query) {
        super("No book found matching: \"" + query + "\"");
        this.query = query;
    }

    public String getQuery() { return query; }
}
