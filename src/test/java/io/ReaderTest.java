package io;

import core.Account;
import core.Entry;
import core.Transaction;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import static io.Reader.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

class ReaderTest {
    @Test
    void testParseTransactionHeader() {
        // header with status
        var header1 = "2023/03/06 * Opening Balance";
        var expected1 = new ArrayList<>(Arrays.asList("2023/03/06", "*", "Opening Balance"));
        assertEquals(expected1, parseTransactionHeader(header1));

        // header without status
        var header2 = "2023/03/07 Moe's restaurant";
        var expected2 = new ArrayList<>(Arrays.asList("2023/03/07", null, "Moe's restaurant"));
        assertEquals(expected2, parseTransactionHeader(header2));
    }

    @Test
    void testParseEntry() {
        // account without spaces, positive number
        var entry1 = "    Assets:Cash                                  500";
        var expected1 = new ArrayList<>(Arrays.asList("Assets:Cash", "500"));
        assertEquals(expected1, parseEntry(entry1));

        // account without spaces, negative number
        var entry2 = "    Equity:Opening Balances                    -1000";
        var expected2 = new ArrayList<>(Arrays.asList("Equity:Opening Balances", "-1000"));
        assertEquals(expected2, parseEntry(entry2));

        // account with two colons
        var entry3 = "    Expenses:Restaurant:Food                      20";
        var expected3 = new ArrayList<>(Arrays.asList("Expenses:Restaurant:Food", "20"));
        assertEquals(expected3, parseEntry(entry3));
    }

    @Test
    void testParseTransaction() {
        // transaction 1
        var transaction1 = """
                2023/03/06 * Opening Balance
                    Assets:Cash                                  500
                    Assets:Debit Card                            500
                    Equity:Opening Balances                    -1000
                """;

        var date = LocalDate.parse("2023/03/06", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var note = "Opening Balance";
        var entries = new ArrayList<Entry>();
        entries.add(new Entry(new Account("Assets:Cash"), 500));
        entries.add(new Entry(new Account("Assets:Debit Card"), 500));
        entries.add(new Entry(new Account("Equity:Opening Balances"), -1000));
        var expected1 = new Transaction(date, note, entries);

        assertEquals(expected1, parseTransaction(transaction1));

        // transaction 2
        var transaction2 = """
                2023/03/07 Moe's restaurant
                    Expenses:Restaurant:Food                      20
                    Expenses:Restaurant:Tips                       2
                    Assets:Cash                                  -12
                    Assets:Debit Card                            -10
                """;

        date = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        note = "Moe's restaurant";
        entries = new ArrayList<>();
        entries.add(new Entry(new Account("Expenses:Restaurant:Food"), 20));
        entries.add(new Entry(new Account("Expenses:Restaurant:Tips"), 2));
        entries.add(new Entry(new Account("Assets:Cash"), -12));
        entries.add(new Entry(new Account("Assets:Debit Card"), -10));
        var expected2 = new Transaction(date, note, entries);

        assertEquals(expected2, parseTransaction(transaction2));

        // transaction 3
        var transaction3 = """
                2023/03/07 Mike's convenience store
                    Expenses:Groceries                         35.95
                    Assets:Cash                               -35.95
                """;

        date = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        note = "Mike's convenience store";
        entries = new ArrayList<>();
        entries.add(new Entry(new Account("Expenses:Groceries"), 35.95));
        entries.add(new Entry(new Account("Assets:Cash"), -35.95));
        var expected3 = new Transaction(date, note, entries);

        assertEquals(expected3, parseTransaction(transaction3));
    }
}