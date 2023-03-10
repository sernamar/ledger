import io.LedgerReader;

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
