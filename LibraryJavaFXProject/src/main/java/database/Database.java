package database;

import objects.Book;
import objects.Member;
import objects.Transaction;
import output.Output;

import java.sql.*;
import java.time.LocalDate;

public class Database {
    private static final String URL = "jdbc:sqlite:library.db";

    public static void connect() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                Output.println("Connected to SQLite.");
            }
        } catch (SQLException e) {
            Output.println("Connection failed: " + e.getMessage());
        }
    }

    public static void createTables() {
        String booksTable = "CREATE TABLE IF NOT EXISTS books (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "title TEXT, " +
                "author TEXT, " +
                "quantity INTEGER);";

        String membersTable = "CREATE TABLE IF NOT EXISTS members (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT, " +
                "age INTEGER);";

        String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "memberId INTEGER, " +
                "bookId INTEGER, " +
                "borrowDate TEXT, " +
                "dueDate TEXT, " +
                "returnDate TEXT, " +
                "FOREIGN KEY(memberId) REFERENCES members(id), " +
                "FOREIGN KEY(bookId) REFERENCES books(id));";

        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            stmt.execute(booksTable);
            stmt.execute(membersTable);
            stmt.execute(transactionsTable);
        } catch (SQLException e) {
            Output.println("Error creating tables: " + e.getMessage());
        }
    }

    public static void addBook(String title, String author, int quantity) {
        String checkSql = "SELECT quantity FROM books WHERE title = ? AND author = ?";
        String insertSql = "INSERT INTO books(title, author, quantity) VALUES(?, ?, ?)";
        String updateSql = "UPDATE books SET quantity = quantity + ? WHERE title = ? AND author = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {

            checkStmt.setString(1, title);
            checkStmt.setString(2, author);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                updateStmt.setInt(1, quantity);
                updateStmt.setString(2, title);
                updateStmt.setString(3, author);
                updateStmt.executeUpdate();
                Output.println("Book quantity updated.");
            } else {
                insertStmt.setString(1, title);
                insertStmt.setString(2, author);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
                Output.println("Book added.");
            }
        } catch (SQLException e) {
            Output.println("Add book failed: " + e.getMessage());
        }
    }

    public static void updateBookQuantity(int bookId, int newQuantity) {
        String sql = "UPDATE books SET quantity = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newQuantity);
            pstmt.setInt(2, bookId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                Output.println("Book quantity updated.");
            } else {
                Output.println("No book found with ID: " + bookId);
            }
        } catch (SQLException e) {
            Output.println("Update book quantity failed: " + e.getMessage());
        }
    }

    public static void deleteBook(int id) {
        String sql = "DELETE FROM books WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            Output.println("Book deleted.");
        } catch (SQLException e) {
            Output.println("Delete book failed: " + e.getMessage());
        }
    }

    public static boolean borrowBook(int memberId, int bookId, LocalDate borrowDate, LocalDate dueDate) {
        String insertTransactionSql = "INSERT INTO transactions (memberId, bookId, borrowDate, dueDate) VALUES (?, ?, ?, ?)";
        String updateBookSql = "UPDATE books SET quantity = quantity - 1 WHERE id = ? AND quantity > 0";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtTransaction = conn.prepareStatement(insertTransactionSql);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(updateBookSql)) {

            pstmtUpdateBook.setInt(1, bookId);
            int bookUpdated = pstmtUpdateBook.executeUpdate();

            if (bookUpdated > 0) {
                pstmtTransaction.setInt(1, memberId);
                pstmtTransaction.setInt(2, bookId);
                pstmtTransaction.setString(3, borrowDate.toString());
                pstmtTransaction.setString(4, dueDate.toString());

                int transactionInserted = pstmtTransaction.executeUpdate();

                if (transactionInserted > 0) {
                    return true;
                } else {
                    Output.println("Transaction failed. Rolling back book quantity.");
                    String rollbackSql = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";
                    try (PreparedStatement rollbackStmt = conn.prepareStatement(rollbackSql)) {
                        rollbackStmt.setInt(1, bookId);
                        rollbackStmt.executeUpdate();
                    }
                }
            } else {
                Output.println("Book is not available or out of stock.");
            }

        } catch (SQLException e) {
            Output.println("Error borrowing book: " + e.getMessage());
        }

        return false;
    }

    public static boolean returnBook(int memberId, int bookId, LocalDate returnDate) {
        String updateTransactionSql = "UPDATE transactions SET returnDate = ? WHERE memberId = ? AND bookId = ? AND returnDate IS NULL";
        String updateBookSql = "UPDATE books SET quantity = quantity + 1 WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmtTransaction = conn.prepareStatement(updateTransactionSql);
             PreparedStatement pstmtUpdateBook = conn.prepareStatement(updateBookSql)) {

            pstmtTransaction.setString(1, returnDate.toString());
            pstmtTransaction.setInt(2, memberId);
            pstmtTransaction.setInt(3, bookId);

            int affectedRows = pstmtTransaction.executeUpdate();

            if (affectedRows > 0) {
                pstmtUpdateBook.setInt(1, bookId);
                pstmtUpdateBook.executeUpdate();
                return true;
            } else {
                Output.println("Transaction not found or book already returned.");
            }

        } catch (SQLException e) {
            Output.println("Error returning book: " + e.getMessage());
        }

        return false;
    }

    public static void showBooks() {
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Book book = new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("quantity")
                );
                Output.println("Book: " + book.getTitle() + ", Author: " + book.getAuthor() + ", Qty: " + book.getQuantity());
            }
        } catch (SQLException e) {
            Output.println("Show books failed: " + e.getMessage());
        }
    }

    public static void addMember(String name, int age) {
        if (name == null || name.trim().isEmpty() || age <= 0) {
            Output.println("Name and valid age are required to add a member.");
            return;
        }

        String checkSql = "SELECT * FROM members WHERE name = ? AND age = ?";
        String insertSql = "INSERT INTO members(name, age) VALUES(?, ?)";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement checkStmt = conn.prepareStatement(checkSql);
             PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {

            checkStmt.setString(1, name.trim());
            checkStmt.setInt(2, age);
            ResultSet rs = checkStmt.executeQuery();

            if (rs.next()) {
                Output.println("Member already exists.");
            } else {
                insertStmt.setString(1, name.trim());
                insertStmt.setInt(2, age);
                insertStmt.executeUpdate();
                Output.println("Member added successfully.");
            }

        } catch (SQLException e) {
            Output.println("Add member failed: " + e.getMessage());
        }
    }

    public static void updateMember(int memberId, String newName, int newAge) {
        String sql = "UPDATE members SET name = ?, age = ? WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, newAge);
            pstmt.setInt(3, memberId);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                Output.println("Member updated successfully.");
            } else {
                Output.println("No member found with ID: " + memberId);
            }
        } catch (SQLException e) {
            Output.println("Update member failed: " + e.getMessage());
        }
    }

    public static void deleteMember(int id) {
        String sql = "DELETE FROM members WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rows = pstmt.executeUpdate();

            if (rows > 0) {
                Output.println("Member deleted successfully.");
            } else {
                Output.println("No member found with ID: " + id);
            }

        } catch (SQLException e) {
            Output.println("Delete member failed: " + e.getMessage());
        }
    }

    public static void showMembers() {
        String sql = "SELECT * FROM members";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Member member = new Member(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("age")
                );
                Output.println("Member: " + member.getName() + ", Age: " + member.getAge());
            }
        } catch (SQLException e) {
            Output.println("Show members failed: " + e.getMessage());
        }
    }

    public static void showTransactions() {
        String sql = "SELECT * FROM transactions";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Transaction tx = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("memberId"),
                        rs.getInt("bookId"),
                        LocalDate.parse(rs.getString("borrowDate")),
                        LocalDate.parse(rs.getString("dueDate")),
                        rs.getString("returnDate") != null ? LocalDate.parse(rs.getString("returnDate")) : null
                );
                Output.println("Transaction ID: " + tx.getId() + ", Book ID: " + tx.getBookId() + ", Member ID: " + tx.getMemberId() +
                        ", Borrow Date: " + tx.getBorrowDate() + ", Due Date: " + tx.getDueDate() +
                        (tx.getReturnDate() != null ? ", Return Date: " + tx.getReturnDate() : ""));
            }
        } catch (SQLException e) {
            Output.println("Show transactions failed: " + e.getMessage());
        }
    }

    public static void findTransactionById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE id = ?";

        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Transaction tx = new Transaction(
                        rs.getInt("id"),
                        rs.getInt("memberId"),
                        rs.getInt("bookId"),
                        LocalDate.parse(rs.getString("borrowDate")),
                        LocalDate.parse(rs.getString("dueDate")),
                        rs.getString("returnDate") != null ? LocalDate.parse(rs.getString("returnDate")) : null
                );

                Output.println("Transaction Found:");
                Output.println("ID: " + tx.getId() +
                        ", Member ID: " + tx.getMemberId() +
                        ", Book ID: " + tx.getBookId() +
                        ", Borrow Date: " + tx.getBorrowDate() +
                        ", Due Date: " + tx.getDueDate() +
                        ", Return Date: " + (tx.getReturnDate() != null ? tx.getReturnDate() : "Not Returned"));

            } else {
                Output.println("No transaction found with ID: " + transactionId);
            }

        } catch (SQLException e) {
            Output.println("Find transaction failed: " + e.getMessage());
        }
    }


}

