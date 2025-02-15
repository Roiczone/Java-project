import java.util.Scanner;

public class Gui {
    private static Library library = new Library();
    private static Librarian librarian = new Librarian("John Doe", "L001");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int choice;

        do {
            System.out.println("\n===== Library Management System =====");
            System.out.println("1. Add Book");
            System.out.println("2. Add Member");
            System.out.println("3. Borrow Book");
            System.out.println("4. Return Book");
            System.out.println("5. Show Books");
            System.out.println("6. Exit");
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
                    System.out.print("Enter member name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter member ID: ");
                    String memberId = scanner.nextLine();
                    Member member = new Member(name, memberId);
                    library.addMember(member);
                    Database.addMember(memberId, name);
                    break;

                case 3:
                    System.out.print("Enter member ID: ");
                    String memId = scanner.nextLine();
                    Member mem = library.findMemberById(memId);
                    if (mem == null) {
                        System.out.println("Member not found!");
                        break;
                    }
                    System.out.print("Enter book title to borrow: ");
                    String borrowTitle = scanner.nextLine();
                    Book borrowBook = library.findBookByTitle(borrowTitle);
                    if (borrowBook == null) {
                        System.out.println("Book not found!");
                    } else {
                        mem.borrowBook(borrowBook);
                    }
                    break;

                case 4:
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
                        returnMem.returnBook(returnBook);
                    }
                    break;

                case 5:
                    System.out.println("Available books:");
                    librarian.showBooks();
                    break;

                case 6:
                    System.out.println("Exiting system. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid choice, please try again.");
            }
        } while (choice != 6);

        scanner.close();
    }
}
