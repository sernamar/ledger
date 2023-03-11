import io.LedgerReader;
import io.LedgerWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import java.io.IOException;
import java.nio.file.Path;

public class Main {
    private final static Logger logger = LogManager.getLogger();

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
        try {
            writer.writeJournal(ledger.getJournal(), outputFile);
        } catch (IOException e) {
            logger.error(e);
        }
    }
}
