package objects;

import java.time.LocalDate;

public class Transaction {
    private int id;
    private int memberId;
    private int bookId;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private LocalDate returnDate;

    public Transaction(int id, int memberId, int bookId,LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        this.id = id;
        this.memberId = memberId;
        this.bookId = bookId;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
    }

    public int getId() {return id;}

    public int getMemberId() {return memberId;}

    public int getBookId() {return bookId;}

    public LocalDate getBorrowDate() {return borrowDate;}

    public LocalDate getDueDate() {return dueDate;}

    public LocalDate getReturnDate() {return returnDate;}

    @Override
    public String toString() {
        return "objects.Transaction{" + "id=" + id + ", memberId=" + memberId + ", bookId=" + bookId + ", borrowDate=" + borrowDate + ", dueDate=" + dueDate + ", returnDate=" + returnDate + '}';
    }
}
