import io.LedgerReader;
import io.LedgerWriter;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        var filename = "src/main/resources/example.ledger";
        var file = Path.of(filename);
        var reader = new LedgerReader();

        var ledger = reader.readJournal(file);

        // print accounts
        var accounts = ledger.getAccounts();
        System.out.println("--- Accounts ---");
        for (var a : accounts) {
            System.out.println(a.getName());
        }
        System.out.println();

        // print journal to standard output
        var writer = new LedgerWriter();
        System.out.println("--- Journal ---");
        writer.writeJournal(ledger.getJournal());

        // save journal to file
        var outputFilename = "src/main/resources/output.ledger";
        var outputFile = Path.of(outputFilename);
        writer.writeJournal(ledger.getJournal(), outputFile);
    }
}
