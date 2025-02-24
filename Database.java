import java.sql.*;

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

    }

}