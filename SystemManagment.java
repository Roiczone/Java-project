import java.time.LocalDate;
import java.util.Scanner;

public class SystemManagment {

    public static void main() {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. delete Book");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Add Member");
            System.out.println("6. Delete member");
            System.out.println("7. Show Books");
            System.out.println("8. Show All members");
            System.out.println("9. Show Transactions");
            System.out.println("10. Exit");
            System.out.print("Enter your choice: ");
            choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice) {
                case 1:     //Add Book to Database
                    System.out.print("Enter book title: ");
                    String title = scanner.nextLine();
                    System.out.print("Enter author: ");
                    String author = scanner.nextLine();
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    Book book = new Book(title, author, quantity);
                    Database.addBook(book.getTitle(), book.getAuthor(), book.getQuantity());
                    break;

                case 2:      //Delete Book from Database
                    System.out.print("Enter bookId to delete: ");
                    int bookId = scanner.nextInt();

                    Book bookToDelete = Database.findBookById(bookId);

                    if (bookToDelete != null) {
                        Database.deleteBook(bookId);
                    } else {
                        System.out.println("");
                    }
                    break;

                case 3:         //Borrow Book
                    System.out.print("Enter member ID: ");
                    int memId = scanner.nextInt();
                    Member mem = Database.findMemberById(memId);

                    if (mem == null) {
                        System.out.println("Member not found!");
                        break;
                    }

                    System.out.print("Enter book ID to borrow: ");
                    bookId = scanner.nextInt();
                    Book borrowBook = Database.findBookById(bookId);
                    scanner.nextLine();

                    if (borrowBook == null) {
                        System.out.println("Book not found!");
                        break;
                    }

                    if (borrowBook.getQuantity() <= 0) {
                        System.out.println("Book is out of stock!");
                        break;
                    }

                    System.out.print("Enter due date (YYYY-MM-DD): ");
                    String dateInput = scanner.nextLine();
                    try {
                        LocalDate borrowDate = LocalDate.now();
                        LocalDate dueDate = LocalDate.parse(dateInput);
                        boolean success = Database.borrowBook(memId, bookId, borrowDate, dueDate);

                        if (success) {
                            System.out.println("Book borrowed successfully! Due on: " + dueDate);
                        } else {
                            System.out.println("Borrowing failed. Please try again later.");
                        }

                    } catch (Exception e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                    break;


                case 4:         //Return Book
                    System.out.print("Enter transaction Id: ");
                    int transactionId = scanner.nextInt();
                    LocalDate transactionDate = Database.findDueDateByTransactionId(transactionId);
                    if (transactionDate == null) {
                        System.out.println("Transaction not found!");
                        break;
                    }
                    System.out.print("Enter member ID: ");
                    memId = scanner.nextInt();
                    mem = Database.findMemberById(memId);
                    if (mem == null) {
                        System.out.println("Member not found!");
                        break;
                    }
                    System.out.print("Enter book Id to return: ");
                    bookId = scanner.nextInt();
                    Book returnBookID = Database.findBookById(bookId);
                    scanner.nextLine();
                    if (returnBookID == null) {
                        System.out.println("Book not found!");
                        break;
                    }
                    try {
                        boolean success = Database.returnBook(memId, bookId, LocalDate.now());

                        if (success) {
                            Database.addBook(returnBookID.getTitle(), returnBookID.getAuthor(), returnBookID.getQuantity());
                            System.out.println("Book returned successfully!");
                            Database.addTransactionReturn(memId, bookId, LocalDate.now(), transactionDate, LocalDate.now());
                        } else {
                            System.out.println("Book return failed. Either the book was already returned or the transaction doesn't exist.");
                        }
                    } catch (Exception e) {
                        System.out.println("Invalid date format. Please use YYYY-MM-DD.");
                    }
                    break;

                case 5:        //Add Member to Database
                    System.out.print("Enter member name: ");
                    String name = scanner.nextLine();
                    Member member = new Member(name);
                    Database.addMember(member.getName());
                    break;

                case 6:          //Delete Member from Database
                    System.out.print("Enter member ID to delete: ");
                    int memberIdToDelete = scanner.nextInt();
                    Member memberToDelete = Database.findMemberById(memberIdToDelete);

                    if (memberToDelete != null) {
                        Database.removeMember(memberIdToDelete);
                    } else {
                        System.out.println("No member found with the given ID.");
                    }
                    break;




                case 7:         //Show Books
                    System.out.println("Available books:");
                    Database.showBooks();
                    break;

                case 8:         //Show Members
                    System.out.println("Show all Members");
                    Database.showMembers();
                    break;

                case 9:        //Show Transactions
                    System.out.println("All Transactions:");
                    Database.showTransactions();
                    break;

                case 10:
                    System.out.println("Exiting system. Goodbye!");
                    break;



                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 10);

        scanner.close();
    }
}
