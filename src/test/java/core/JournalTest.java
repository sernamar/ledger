package core;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JournalTest {

    private final Journal journal = new Journal();

    @BeforeEach
    void setUp() {
        var date1 = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status1 = TransactionStatus.CLEARED;
        var payee1 = "Opening Balance";
        var entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(new Account("Assets:Cash"), 500));
        entries1.add(new Entry(new Account("Assets:Debit Card"), 500));
        entries1.add(new Entry(new Account("Equity:Opening Balances"), -1000));
        journal.addTransaction(new Transaction(date1, status1, payee1, entries1));

        var date2 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status2 = TransactionStatus.PENDING;
        var payee2 = "Moe's restaurant";
        var entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(new Account("Expenses:Restaurant:Food"), 20));
        entries2.add(new Entry(new Account("Expenses:Restaurant:Tips"), 2));
        entries2.add(new Entry(new Account("Assets:Cash"), -12));
        entries2.add(new Entry(new Account("Assets:Debit Card"), -10));
        journal.addTransaction(new Transaction(date2, status2, payee2, entries2));

        var date3 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        TransactionStatus status3 = null;
        var payee3 = "Mike's convenience store";
        var entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(new Account("Expenses:Groceries"), 35.95));
        entries3.add(new Entry(new Account("Assets:Cash"), -35.95));
        journal.addTransaction(new Transaction(date3, status3, payee3, entries3));
    }

    @Test
    void getBalance() {
        var balance1 = journal.getBalanceBetweenDates("Equity");
        var expected1 = BigDecimal.valueOf(-1000.0);
        assertEquals(expected1, balance1);

        var balance2 = journal.getBalanceBetweenDates("Cash");
        var expected2 = BigDecimal.valueOf(452.05);
        assertEquals(expected2, balance2);
    }

    @Test
    void getBalanceBetweenDates() {
        // dates before any date in the journal
        var balance1 = journal.getBalanceBetweenDates("Cash", "2023/03/01", "2023/03/01");
        var expected1 = BigDecimal.valueOf(0);
        assertEquals(expected1, balance1);

        // dates that only include a single +500 cash transaction
        var balance2 = journal.getBalanceBetweenDates("Cash", "2023/03/01", "2023/03/06");
        var expected2 = BigDecimal.valueOf(500.0);
        assertEquals(expected2, balance2);

        // dates that include all cash transactions in the journal
        var balance3 = journal.getBalanceBetweenDates("Cash", "2023/03/01", "2023/03/31");
        var expected3 = BigDecimal.valueOf(452.05);
        assertEquals(expected3, balance3);
    }
}