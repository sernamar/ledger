package io;

import core.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.regex.Pattern;

public class Reader {

    public static Journal readFile(String filename){
        try {
            var journal = new Journal();
            var fileURI = Objects.requireNonNull(Reader.class.getClassLoader().getResource(filename)).toURI();
            var content = Files.readString((Path.of(fileURI)));
            var RawTransactions = content.split("(\n\n)|(\n*$)");
            for (var rawTransaction: RawTransactions) {
                var transaction = parseTransaction(rawTransaction);
                journal.addTransaction(transaction);
            }
            return journal;
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Transaction parseTransaction(String rawTransaction) {
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
            var account = new Account(entry.get(0));
            var amount = Double.parseDouble(entry.get(1));
            entries.add(new Entry(account, amount));
        }
        return new Transaction(date, status, payee, entries);
    }

    private static TransactionStatus getTransactionStatus(String statusSymbol) {
        TransactionStatus status;
        if (statusSymbol != null && statusSymbol.equals("!")) {
            status = TransactionStatus.PENDING;
        } else {
            status = TransactionStatus.CLEARED;
        }
        return status;
    }

    static ArrayList<String> parseTransactionHeader(String line) {
        var pattern = Pattern.compile("(^.*?(?=\\s))\\s?([*!])?\\s(.*)");
        return getMatches(line, pattern);
    }

    static ArrayList<String> parseEntry(String line) {
        var pattern = Pattern.compile("\\s+(.*?(?=\\s{2,}))\\s+(.*)");
        return getMatches(line, pattern);
    }

    private static ArrayList<String> getMatches(String line, Pattern pattern) {
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
