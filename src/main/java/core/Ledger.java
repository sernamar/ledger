package core;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

public class Ledger {
    private final ChartOfAccounts accounts;
    private final Journal journal;

    public Ledger() {
        accounts = new ChartOfAccounts();
        journal = new Journal();
    }

    public Set<Account> getAccounts() {
        return accounts.getAccounts();
    }

    public List<Transaction> getTransactions() {
        return journal.getTransactions();
    }

    public boolean readJournal(Path filePath) {
        try {
            var content = Files.readString(filePath);
            var RawTransactions = content.split("(\n\n)|(\n*$)");
            for (var rawTransaction : RawTransactions) {
                var transaction = parseTransaction(rawTransaction);
                journal.addTransaction(transaction);
            }
            return true;
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

    public static void main(String[] args) {
        var filename = "src/main/resources/example.ledger";
        var file = Path.of(filename);

        var ledger = new Ledger();
        var readOk = ledger.readJournal(file);

        if (readOk) {
            // print accounts
            var accounts = ledger.getAccounts();
            System.out.println("--- Accounts ---");
            for (var a : accounts) {
                System.out.println(a);
            }
            System.out.println();

            // print transactions
            var transactions = ledger.getTransactions();
            System.out.println("--- Transactions ---");
            for (var t : transactions) {
                System.out.println(t);
            }
        }
    }
}
