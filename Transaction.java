import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Transaction {
    private String transactionId;
    private int memberId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String type; // "Borrow" or "Return"
    private double fineAmount;

    private static final double FINE_PER_DAY = 5.0; // Fine rate per overdue day

    public Transaction(String transactionId, int memberId, int bookId, LocalDate borrowDate, int dueDays, String type) {
        this.transactionId = transactionId;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = borrowDate.plusDays(dueDays); // Set due date based on the number of due days
        this.type = type;
        this.fineAmount = 0.0;
    }

    // Method to return the book and calculate fine automatically
    public void returnBook(LocalDate returnDate) {
        this.returnDate = returnDate;
        this.type = "Return";
        calculateFine();
    }

    // Calculate the fine based on overdue days
    private void calculateFine() {
        if (returnDate != null && returnDate.isAfter(dueDate)) {
            long overdueDays = ChronoUnit.DAYS.between(dueDate, returnDate);
            fineAmount = overdueDays * FINE_PER_DAY;
        } else {
            fineAmount = 0.0;
        }
    }

    // Getters
    public double getFineAmount() {
        return fineAmount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public int getMemberId() {
        return memberId;
    }

    public int getBookId() {
        return bookId;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Transaction ID: " + transactionId +
                ", Member ID: " + memberId +
                ", Book ID: " + bookId +
                ", Borrow Date: " + borrowDate +
                ", Due Date: " + dueDate +
                ", Return Date: " + (returnDate != null ? returnDate : "Not returned yet") +
                ", Fine: $" + fineAmount;
    }
}
