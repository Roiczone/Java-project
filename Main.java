import java.time.LocalDate;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();
        Librarian librarian = new Librarian("John Doe", "L001");
        Member member = new Member("Alice", "M001");

        Book book1 = new Book("Java Programming", "James Gosling");
        Book book2 = new Book("Data Structures", "Robert Lafore");

        librarian.addBook(book1);
        librarian.addBook(book2);
        library.addBook(book1);
        library.addBook(book2);
        library.addMember(member);

        member.borrowBook(book1);

        Transaction transaction = new Transaction(member, book1, LocalDate.now(), LocalDate.now().plusDays(7));
        System.out.println(transaction);

        // Simulating a fine scenario
        double fineAmount = Fine.calculateFine(transaction);
        System.out.println("Fine Amount: $" + fineAmount);
    }
}
