import java.time.LocalDate;

public class Transaction {
    private Member member;
    private Book book;
    private LocalDate borrowDate;
    LocalDate returnDate;

    public Transaction(Member member, Book book, LocalDate borrowDate, LocalDate returnDate) {
        this.member = member;
        this.book = book;
        this.borrowDate = borrowDate;
        this.returnDate = returnDate;
    }

    public boolean isOverdue() {
        return LocalDate.now().isAfter(returnDate);
    }

    @Override
    public String toString() {
        return member.getName() + " borrowed " + book.getTitle() + " on " + borrowDate +
                " (Return by: " + returnDate + ")";
    }
}
