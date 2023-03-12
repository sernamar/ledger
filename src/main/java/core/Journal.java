package core;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

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
    private final CurrencyUnit defaultCurrency;

    public Journal() {
        transactions = new ArrayList<>();
        defaultCurrency = CurrencyUnit.of(Locale.getDefault());
    }

    public CurrencyUnit getDefaultCurrency() {
        return defaultCurrency;
    }

    public void addTransaction (Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public Money getBalance(String accountName) {
        return filterEntriesBy(accountName)
                .map(Entry::amount)
                .reduce(Money.zero(defaultCurrency), Money::plus);
    }

    public Money getBalance(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return filterEntriesBy(accountName, start, end)
                .map(Entry::amount)
                .reduce(Money.zero(defaultCurrency), Money::plus);
    }

    public String getBalanceReport(String accountName) {
        var report = new StringBuilder();
        var entries = filterEntriesBy(accountName).toList();
        for (var entry : entries) {
            var name = entry.account().getName();
            var amount = entry.amount();
            report.append(String.format("  %s", amount)).append(String.format("  %s\n", name));
        }
        report.append("-----------------------------------------------------\n");
        report.append(String.format("  %s\n", getBalance(accountName)));

        return report.toString();
    }

    public String getBalanceReport(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        var report = new StringBuilder();
        var entries = filterEntriesBy(accountName, start, end).toList();
        for (var entry : entries) {
            var name = entry.account().getName();
            var amount = entry.amount();
            report.append(String.format("  %s", amount)).append(String.format("  %s\n", name));
        }
        report.append("-----------------------------------------------------\n");
        report.append(String.format("  %s\n", getBalance(accountName, startDate, endDate)));

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
}
