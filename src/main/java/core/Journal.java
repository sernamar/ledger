package core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;

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
        return filterEntriesBy(accountName)
                .map(Entry::amount)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    public BigDecimal getBalance(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return filterEntriesBy(accountName, start, end)
                .map(Entry::amount)
                .reduce(BigDecimal.valueOf(0), BigDecimal::add);
    }

    public String getBalanceReport(String accountName) {
        var report = new StringBuilder();
        var entries = filterEntriesBy(accountName).toList();
        for (var entry : entries) {
            var name = entry.account().getName();
            var amount = getFormattedAmount(entry.amount());
            report.append(amount).append(String.format("  %s\n", name));
        }
        report.append("-----------------------------------------------------\n");
        var balance = getFormattedAmount(getBalance(accountName));
        report.append(balance).append("\n");

        return report.toString();
    }

    public String getBalanceReport(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        var report = new StringBuilder();
        var entries = filterEntriesBy(accountName, start, end).toList();
        for (var entry : entries) {
            var name = entry.account().getName();
            var amount = getFormattedAmount(entry.amount());
            report.append(amount).append(String.format("  %s\n", name));
        }
        report.append("-----------------------------------------------------\n");
        var balance = getFormattedAmount(getBalance(accountName, startDate, endDate));
        report.append(balance).append("\n");

        return report.toString();
    }

    private Stream<Entry> filterEntriesBy(String accountName) {
        return transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(e -> e.account().getName().contains(accountName));
    }

    private Stream<Entry> filterEntriesBy(String accountName, LocalDate start, LocalDate end) {
        return transactions.stream()
                .filter(t -> !(t.date().isBefore(start) || t.date().isAfter(end)))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(e -> e.account().getName().contains(accountName));
    }

    private String getFormattedAmount(BigDecimal amount) {
        /*
         `intValueExtract()` is a `BigDecimal` method that throws an `ArithmeticException` if `amount` has a nonzero
         fractional part, which means that it cannot be converted to an `int`: instead, we convert it to a `double` in
         the `catch` expression.
        */
        try {
            return String.format("%8d", amount.intValueExact());
        } catch (ArithmeticException exception) {
            return String.format(new Locale("en", "US"), "%8.2f", amount.doubleValue());
        }
    }
}
