package core;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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

    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    /* =============== */
    /* Balance methods */
    /* =============== */

    public Money getBalance(String accountName) {
        return transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(accountName))
                .map(Entry::amount)
                .reduce(Money.zero(defaultCurrency), Money::plus);
    }

    public Money getBalance(Account account) {
        return getBalance(account.getName());
    }

    public Money getBalance(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return transactions.stream()
                .filter(transaction -> !(transaction.date().isBefore(start) || transaction.date().isAfter(end)))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(e -> e.account().getName().contains(accountName))
                .map(Entry::amount)
                .reduce(Money.zero(defaultCurrency), Money::plus);
    }

    public Money getBalance(Account account, String startDate, String endDate) {
        return getBalance(account.getName(), startDate, endDate);
    }

    public String getBalanceReport(String accountName) {
        var report = new StringBuilder();
        var entries = transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(e -> e.account().getName().contains(accountName))
                .toList();
        for (var entry : entries) {
            var name = entry.account().getName();
            var amount = entry.amount();
            report.append(String.format("  %s", amount)).append(String.format("  %s\n", name));
        }
        report.append("-----------------------------------------------------\n");
        report.append(String.format("  %s\n", getBalance(accountName)));

        return report.toString();
    }

    public String getBalanceReport(Account account) {
        return getBalanceReport(account.getName());
    }

    public String getBalanceReport(String accountName, String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        var report = new StringBuilder();
        var entries = transactions.stream()
                .filter(transaction -> !(transaction.date().isBefore(start) || transaction.date().isAfter(end)))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(accountName))
                .toList();
        for (var entry : entries) {
            var name = entry.account().getName();
            var amount = entry.amount();
            report.append(String.format("  %s", amount)).append(String.format("  %s\n", name));
        }
        report.append("-----------------------------------------------------\n");
        report.append(String.format("  %s\n", getBalance(accountName, startDate, endDate)));

        return report.toString();
    }

    public String getBalanceReport(Account account, String startDate, String endDate) {
        return getBalanceReport(account.getName(), startDate, endDate);
    }

    /* ======================= */
    /* Entry filtering methods */
    /* ======================= */

    // Methods that only filter transaction information
    // ------------------------------------------------

    public List<Entry> getEntriesBy(Payee payee) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Entry> getEntriesBy(LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .toList();
    }

    public List<Entry> getEntriesBy(Payee payee, LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee) && transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .toList();
    }

    // Methods that only filter entry information
    // ------------------------------------------

    public List<Entry> getEntriesBy(Account account) {
        return transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(account.getName()))
                .toList();
    }

    public List<Entry> getEntriesBy(Money amount) {
        return transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.amount().equals(amount))
                .toList();
    }

    public List<Entry> getEntriesBy(Account account, Money amount) {
        return transactions.stream()
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().equals(account) && entry.amount().equals(amount))
                .toList();
    }

    // Methods that filter both transaction and entry information
    // ----------------------------------------------------------

    public List<Entry> getEntriesBy(Payee payee, Account account) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(account.getName()))
                .toList();
    }

    public List<Entry> getEntriesBy(Account account, Payee payee) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(account.getName()))
                .toList();
    }

    public List<Entry> getEntriesBy(Payee payee, Money amount) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.amount().equals(amount))
                .toList();
    }

    public List<Entry> getEntriesBy(Account account, LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(account.getName()))
                .toList();
    }

    public List<Entry> getEntriesBy(Money amount, LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.amount().equals(amount))
                .toList();
    }

    public List<Entry> getEntriesBy(Payee payee, Account account, Money amount) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().equals(account) && entry.amount().equals(amount))
                .toList();
    }

    public List<Entry> getEntriesBy(Account account, Payee payee, Money amount) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().equals(account) && entry.amount().equals(amount))
                .toList();
    }

    public List<Entry> getEntriesBy(Payee payee, Account account, LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee) && transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(account.getName()))
                .toList();
    }

    public List<Entry> getEntriesBy(Account account, Payee payee, LocalDate date) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee) && transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().getName().contains(account.getName()))
                .toList();
    }

    public List<Entry> getEntriesBy(Payee payee, LocalDate date, Money amount) {
        return transactions.stream()
                .filter(transaction -> transaction.payee().equals(payee) && transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.amount().equals(amount))
                .toList();
    }

    public List<Entry> getEntriesBy(Account account, LocalDate date, Money amount) {
        return transactions.stream()
                .filter(transaction -> transaction.date().equals(date))
                .map(Transaction::entries)
                .flatMap(Collection::stream)
                .filter(entry -> entry.account().equals(account) && entry.amount().equals(amount))
                .toList();
    }

    /* ============================= */
    /* Transaction filtering methods */
    /* ============================= */

    protected Set<Transaction> getTransactions(Account account) {
        return getTransactions().stream()
                .filter(transaction -> accountInTransaction(account, transaction))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Set<Transaction> getTransactions(List<Account> accounts) {
        return accounts.stream()
                .flatMap(account -> getTransactions(account).stream())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Set<Transaction> getTransactions(Payee payee) {
        return getTransactions().stream()
                .filter(transaction -> payeeInTransaction(payee, transaction))
                .collect(Collectors.toCollection(HashSet::new));
    }

    public Set<Transaction> getTransactions(String startDate, String endDate) {
        var start = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var end = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyy/MM/dd"));

        return getTransactions().stream()
                .filter(transaction -> !(transaction.date().isBefore(start) || transaction.date().isAfter(end)))
                .collect(Collectors.toCollection(HashSet::new));
    }

    protected boolean accountInTransaction(Account account, Transaction transaction) {
        return transaction.entries().stream()
                .anyMatch(entry -> entry.account().equals(account));
    }

    protected boolean payeeInTransaction(Payee payee, Transaction transaction) {
        return transaction.payee().equals(payee);
    }
}
