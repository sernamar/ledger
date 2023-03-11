package io;

import core.Entry;
import core.Journal;
import core.Transaction;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * Writes a journal to a file stored using the Ledger format.
 * (see: <a href="https://www.ledger-cli.org/3.0/doc/ledger3.html#Journal-File-Format-for-Developers">
 *         Ledger 3 documentation - Journal File Format for Developers
 *       </a>)
 */
public class LedgerWriter implements Writer {
    @Override
    public void writeJournal(Journal journal) {
        var transactions = journal.getTransactions();
        for (var t : transactions) {
            var header = buildHeader(t);
            System.out.println(header);
            var entries = t.entries();
            for (var e : entries) {
                var entry = buildEntry(e);
                System.out.println(entry);
            }
            System.out.println();
        }
    }

    @Override
    public void writeJournal(Journal journal, Path path) throws IOException {
        Files.write(path, "".getBytes());  // truncate the file if it exists, or create it if it doesn't exist
        var transactions = journal.getTransactions();
        // using a classic for loop because we need to do something different in the last transaction
        for (int i = 0; i < transactions.size(); i++) {
            var transaction = transactions.get(i);
            // write header
            var header = buildHeader(transaction) + "\n";
            Files.write(path, header.getBytes(), StandardOpenOption.APPEND);
            // write entries
            var entries = transaction.entries();
            for (var e : entries) {
                var entry = buildEntry(e) + "\n";
                Files.write(path, entry.getBytes(), StandardOpenOption.APPEND);
            }
            // add a new line between transactions, but not after the last one
            if (i < transactions.size() - 1) {
                Files.write(path, "\n".getBytes(), StandardOpenOption.APPEND);
            }
        }
    }

    protected String buildHeader(Transaction t) {
        var header = new StringBuilder();

        var date = t.date().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status = t.status();
        var payee = t.payee();

        header.append(date);
        if (status != null) {
            switch (status) {
                case CLEARED -> header.append(" *");
                case PENDING -> header.append(" ?");
            }
        }
        header.append(" ").append(payee);
        return header.toString();
    }

    protected String buildEntry(Entry e) {
        var entry = new StringBuilder();

        var accountName = e.account().getName();
        entry.append(String.format("    %-40s", accountName));

        var amount = e.amount();
        var formattedAmount = getFormattedAmount(amount);
        entry.append(formattedAmount);

        return entry.toString();
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
