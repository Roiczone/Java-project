import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static final String URL = "jdbc:sqlite:C:\\Users\\roicz\\OneDrive\\Desktop\\Meine Kurse\\Software engineering\\Java-project\\database\\library.db";

    // Add a new borrow transaction
    public static void borrowBook(int memberId, int bookId, LocalDate borrowDate) {
        String sql = "INSERT INTO transactions(memberId, bookId, borrowDate) VALUES(?, ?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            pstmt.setString(3, borrowDate.toString());
            pstmt.executeUpdate();
            System.out.println("Book borrowed successfully.");
        } catch (SQLException e) {
            System.out.println("Error borrowing book: " + e.getMessage());
        }
    }

    // Return a book

    // Check if a book is already returned
    public static boolean isBookReturned(int memberId, int bookId) {
        String sql = "SELECT returnDate FROM transactions WHERE memberId = ? AND bookId = ? ORDER BY borrowDate DESC LIMIT 1";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberId);
            pstmt.setInt(2, bookId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getString("returnDate") != null;
            }
        } catch (SQLException e) {
            System.out.println("Error checking book return status: " + e.getMessage());
        }
        return false;
    }





    // Display all transactions
//    public static void showAllTransactions() {
//        List<Transaction> transactions = getAllTransactions();
//        if (transactions.isEmpty()) {
//            System.out.println("No transactions found.");
//        } else {
//            for (Transaction transaction : transactions) {
//                System.out.println(transaction);
//            }
//        }
//    }

//    public static void addTransaction(Transaction transaction) {
//    }
}