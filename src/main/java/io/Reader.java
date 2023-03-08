package io;

import core.Account;
import core.Entry;
import core.Transaction;

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
    public static void main(String[] args) {
        var filename = "example.ledger";
        parseFile(filename);
    }

    public static void parseFile(String filename){
        try {
            var fileURI = Objects.requireNonNull(Reader.class.getClassLoader().getResource(filename)).toURI();
            var content = Files.readString((Path.of(fileURI)));
            var RawTransactions = content.split("(\n\n)|(\n*$)");
            for (var rawTransaction: RawTransactions) {
                var transaction = parseTransaction(rawTransaction);
                System.out.println(transaction);
            }
        } catch (URISyntaxException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    static Transaction parseTransaction(String rawTransaction) {
        var lines = rawTransaction.split("\n");
        // header
        var header = parseTransactionHeader(lines[0]);
        var date = LocalDate.parse(header.get(0), DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var status = header.get(1); // not using the transaction status for now
        var note = header.get(2);
        //entries
        var entries = new ArrayList<Entry>();
        for (int i = 1; i < lines.length; i++) {
            var entry = parseEntry(lines[i]);
            var account = new Account(entry.get(0));
            var amount = Double.parseDouble(entry.get(1));
            entries.add(new Entry(account, amount));
        }
        return new Transaction(date, note, entries);
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
