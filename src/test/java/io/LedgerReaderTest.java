package io;

import core.Account;
import core.Entry;
import core.Transaction;
import core.TransactionStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;


class LedgerReaderTest {

    private final LedgerReader reader = new LedgerReader();

    @Test
    void parseTransactionHeader() {
        // header with status
        var header1 = "2023/03/06 * Opening Balance";
        var expected1 = new ArrayList<>(Arrays.asList("2023/03/06", "*", "Opening Balance"));
        assertEquals(expected1, reader.parseTransactionHeader(header1));

        // header without status
        var header2 = "2023/03/07 Moe's restaurant";
        var expected2 = new ArrayList<>(Arrays.asList("2023/03/07", null, "Moe's restaurant"));
        assertEquals(expected2, reader.parseTransactionHeader(header2));
    }

    @Test
    void parseEntry() {
        // account without spaces, positive number
        var entry1 = "    Assets:Cash                                  500";
        var expected1 = new ArrayList<>(Arrays.asList("Assets:Cash", "500"));
        assertEquals(expected1, reader.parseEntry(entry1));

        // account without spaces, negative number
        var entry2 = "    Equity:Opening Balances                    -1000";
        var expected2 = new ArrayList<>(Arrays.asList("Equity:Opening Balances", "-1000"));
        assertEquals(expected2, reader.parseEntry(entry2));

        // account with two colons
        var entry3 = "    Expenses:Restaurant:Food                      20";
        var expected3 = new ArrayList<>(Arrays.asList("Expenses:Restaurant:Food", "20"));
        assertEquals(expected3, reader.parseEntry(entry3));
    }

    @Test
    void parseTransaction() {
        // cleared transaction
        var transaction1 = """
                2023/03/06 * Opening Balance
                    Assets:Cash                                  500
                    Assets:Debit Card                            500
                    Equity:Opening Balances                    -1000
                """;

        var date1 = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status1 = TransactionStatus.CLEARED;
        var payee1 = "Opening Balance";
        var entries1 = new ArrayList<Entry>();
        entries1.add(new Entry(new Account("Assets:Cash"), 500));
        entries1.add(new Entry(new Account("Assets:Debit Card"), 500));
        entries1.add(new Entry(new Account("Equity:Opening Balances"), -1000));
        var expected1 = new Transaction(date1, status1, payee1, entries1);

        assertEquals(expected1, reader.parseTransaction(transaction1));

        // pending transaction
        var transaction2 = """
                2023/03/07 ! Moe's restaurant
                    Expenses:Restaurant:Food                      20
                    Expenses:Restaurant:Tips                       2
                    Assets:Cash                                  -12
                    Assets:Debit Card                            -10
                """;

        var date2 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status2 = TransactionStatus.PENDING;
        var payee2 = "Moe's restaurant";
        var entries2 = new ArrayList<Entry>();
        entries2.add(new Entry(new Account("Expenses:Restaurant:Food"), 20));
        entries2.add(new Entry(new Account("Expenses:Restaurant:Tips"), 2));
        entries2.add(new Entry(new Account("Assets:Cash"), -12));
        entries2.add(new Entry(new Account("Assets:Debit Card"), -10));
        var expected2 = new Transaction(date2, status2, payee2, entries2);

        assertEquals(expected2, reader.parseTransaction(transaction2));

        // no-status transaction
        var transaction3 = """
                2023/03/07 Mike's convenience store
                    Expenses:Groceries                         35.95
                    Assets:Cash                               -35.95
                """;

        var date3 = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status3 = TransactionStatus.CLEARED;
        var payee3 = "Mike's convenience store";
        var entries3 = new ArrayList<Entry>();
        entries3.add(new Entry(new Account("Expenses:Groceries"), 35.95));
        entries3.add(new Entry(new Account("Assets:Cash"), -35.95));
        var expected3 = new Transaction(date3, status3, payee3, entries3);

        assertEquals(expected3, reader.parseTransaction(transaction3));
    }
}