package core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
/**
 * Represents a journal (a group of transactions).
 */

public class Journal {
    private final List<Transaction> transactions;

    public Journal() {
        transactions = new ArrayList<>();
    }

    public void addTransaction (Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public BigDecimal getBalance(String accountName) {
        return transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(e -> e.account().getName().contains(accountName))
                .map(Entry::amount)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    public BigDecimal getBalance(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return transactions.stream()
                .filter(t -> !(t.date().isBefore(start) || t.date().isAfter(end)))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(e -> e.account().getName().contains(accountName))
                .map(Entry::amount)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }
}
