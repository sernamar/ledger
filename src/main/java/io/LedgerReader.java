package io;

import core.*;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.joda.money.format.MoneyAmountStyle;
import org.joda.money.format.MoneyFormatterBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Locale;
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
        this(Locale.getDefault());
    }

    public LedgerReader(Locale locale) {
        this.accounts = new ChartOfAccounts();
        this.journal  = new Journal(locale);
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
        Pattern currencyAmountPattern = Pattern.compile("([$£€¥₹]|\\w+)\\s([+-]?\\d+[.,]?\\d*)");
        var matches = getMatches(amountStr, currencyAmountPattern);

        var inputFormatter = new MoneyFormatterBuilder()
                .appendAmount(MoneyAmountStyle.of(journal.getLocale()))
                .toFormatter();
        var startIndex = 0;

        if (!matches.isEmpty()) {
            var currencyStr = matches.get(0);
            startIndex = currencyStr.length() + 1;
        }

        var parsed = inputFormatter.parse(amountStr, startIndex);
        var onlyAmount = parsed.getAmount();
        var currency = CurrencyUnit.of(journal.getLocale());
        if (onlyAmount != null) {
            return Money.of(currency, onlyAmount);
        } else {
            return null;
        }
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