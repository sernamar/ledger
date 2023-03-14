import io.LedgerReader;

import java.nio.file.Path;

public class Main {
    public static void main(String[] args) {
        var filename = "src/main/resources/example.ledger";
        var file = Path.of(filename);
        var reader = new LedgerReader();
        var ledger = reader.readJournal(file);

        var journal = ledger.getJournal();
    }
}
