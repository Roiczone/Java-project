import java.util.ArrayList;
import java.util.List;

public class Librarian {
    private String name;
    private String librarianId;
    private List<Book> books;
    private List<Member> members;

    public Librarian(String name, String librarianId) {
        this.name = name;
        this.librarianId = librarianId;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
    }

    public void addBook(Book book) {
        books.add(book);
        System.out.println("Book added: " + book.getTitle());
    }

    public void removeBook(Book book) {
        books.remove(book);
        System.out.println("Book removed: " + book.getTitle());
    }

    public void removeMember(Member member) {
        members.remove(member);
        System.out.println("Member removed: " + member.getName());
    }

    public void showBooks() {
        for (Book book : books) {
            System.out.println(book);
        }
    }
}
