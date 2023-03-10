package io;

import core.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Reads a journal from a file stored using the Ledger format.
 * (see: <a href="https://www.ledger-cli.org/3.0/doc/ledger3.html#Journal-File-Format-for-Developers">
 *         Ledger 3 documentation - Journal File Format for Developers
 *       </a>)
 */

public class LedgerReader implements Reader {

    private final ChartOfAccounts accounts;
    private final Journal journal;

    public LedgerReader() {
        this.accounts = new ChartOfAccounts();
        this.journal  = new Journal();
    }

    @Override
    public Ledger readJournal(Path filePath) {
        try {
            var content = Files.readString(filePath);
            var RawTransactions = content.split("(\n\n)|(\n*$)");
            for (var rawTransaction : RawTransactions) {
                var transaction = parseTransaction(rawTransaction);
                journal.addTransaction(transaction);
            }
            return new Ledger(accounts, journal);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected Transaction parseTransaction(String rawTransaction) {
        var lines = rawTransaction.split("\n");
        // header
        var header = parseTransactionHeader(lines[0]);
        var date = LocalDate.parse(header.get(0), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status = getTransactionStatus(header.get(1));
        var payee = header.get(2);
        //entries
        var entries = new ArrayList<Entry>();
        for (int i = 1; i < lines.length; i++) {
            var entry = parseEntry(lines[i]);
            var accountName = entry.get(0);
            var account = accounts.addAccount(accountName);
            var amount = Double.parseDouble(entry.get(1));
            entries.add(new Entry(account, amount));
        }
        return new Transaction(date, status, payee, entries);
    }

    private TransactionStatus getTransactionStatus(String statusSymbol) {
        TransactionStatus status;
        if (statusSymbol != null && statusSymbol.equals("!")) {
            status = TransactionStatus.PENDING;
        } else {
            status = TransactionStatus.CLEARED;
        }
        return status;
    }

    protected ArrayList<String> parseTransactionHeader(String line) {
        var pattern = Pattern.compile("(^.*?(?=\\s))\\s?([*!])?\\s(.*)");
        return getMatches(line, pattern);
    }

    protected ArrayList<String> parseEntry(String line) {
        var pattern = Pattern.compile("\\s+(.*?(?=\\s{2,}))\\s+(.*)");
        return getMatches(line, pattern);
    }

    private ArrayList<String> getMatches(String line, Pattern pattern) {
        var matches = new ArrayList<String>();
        var matcher = pattern.matcher(line);
        if (matcher.find()) {
            for (int i = 1; i <= matcher.groupCount(); i++) {
                matches.add(matcher.group(i));
            }
        }
        return matches;
    }
}
