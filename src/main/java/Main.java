import io.LedgerReader;

import java.nio.file.Path;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        //var locale = Locale.getDefault();
        //var filename = "src/main/resources/example.ledger";
        //var filename = "src/main/resources/no-currency.ledger";
        var locale = Locale.CHINA;
        var filename = "src/main/resources/yuan.ledger";
        var file = Path.of(filename);

        var reader = new LedgerReader(locale);
        var ledger = reader.readJournal(file);
    }
}
