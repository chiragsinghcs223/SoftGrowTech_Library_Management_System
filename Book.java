// ─────────────────────────────────────────────────────────────────────────────
// Book.java
// OOP Concept: ENCAPSULATION — all fields are private, accessed via getters/setters
// ─────────────────────────────────────────────────────────────────────────────

public class Book {

    // Private fields — direct access from outside the class is forbidden
    private String title;
    private String author;
    private int    publishYear;
    private String isbn;

    // ── Constructor ──────────────────────────────────────────────────────────
    public Book(String title, String author, int publishYear, String isbn) {
        this.title       = title.trim();
        this.author      = author.trim();
        this.publishYear = publishYear;
        this.isbn        = isbn.trim();
    }

    // ── Getters ──────────────────────────────────────────────────────────────
    public String getTitle()       { return title; }
    public String getAuthor()      { return author; }
    public int    getPublishYear() { return publishYear; }
    public String getIsbn()        { return isbn; }

    // ── Setters ──────────────────────────────────────────────────────────────
    public void setTitle(String title)             { this.title       = title.trim(); }
    public void setAuthor(String author)           { this.author      = author.trim(); }
    public void setPublishYear(int publishYear)    { this.publishYear = publishYear; }
    public void setIsbn(String isbn)               { this.isbn        = isbn.trim(); }

    // ── toString override — used when saving to file ──────────────────────────
    @Override
    public String toString() {
        return title + " by " + author + " (" + publishYear + ") [ISBN: " + isbn + "]";
    }
}
