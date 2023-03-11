package io;

import core.Account;
import core.Entry;
import core.Transaction;
import core.TransactionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class LedgerWriterTest {
    private final LedgerWriter writer = new LedgerWriter();
    private final List<Transaction> transactions = new ArrayList<>();

    @BeforeEach
    void setUp() {
        var date1 = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status1 = TransactionStatus.CLEARED;
        var payee1 = "Opening Balance";
        var entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(new Account("Assets:Cash"), 500));
        entries1.add(new Entry(new Account("Assets:Debit Card"), 500));
        entries1.add(new Entry(new Account("Equity:Opening Balances"), -1000));
        transactions.add(new Transaction(date1, status1, payee1, entries1));

        var date2 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status2 = TransactionStatus.PENDING;
        var payee2 = "Moe's restaurant";
        var entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(new Account("Expenses:Restaurant:Food"), 20));
        entries2.add(new Entry(new Account("Expenses:Restaurant:Tips"), 2));
        entries2.add(new Entry(new Account("Assets:Cash"), -12));
        entries2.add(new Entry(new Account("Assets:Debit Card"), -10));
        transactions.add(new Transaction(date2, status2, payee2, entries2));

        var date3 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        TransactionStatus status3 = null;
        var payee3 = "Mike's convenience store";
        var entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(new Account("Expenses:Groceries"), 35.95));
        entries3.add(new Entry(new Account("Assets:Cash"), -35.95));
        transactions.add(new Transaction(date3, status3, payee3, entries3));
    }

    @Test
    void buildHeader() {
        var transaction1 = transactions.get(0);
        var expected1 = "2023/03/06 * Opening Balance";
        assertEquals(expected1, writer.buildHeader(transaction1));

        var transaction2 = transactions.get(1);
        var expected2 = "2023/03/07 ? Moe's restaurant";
        assertEquals(expected2, writer.buildHeader(transaction2));

        var transaction3 = transactions.get(2);
        var expected3 = "2023/03/07 Mike's convenience store";
        assertEquals(expected3, writer.buildHeader(transaction3));
    }

    @Test
    void buildEntry() {
        var entry1 = transactions.get(0).entries().get(0);
        var expected1 = "    Assets:Cash                                  500";
        assertEquals(expected1, writer.buildEntry(entry1));

        var entry2 = transactions.get(1).entries().get(2);
        var expected2 = "    Assets:Cash                                  -12";
        assertEquals(expected2, writer.buildEntry(entry2));

        var entry3 = transactions.get(2).entries().get(1);
        var expected3 = "    Assets:Cash                               -35.95";
        assertEquals(expected3, writer.buildEntry(entry3));
    }
}