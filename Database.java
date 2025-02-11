import java.sql.*;

public class Database {
    private static final String URL = "jdbc:sqlite:library.db";

    public static void connect() {
        try (Connection conn = DriverManager.getConnection(URL)) {
            if (conn != null) {
                System.out.println("Connected to SQLite database.");
            }
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }

    public static void createTables() {
        String booksTable = "CREATE TABLE IF NOT EXISTS books (id TEXT PRIMARY KEY, title TEXT, author TEXT, isAvailable INTEGER);";
        String membersTable = "CREATE TABLE IF NOT EXISTS members (id TEXT PRIMARY KEY, name TEXT);";
        String transactionsTable = "CREATE TABLE IF NOT EXISTS transactions (id INTEGER PRIMARY KEY AUTOINCREMENT, memberId TEXT, bookId TEXT, borrowDate TEXT, returnDate TEXT, FOREIGN KEY(memberId) REFERENCES members(id), FOREIGN KEY(bookId) REFERENCES books(id));";

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

    public static void addBook(String id, String title, String author) {
        String sql = "INSERT INTO books(id, title, author, isAvailable) VALUES(?, ?, ?, 1)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, title);
            pstmt.setString(3, author);
            pstmt.executeUpdate();
            System.out.println("Book added to database.");
        } catch (SQLException e) {
            System.out.println("Error adding book: " + e.getMessage());
        }
    }

    public static void addMember(String id, String name) {
        String sql = "INSERT INTO members(id, name) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, name);
            pstmt.executeUpdate();
            System.out.println("Member added to database.");
        } catch (SQLException e) {
            System.out.println("Error adding member: " + e.getMessage());
        }
    }

    public static void showBooks() {
        String sql = "SELECT * FROM books";
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString("title") + " by " + rs.getString("author") + " (Available: " + rs.getInt("isAvailable") + ")");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving books: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        connect();
        createTables();
        addBook("1", "Java Programming", "James Gosling");
        addMember("M001", "Alice");
        showBooks();
    }
}
