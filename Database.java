import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String URL = "jdbc:sqlite:C:\\Users\\roicz\\OneDrive\\Desktop\\Meine Kurse\\Software engineering\\Java-project\\database\\library.db";


    public static void connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.out.println("SQLite JDBC Driver not found.");
        }
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public static void createTables() {
        String booksTable = "CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, isAvailable INTEGER);";
        String membersTable = "CREATE TABLE IF NOT EXISTS members (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT);";
        String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, memberId TEXT, bookId TEXT, borrowDate TEXT, dueDate TEXT, returnDate TEXT, FOREIGN KEY(memberId) REFERENCES members(id), FOREIGN KEY(bookId) REFERENCES books(id));";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(booksTable);
            stmt.execute(membersTable);
            stmt.execute(transactionsTable);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            System.out.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void addBook(String title, String author) {
        String sql = "INSERT INTO books(title, author, isAvailable) VALUES(?, ?, 1)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Request generated keys

            pstmt.setString(1, title);
            pstmt.setString(2, author);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Book added with ID: " + generatedId);
                    } else {
                        System.out.println("Failed to retrieve generated ID.");
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public static Book findBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id); // Set the ID parameter

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Retrieve book details from the result set
                    String title = rs.getString("title");
                    String author = rs.getString("author");

                    // Create and return a Book object
                    return new Book(title, author);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding book by ID: " + e.getMessage());
        }

        return null; // Book not found
    }

    public static void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Book deleted successfully.");
            } else {
                System.out.println("No book found with the given ID.");
            }
        } catch (SQLException e) {
            System.out.println("Error deleting book: " + e.getMessage());
        }
    }


    public static boolean returnBook(int memberId, int bookId, LocalDate returnDate) {
        String sql = "UPDATE transactions SET returnDate = ? WHERE memberId = ? AND bookId = ? AND returnDate IS NULL";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, returnDate.toString());
            pstmt.setInt(2, memberId);
            pstmt.setInt(3, bookId);

            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
            return false;
        }
    }


    private static void calculateFine(LocalDate borrowDate, LocalDate returnDate) {
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(borrowDate.plusDays(14), returnDate); // Assuming 14-day borrow period
        if (daysLate > 0) {
            double fine = daysLate * 1.5; // Example fine rate: $1.5 per late day
            System.out.println("Book returned late. Fine due: $" + fine);
        } else {
            System.out.println("Book returned on time. No fine due.");
        }
    }

    public static void addMember(String name) {
        String sql = "INSERT INTO members(name) VALUES(?)"; // ID will be auto-generated

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name); // Set member name

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Member added with ID: " + generatedId);
                    }
                }
            } else {
                System.out.println("Failed to add member.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    public static Member findMemberById(int memberId) {
        String sql = "SELECT * FROM members WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId); // Set the member ID parameter
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Create and return a Member object from the retrieved data
                String name = rs.getString("name");
                return new Member(name);
            }

        } catch (SQLException e) {
            System.out.println("Error finding member: " + e.getMessage());
        }

        return null; // Return null if no member found
    }

    // DELETE a Member by ID
    // DELETE a Member by ID
    public static void removeMember(int memberId) {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId); // Set the member ID to delete
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Member removed successfully with ID: " + memberId);
            } else {
                System.out.println("No member found with ID: " + memberId);
            }
        } catch (SQLException e) {
            System.out.println("Error removing member: " + e.getMessage());
        }
    }


    public static void showBooks() {
        String sql = "SELECT * FROM books";
        System.out.println(sql);
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id")+ ". " + rs.getString("title") + " by " + rs.getString("author") + " (Available: " + rs.getInt("isAvailable") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
    }

    public static void addTransactionBorrow(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        String sql = "INSERT INTO transactions(memberId, bookId, borrowDate, dueDate) VALUES(?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) { // Request generated keys

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.setString(3, borrowDate.toString());

            // Set dueDate or NULL if it's not provided
            if (dueDate != null) {
                pstmt.setString(4, dueDate.toString());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                // Retrieve the auto-generated transaction ID
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int generatedId = generatedKeys.getInt(1);
                        System.out.println("Transaction added with ID: " + generatedId);
                    } else {
                        System.out.println("Failed to retrieve generated transaction ID.");
                    }
                }
            } else {
                System.out.println("Failed to add the transaction.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    public static void addTransactionReturn(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        String sql = "INSERT INTO transactions(memberId, bookId, borrowDate, returnDate, dueDate) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.setString(3, borrowDate.toString());

            // If returnDate is null, set it as null in the database
            if (returnDate != null) {
                pstmt.setString(4, returnDate.toString());
            } else {
                pstmt.setNull(4, Types.VARCHAR);}
            if (dueDate.toString() != null) {
                pstmt.setString(5, dueDate.toString());
            } else {
                pstmt.setNull(5, Types.VARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Transaction added successfully.");
            } else {
                System.out.println("Failed to add the transaction.");
            }

        } catch (SQLException e) {
            System.out.println("Error adding transaction: " + e.getMessage());
        }
    }

    public static LocalDate findDueDateByTransactionId(int memberId) {
        String sql = "SELECT dueDate FROM transactions WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId); // Set the transaction ID parameter
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retrieve and return the dueDate
                String dueDateStr = rs.getString("dueDate");
                if (dueDateStr != null) {
                    return LocalDate.parse(dueDateStr); // Parse and return the due date as LocalDate
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding due date: " + e.getMessage());
        }

        return null; // Return null if no due date or transaction found
    }



    public static void showTransactions() {
        String sql = "SELECT * FROM transactions";
        System.out.println(sql);

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String memberId = rs.getString("memberId");
                String bookId = rs.getString("bookId");
                String borrowDate = rs.getString("borrowDate");
                String dueDate = rs.getString("dueDate");
                String returnDate = rs.getString("returnDate");

                // Display transaction details
                System.out.println(
                        id + ". Member ID: " + memberId +
                                ", Book ID: " + bookId +
                                ", Borrowed: " + borrowDate +
                                ", Due: " + (dueDate != null ? dueDate : "N/A") +
                                ", Returned: " + (returnDate != null ? returnDate : "Not returned")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving transactions: " + e.getMessage());
        }
    }

    public static void showMembers() {
        String sql = "SELECT * FROM members";
        System.out.println(sql);

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");

                // Display member details
                System.out.println(id + ". " + name);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving members: " + e.getMessage());
        }
    }


    public static void main(String[] args) {
        connect();
        createTables();
        addBook("Java Programming", "James Gosling");
        //addMember("Alice");
        //deleteMember("1");
        //showBooks();
        showTransactions();

    }

}