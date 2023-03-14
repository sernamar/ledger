import core.Account;
import core.Payee;
import io.LedgerReader;

import java.nio.file.Path;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        var filename = "src/main/resources/example.ledger";
        var file = Path.of(filename);
        var reader = new LedgerReader();
        var ledger = reader.readJournal(file);
        var journal = ledger.getJournal();

        System.out.println("--- Transaction report for account Assets:Cash ---");
        System.out.println(journal.getTransactionReport(new Account("Assets:Cash")));

        System.out.println("--- Transaction report for accounts Assets:Debit Card and Equity:Opening Balances ---");
        System.out.println(journal.getTransactionReport(
                List.of(new Account("Assets:Debit Card"), new Account("Equity:Opening Balances"))));

        System.out.println("--- Transaction report for payee Moe's restaurant ---");
        System.out.println(journal.getTransactionReport(new Payee("Moe's restaurant")));

        System.out.println("--- Transaction report for all transactions between 2023/03/01 and 2023/03/06 ---");
        System.out.println(journal.getTransactionReport("2023/03/01", "2023/03/06"));

        System.out.println("--- Transaction report for all transactions between 2023/03/01 and 2023/03/31 ---");
        System.out.println(journal.getTransactionReport("2023/03/01", "2023/03/31"));
    }
}
