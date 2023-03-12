import io.LedgerReader;
import io.LedgerWriter;


import java.io.IOException;
import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var filename = "src/main/resources/example.ledger";
        var file = Path.of(filename);
        var reader = new LedgerReader();

        var ledger = reader.readJournal(file);

        // print accounts
        var accounts = ledger.getAccounts();
        System.out.println("\n--- Accounts ---");
        for (var a : accounts) {
            System.out.println(a.getName());
        }

        // print journal to standard output
        var journal = ledger.getJournal();
        var writer = new LedgerWriter();
        System.out.println("\n--- Journal ---");
        writer.writeJournal(journal);

        // save journal to file
        // var outputFilename = "src/main/resources/output.ledger";
        var outputFilename = "/opt/output.ledger";
        var outputFile = Path.of(outputFilename);
        try {
            writer.writeJournal(journal, outputFile);
        } catch (IOException e) {
            System.out.println("Error writing the journal to a file. Cause: " + e);
        }

        // get balance
        System.out.println("\nBalance for Assets: " + journal.getBalance("Assets"));
        var start = "2023/03/05";
        var end = "2023/03/07";
        System.out.println("Balance for Cash between " + start + " and " + end + ": " +
                journal.getBalance("Cash", start, end));

        // get balance report
        System.out.println("\n--- Balance Report for Assets ---");
        System.out.println(journal.getBalanceReport("Assets"));

        System.out.println("\n--- Balance Report for Cash between " + start + " and " + end + " ---");
        System.out.println(journal.getBalanceReport("Cash", start, end));
    }
}
