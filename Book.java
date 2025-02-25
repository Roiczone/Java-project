import java.util.UUID;

public class Book {
    private String id;
    private String title;
    private String author;
    private boolean isAvailable;
    private int quantity;

    public Book(String title, String author, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.author = author;
        this.isAvailable = true;
        this.quantity = quantity;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public boolean isAvailable() { return isAvailable; }
    public int getQuantity() { return quantity; }

    public void borrowBook() { isAvailable = false; }
    public void returnBook() { isAvailable = true; }

    @Override
    public String toString() {
        return "Book: " + title + " by " + author + " (Available: " + isAvailable + ")";
    }

}
