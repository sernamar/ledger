package io;

import core.*;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

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
        var status = (header.get(1) != null) ? getTransactionStatus(header.get(1)) : null;
        var payee = new Payee(header.get(2));
        //entries
        var entries = new ArrayList<Entry>();
        for (int i = 1; i < lines.length; i++) {
            var entry = parseEntry(lines[i]);
            var accountName = entry.get(0);
            var account = accounts.addAccount(accountName);
            var amountStr = entry.get(1);
            var amount = parseAmount(amountStr);
            entries.add(new Entry(account, amount));
        }
        return new Transaction(date, status, payee, entries);
    }

    private TransactionStatus getTransactionStatus(String statusSymbol) {
        return switch (statusSymbol) {
            case "*" -> TransactionStatus.CLEARED;
            case "!" -> TransactionStatus.PENDING;
            default -> null;
        };
    }

    protected ArrayList<String> parseTransactionHeader(String line) {
        var pattern = Pattern.compile("(^.*?(?=\\s))\\s?([*!])?\\s(.*)");
        return getMatches(line, pattern);
    }

    protected ArrayList<String> parseEntry(String line) {
        var pattern = Pattern.compile("\\s+(.*?(?=\\s{2,}))\\s+(.*)");
        return getMatches(line, pattern);
    }

    protected Money parseAmount(String amountStr) {
        Pattern currencyAmountPattern = Pattern.compile("(\\w+)\\s([+-]?\\d+\\.?\\d*)");
        Pattern amountCurrencyPattern = Pattern.compile("([+-]?\\d+\\.?\\d*)\\s(\\w+)");
        Pattern amountOnlyPattern = Pattern.compile("([+-]?\\d+.?\\d*)");
        CurrencyUnit currency;
        double amount;
        var currencyAmountMatches = getMatches(amountStr, currencyAmountPattern);
        var amountCurrencyMatches = getMatches(amountStr, amountCurrencyPattern);
        var amountOnlyMatches = getMatches(amountStr, amountOnlyPattern);
        if (!currencyAmountMatches.isEmpty()) {
            currency = CurrencyUnit.of(currencyAmountMatches.get(0));
            amount   = Double.parseDouble(currencyAmountMatches.get(1));
            return Money.of(currency, amount);
        } else if (!amountCurrencyMatches.isEmpty()) {
            amount = Double.parseDouble(amountCurrencyMatches.get(0));
            currency = CurrencyUnit.of(amountCurrencyMatches.get(1));
            return Money.of(currency, amount);
        } else if (!amountOnlyMatches.isEmpty()) {
                amount = Double.parseDouble(amountOnlyMatches.get(0));
                return Money.of(journal.getDefaultCurrency(), amount);
        }
        return null;
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
