import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionManager {
    private static List<Transaction> transactions = new ArrayList<>();

    // Add a new transaction
    public static void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Return a book
    public static void returnBook(int transactionId, LocalDate returnDate) {
        for (Transaction transaction : transactions) {
            if (transaction.getTransactionId().equals(transactionId) && transaction.getType().equals("Borrow")) {
                transaction.returnBook(returnDate);
                System.out.println("Book returned successfully.");
                if (transaction.getFineAmount() > 0) {
                    System.out.println("Fine due: $" + transaction.getFineAmount());
                } else {
                    System.out.println("No fine. Book returned on time.");
                }
                return;
            }
        }
        System.out.println("Transaction not found or book already returned.");
    }

    // Get all transactions
    public static List<Transaction> getAllTransactions() {
        return transactions;
    }
}
