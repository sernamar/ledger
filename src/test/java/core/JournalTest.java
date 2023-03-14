package core;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@DefaultLocale("es-ES")
class JournalTest {

    private Journal journal;
    private CurrencyUnit currency;

    @BeforeEach
    void setUp() {
        journal = new Journal();
        currency = CurrencyUnit.of(Locale.getDefault());

        var date1 = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status1 = TransactionStatus.CLEARED;
        var payee1 = new Payee("Opening Balance");
        var entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(new Account("Assets:Cash"), Money.of(currency, 500)));
        entries1.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, 500)));
        entries1.add(new Entry(new Account("Equity:Opening Balances"), Money.of(currency, -1000)));
        journal.addTransaction(new Transaction(date1, status1, payee1, entries1));

        var date2 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status2 = TransactionStatus.PENDING;
        var payee2 = new Payee("Moe's restaurant");
        var entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(new Account("Expenses:Restaurant:Food"), Money.of(currency, 20)));
        entries2.add(new Entry(new Account("Expenses:Restaurant:Tips"), Money.of(currency, 2)));
        entries2.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        entries2.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, -10)));
        journal.addTransaction(new Transaction(date2, status2, payee2, entries2));

        var date3 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var payee3 = new Payee("Mike's convenience store");
        var entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(new Account("Expenses:Groceries"), Money.of(currency, 35.95)));
        entries3.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -35.95)));
        journal.addTransaction(new Transaction(date3, null, payee3, entries3));
    }

    @Test
    void getBalance() {
        var balance1 = journal.getBalance("Equity");
        var expected1 = Money.of(currency, -1000);
        assertEquals(expected1, balance1);

        var balance2 = journal.getBalance("Cash");
        var expected2 = Money.of(currency, 452.05);
        assertEquals(expected2, balance2);
    }

    @Test
    void getBalanceBetweenDates() {
        // dates before any date in the journal
        var balance1 = journal.getBalance("Cash", "2023/03/01", "2023/03/01");
        var expected1 = Money.of(currency, 0);
        assertEquals(expected1, balance1);

        // dates that only include a single +500 cash transaction
        var balance2 = journal.getBalance("Cash", "2023/03/01", "2023/03/06");
        var expected2 = Money.of(currency, 500);
        assertEquals(expected2, balance2);

        // dates that include all cash transactions in the journal
        var balance3 = journal.getBalance("Cash", "2023/03/01", "2023/03/31");
        var expected3 = Money.of(currency, 452.05);
        assertEquals(expected3, balance3);
    }

    @Test
    void getEntriesByPayee() {
        var payee = "Moe's restaurant";
        var date = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var accountName = "Assets:Cash";
        var amount = Money.of(CurrencyUnit.of(Locale.getDefault()), -12);

        // List<Entry> getEntriesByPayee(String payee)
        var entries = journal.getEntriesByPayee(payee);
        var expected = new ArrayList<>();
        expected.add(new Entry(new Account("Expenses:Restaurant:Food"), Money.of(currency, 20)));
        expected.add(new Entry(new Account("Expenses:Restaurant:Tips"), Money.of(currency, 2)));
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        expected.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, -10)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByPayee(String payee, LocalDate date)
        entries = journal.getEntriesByPayee(payee, date);
        assertEquals(expected, entries);

        // List<Entry> getEntriesByPayee(String payee, String accountName)
        entries = journal.getEntriesByPayee(payee, accountName);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByPayee(String payee, Money amount)
        entries = journal.getEntriesByPayee(payee, amount);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByPayee(String payee, String accountName, Money amount)
        entries = journal.getEntriesByPayee(payee, accountName, amount);
        assertEquals(expected, entries);

        // List<Entry> getEntriesByPayee(String payee, String accountName, LocalDate date)
        entries = journal.getEntriesByPayee(payee, accountName, date);
        assertEquals(expected, entries);

        // List<Entry> getEntriesByPayee(String payee, LocalDate date, Money amount)
        entries = journal.getEntriesByPayee(payee, date, amount);
        assertEquals(expected, entries);
    }

    @Test
    void getEntriesByAccount() {
        var payee = "Moe's restaurant";
        var date = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var accountName = "Assets:Cash";
        var amount = Money.of(CurrencyUnit.of(Locale.getDefault()), -12);

        // List<Entry> getEntriesByAccount(String accountName)
        var entries = journal.getEntriesByAccount(accountName);
        var expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, 500)));
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -35.95)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByAccount(String accountName, Money amount)
        entries = journal.getEntriesByAccount(accountName, amount);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByAccount(String accountName, String payee)
        entries = journal.getEntriesByAccount(accountName, payee);
        assertEquals(expected, entries);

        // List<Entry> getEntriesByAccount(String accountName, LocalDate date)
        entries = journal.getEntriesByAccount(accountName, date);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -35.95)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByAccount(String accountName, String payee, Money amount)
        entries = journal.getEntriesByAccount(accountName, payee, amount);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByAccount(String accountName, String payee, LocalDate date)
        entries = journal.getEntriesByAccount(accountName, payee, date);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesByAccount(String accountName, LocalDate date, Money amount)
        entries = journal.getEntriesByAccount(accountName, date, amount);
        assertEquals(expected, entries);
    }

    @Test
    void getEntriesBy() {
        var date = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var amount = Money.of(CurrencyUnit.of(Locale.getDefault()), -12);

        // List<Entry> getEntriesBy(LocalDate date)
        var entries = journal.getEntriesBy(date);
        var expected = new ArrayList<>();
        expected.add(new Entry(new Account("Expenses:Restaurant:Food"), Money.of(currency, 20)));
        expected.add(new Entry(new Account("Expenses:Restaurant:Tips"), Money.of(currency, 2)));
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        expected.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, -10)));
        expected.add(new Entry(new Account("Expenses:Groceries"), Money.of(currency, 35.95)));
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -35.95)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesBy(Money amount)
        entries = journal.getEntriesBy(amount);
        expected = new ArrayList<>();
        expected.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        assertEquals(expected, entries);

        // List<Entry> getEntriesBy(Money amount, LocalDate date)
        entries = journal.getEntriesBy(amount, date);
        assertEquals(expected, entries);
    }

    @Test
    void getTransactions() {

        // Set<Transaction> getTransactionsByAccount(String accountName)
        var transactions = journal.getTransactionsByAccount("Equity:Opening Balances");
        Set<Transaction> expected = new HashSet<>();

        var date1 = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status1 = TransactionStatus.CLEARED;
        var payee1 = new Payee("Opening Balance");
        var entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(new Account("Assets:Cash"), Money.of(currency, 500)));
        entries1.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, 500)));
        entries1.add(new Entry(new Account("Equity:Opening Balances"), Money.of(currency, -1000)));
        expected.add(new Transaction(date1, status1, payee1, entries1));

        assertEquals(expected, transactions);

        // Set<Transaction> getTransactions(String payee)
        transactions = journal.getTransactions("Opening Balance");
        assertEquals(expected, transactions);

        // Set<Transaction> getTransactions(String startDate, String endDate)
        transactions = journal.getTransactions("2023/03/01", "2023/03/06");
        assertEquals(expected, transactions);

        // Set<Transaction> getTransactions(List<String> accountsNames)
        var accountNames = List.of("Equity:Opening Balances", "Assets:Debit Card");
        transactions = journal.getTransactions(accountNames);

        var date2 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status2 = TransactionStatus.PENDING;
        var payee2 = new Payee("Moe's restaurant");
        var entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(new Account("Expenses:Restaurant:Food"), Money.of(currency, 20)));
        entries2.add(new Entry(new Account("Expenses:Restaurant:Tips"), Money.of(currency, 2)));
        entries2.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        entries2.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, -10)));
        expected.add(new Transaction(date2, status2, payee2, entries2));

        assertEquals(expected, transactions);

        // Set<Transaction> getTransactions(String startDate, String endDate)
        transactions = journal.getTransactions("2023/03/01", "2023/03/31");

        var date3 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var payee3 = new Payee("Mike's convenience store");
        var entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(new Account("Expenses:Groceries"), Money.of(currency, 35.95)));
        entries3.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -35.95)));
        expected.add(new Transaction(date3, null, payee3, entries3));
        assertEquals(expected, transactions);
    }

    @Test
    void accountInTransaction() {
        var date = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status = TransactionStatus.CLEARED;
        var payee = new Payee("Opening Balance");
        var entries = new ArrayList<Entry>();
        entries.add(new Entry(new Account("Assets:Cash"), Money.of(currency, 500)));
        entries.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, 500)));
        entries.add(new Entry(new Account("Equity:Opening Balances"), Money.of(currency, -1000)));
        var transaction = new Transaction(date, status, payee, entries);

        assertTrue(journal.accountNameInTransaction("Assets:Cash", transaction));
        assertFalse(journal.accountNameInTransaction("Foo:Bar:Baz", transaction));
    }

    @Test
    void payeeInTransaction() {
        var date = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status = TransactionStatus.CLEARED;
        var payee = new Payee("Opening Balance");
        var entries = new ArrayList<Entry>();
        entries.add(new Entry(new Account("Assets:Cash"), Money.of(currency, 500)));
        entries.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, 500)));
        entries.add(new Entry(new Account("Equity:Opening Balances"), Money.of(currency, -1000)));
        var transaction = new Transaction(date, status, payee, entries);

        assertTrue(journal.payeeInTransaction("Opening Balance", transaction));
        assertFalse(journal.payeeInTransaction("Foo Bar Baz", transaction));
    }
}