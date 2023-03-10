package io;

import core.Entry;
import core.Journal;
import core.Transaction;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

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
    public void writeJournal(Journal journal, Path path) {
        try {
            Files.write(path, "".getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        var transactions = journal.getTransactions();
        for (int i=0; i < transactions.size(); i++) { // doing a classic for loop to do something different in the last transaction
            var transaction = transactions.get(i);
            var header = buildHeader(transaction) + "\n";
            try {
                Files.write(path, header.getBytes(), StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            var entries = transaction.entries();
            for (var e : entries) {
                var entry = buildEntry(e) + "\n";
                try {
                    Files.write(path, entry.getBytes(), StandardOpenOption.APPEND);
                } catch (IOException exception) {
                    throw new RuntimeException(exception);
                }
            }
            // add a new line between transactions, but not after the last one
            if (i < transactions.size() - 1) {
                try {
                    Files.write(path, "\n".getBytes(), StandardOpenOption.APPEND);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    private String buildHeader(Transaction t) {
        var header = new StringBuilder();

        var date = t.date();
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

    private String buildEntry(Entry e) {
        var entry = new StringBuilder();

        var accountName = e.account().getName();
        var amount = e.amount();

        entry.append(String.format("    %-40s", accountName));
        try {
            entry.append(String.format("%8d", amount.intValueExact()));
        } catch (ArithmeticException exception) {
            entry.append(String.format("%8.2f", amount.doubleValue()));
        }
        return entry.toString();
    }
}
