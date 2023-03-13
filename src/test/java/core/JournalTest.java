package core;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junitpioneer.jupiter.DefaultLocale;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
        var payee1 = "Opening Balance";
        var entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(new Account("Assets:Cash"), Money.of(currency, 500)));
        entries1.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, 500)));
        entries1.add(new Entry(new Account("Equity:Opening Balances"), Money.of(currency, -1000)));
        journal.addTransaction(new Transaction(date1, status1, payee1, entries1));

        var date2 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status2 = TransactionStatus.PENDING;
        var payee2 = "Moe's restaurant";
        var entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(new Account("Expenses:Restaurant:Food"), Money.of(currency, 20)));
        entries2.add(new Entry(new Account("Expenses:Restaurant:Tips"), Money.of(currency, 2)));
        entries2.add(new Entry(new Account("Assets:Cash"), Money.of(currency, -12)));
        entries2.add(new Entry(new Account("Assets:Debit Card"), Money.of(currency, -10)));
        journal.addTransaction(new Transaction(date2, status2, payee2, entries2));

        var date3 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var payee3 = "Mike's convenience store";
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
}