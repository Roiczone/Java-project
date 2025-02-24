import java.util.UUID;

public class Book {
    private String id;
    private String title;
    private String author;
    private boolean isAvailable;

    public Book(String title, String author) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.isAvailable = true;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }

    public void borrowBook() { isAvailable = false; }
    public void returnBook() { isAvailable = true; }

    @Override
    public String toString() {
        return "Book: " + title + " by " + author + " (Available: " + isAvailable + ")";
    }

}
