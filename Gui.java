import java.time.LocalDate;
import java.util.Scanner;

public class Gui {
    private static Library library = new Library();
    private static Librarian librarian = new Librarian("John Doe", "L001");

    public static void main() {
        TransactionManager transactionManager = new TransactionManager();
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. delete Book");
            System.out.println("3. Add Member");
            System.out.println("4. Delete member");
            System.out.println("5. Borrow Book");
            System.out.println("6. Return Book");
            System.out.println("7. Show Books");
            System.out.println("8. Show Transactions");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    Book book = new Book(title, author);
                    librarian.addBook(book);
                    library.addBook(book);
                    Database.addBook(book.getId(), book.getTitle(), book.getAuthor());
                    break;

                case 2:
                    System.out.print("Enter book Name to delete: ");
                    String bookTitle = scanner.nextLine();

                    Book bookToDelete = library.findBookByTitle(bookTitle);

                    if (bookToDelete != null) {
                        librarian.removeBook(bookToDelete); // Remove from librarian's collection
                        library.removeBook(bookToDelete);   // Remove from library's collection
                        Database.deleteBook(bookTitle);       // Remove from database
                        System.out.println("Book deleted successfully.");
                    } else {
                        System.out.println("No book found with the given Name.");
                    }
                    break;

                case 3:
                    System.out.print("Enter member name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter member ID: ");
                    String memberId = scanner.nextLine();
                    Member member = new Member(name, memberId);
                    library.addMember(member);
                    Database.addMember(memberId, name);
                    break;

                case 4:
                    System.out.print("Enter member ID to delete: ");
                    String memberIdToDelete = scanner.nextLine();

                    // Get the member by ID from the library (assuming such a method exists)
                    Member memberToDelete = library.findMemberById(memberIdToDelete);

                    if (memberToDelete != null) {
                        librarian.removeMember(memberToDelete);   // Remove from library
                        Database.deleteMember(memberIdToDelete); // Remove from database
                        System.out.println("Member deleted successfully.");
                    } else {
                        System.out.println("No member found with the given ID.");
                    }
                    break;


                case 5:
                    System.out.print("Enter member ID: ");
                    String memId = scanner.nextLine();
                    Member mem = library.findMemberById(memId);
                    if (mem == null) {
                        System.out.println("Member not found!");
                        break;
                    }
                    System.out.print("Enter book title to borrow: ");
                    String borrowTitle = scanner.nextLine();
                    String bookId = scanner.nextLine();
                    Book borrowBook = library.findBookByTitle(borrowTitle);
                    System.out.print("Enter borrowing days: ");
                    int dueDays = scanner.nextInt();
                    scanner.nextLine();
                    if (borrowBook == null) {
                        System.out.println("Book not found!");
                    } else {
                        mem.borrowBook(borrowBook);
                        String transactionId = "TXN" + System.currentTimeMillis(); // Unique ID based on time
                        Transaction transaction = new Transaction(transactionId, memId, bookId, LocalDate.now(), dueDays, "Borrow");
                        TransactionManager.addTransaction(transaction);
                        System.out.println("Book borrowed successfully with Transaction ID: " + transactionId);
                    }
                    break;

                case 6:
                    System.out.print("Enter member ID: ");
                    String returnMemId = scanner.nextLine();
                    Member returnMem = library.findMemberById(returnMemId);
                    if (returnMem == null) {
                        System.out.println("Member not found!");
                        break;
                    }
                    System.out.print("Enter book title to return: ");
                    String returnTitle = scanner.nextLine();
                    Book returnBook = library.findBookByTitle(returnTitle);
                    if (returnBook == null) {
                        System.out.println("Book not found!");
                    } else {
//                        returnMem.returnBook(returnBook);
                        System.out.print("Enter return date (YYYY-MM-DD): ");
                        String dateInput = scanner.nextLine();
                        LocalDate returnDate = LocalDate.parse(dateInput);
                        TransactionManager.returnBook(returnTitle, returnDate);
                    }
                    break;

                case 7:
                    System.out.println("Available books:");
                    librarian.showBooks();
                    break;

                case 8:
                    System.out.println("All Transactions:");
                    for (Transaction t : TransactionManager.getAllTransactions()) {
                        System.out.println(t);
                    }
                    break;

                case 9:
                    System.out.println("Exiting system. Goodbye!");
                    break;


                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 9);

        scanner.close();
    }
}
