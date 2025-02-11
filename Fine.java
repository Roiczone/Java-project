import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Fine {
    private static final double DAILY_FINE_RATE = 1.0;

    public static double calculateFine(Transaction transaction) {
        if (transaction.isOverdue()) {
            long daysOverdue = ChronoUnit.DAYS.between(transaction.returnDate, LocalDate.now());
            return daysOverdue * DAILY_FINE_RATE;
        }
        return 0;
    }
}
