import java.sql.*;
import java.time.LocalDate;

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
        String booksTable = "CREATE TABLE IF NOT EXISTS books (id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT, author TEXT, quantity Integer, isAvailable INTEGER);";
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

    public static void addBook(String title, String author, int quantity) {

        String checkBookSql = "SELECT quantity FROM books WHERE title = ? AND author = ?";
        String addBookSql = "INSERT INTO books(title, author, quantity, isAvailable) VALUES(?, ?, ?, 1)";
        String updateQuantitySql = "UPDATE books SET quantity = quantity + ? WHERE title = ? AND author = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtCheck = conn.prepareStatement(checkBookSql);
             PreparedStatement pstmtAdd = conn.prepareStatement(addBookSql);
             PreparedStatement pstmtUpdate = conn.prepareStatement(updateQuantitySql)) {

            pstmtCheck.setString(1, title);
            pstmtCheck.setString(2, author);
            ResultSet rs = pstmtCheck.executeQuery();

            if (rs.next()) {
                pstmtUpdate.setInt(1, quantity);
                pstmtUpdate.setString(2, title);
                pstmtUpdate.setString(3, author);
                pstmtUpdate.executeUpdate();
                System.out.println("Book quantity updated successfully.");
            } else {
                pstmtAdd.setString(1, title);
                pstmtAdd.setString(2, author);
                pstmtAdd.setInt(3, quantity);
                pstmtAdd.executeUpdate();
                System.out.println("Book added successfully with quantity: " + quantity);
            }

        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public static Book findBookById(int id) {
        String sql = "SELECT * FROM books WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    String title = rs.getString("title");
                    String author = rs.getString("author");
                    int quantity = rs.getInt("quantity");

                    return new Book(title, author, quantity );
                }
            }
        } catch (SQLException e) {
            System.out.println("Error finding book by ID: " + e.getMessage());
        }

        return null;
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

    public static boolean borrowBook(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        String insertTransactionSql = "INSERT INTO transactions (memberId, bookId, borrowDate, dueDate) VALUES (?, ?, ?, ?)";
        String updateBookSql = "UPDATE books SET quantity = quantity - 1 WHERE id = ? AND quantity > 0"; // Decrease quantity

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtTransaction = conn.prepareStatement(insertTransactionSql);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(updateBookSql)) {

            pstmtTransaction.setInt(1, memberId);
            pstmtTransaction.setInt(2, bookId);
            pstmtTransaction.setString(3, borrowDate.toString());
            pstmtTransaction.setString(4, dueDate.toString());

            int transactionInserted = pstmtTransaction.executeUpdate();

            if (transactionInserted > 0) {
                pstmtUpdateBook.setInt(1, bookId);
                int bookUpdated = pstmtUpdateBook.executeUpdate();

                if (bookUpdated > 0) {
                    return true;
                } else {
                    System.out.println("Book is not available or out of stock.");
                    return false;
                }
            } else {
                System.out.println("Failed to record borrowing transaction.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
            return false;
        }
    }

    public static boolean returnBook(int memberId, int bookId, LocalDate returnDate) {
        String updateTransactionSql = "UPDATE transactions SET returnDate = ? WHERE memberId = ? AND bookId = ? AND returnDate IS NULL";
        String updateBookSql = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";
        String getBorrowDateSql = "SELECT borrowDate, dueDate FROM transactions WHERE memberId = ? AND bookId = ? AND returnDate IS NULL";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtTransaction = conn.prepareStatement(updateTransactionSql);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(updateBookSql);
             PreparedStatement pstmtGetBorrowDate = conn.prepareStatement(getBorrowDateSql)) {

            pstmtGetBorrowDate.setInt(1, memberId);
            pstmtGetBorrowDate.setInt(2, bookId);
            ResultSet rs = pstmtGetBorrowDate.executeQuery();

            if (rs.next()) {
                LocalDate borrowDate = LocalDate.parse(rs.getString("borrowDate"));
                LocalDate dueDate = rs.getString("dueDate") != null ? LocalDate.parse(rs.getString("dueDate")) : borrowDate.plusDays(14);

                pstmtTransaction.setString(1, returnDate.toString());
                pstmtTransaction.setInt(2, memberId);
                pstmtTransaction.setInt(3, bookId);

                int affectedRows = pstmtTransaction.executeUpdate();

                if (affectedRows > 0) {
                    pstmtUpdateBook.setInt(1, bookId);
                    pstmtUpdateBook.executeUpdate();

                    System.out.println("Book returned successfully.");


                    calculateFine(borrowDate, returnDate);

                    return true;
                } else {
                    System.out.println("Error: Either the book was already returned or the transaction doesn't exist.");
                    return false;
                }
            } else {
                System.out.println("No matching borrow transaction found.");
                return false;
            }

        } catch (SQLException e) {
            System.out.println("Error returning book: " + e.getMessage());
            return false;
        }
    }



    private static void calculateFine(LocalDate borrowDate, LocalDate returnDate) {
        long daysLate = java.time.temporal.ChronoUnit.DAYS.between(borrowDate.plusDays(14), returnDate); // Assuming 14-day borrow period
        if (daysLate > 0) {
            double fine = daysLate * 1.5;
            System.out.println("Book returned late. Fine due: $" + fine);
        } else {
            System.out.println("Book returned on time. No fine due.");
        }
    }

    public static void addMember(String name) {
        String sql = "INSERT INTO members(name) VALUES(?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, name);

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
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

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                return new Member(name);
            }

        } catch (SQLException e) {
            System.out.println("Error finding member: " + e.getMessage());
        }

        return null;
    }

    public static void removeMember(int memberId) {
        String sql = "DELETE FROM members WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
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


    public static void addTransactionBorrow(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        String sql = "INSERT INTO transactions(memberId, bookId, borrowDate, dueDate) VALUES(?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.setString(3, borrowDate.toString());

            if (dueDate != null) {
                pstmt.setString(4, dueDate.toString());
            } else {
                pstmt.setNull(4, Types.VARCHAR);
            }

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
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

    public static LocalDate findDueDateByTransactionId(int memberId) {
        String sql = "SELECT dueDate FROM transactions WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                // Retrieve and return the dueDate
                String dueDateStr = rs.getString("dueDate");
                if (dueDateStr != null) {
                    return LocalDate.parse(dueDateStr);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error finding due date: " + e.getMessage());
        }

        return null;
    }

    public static void addTransactionReturn(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate, LocalDate returnDate) {
        String sql = "INSERT INTO transactions(memberId, bookId, borrowDate, returnDate, dueDate) VALUES(?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.setString(3, borrowDate.toString());

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


    public static void showBooks() {
        String sql = "SELECT * FROM books";
        System.out.println(sql);

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getInt("id")+ ". " + rs.getString("title") + " by " + rs.getString("author") + " (Quantity: " + rs.getInt("quantity") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
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

                System.out.println(id + ". " + name);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving members: " + e.getMessage());
        }
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


    public static void main(String[] args) {
        connect();
        createTables();

    }

}