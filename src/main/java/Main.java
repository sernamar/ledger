import core.Account;
import core.Payee;
import io.LedgerReader;

import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {

        var locale = Locale.getDefault();
        var filename = "src/main/resources/example.ledger";
        //var filename = "src/main/resources/no-currency.ledger";
        //var locale = Locale.CHINA;
        //var filename = "src/main/resources/yuan.ledger";
        var file = Path.of(filename);

        var reader = new LedgerReader(locale);
        var ledger = reader.readJournal(file);

        var journal = ledger.getJournal();
        System.out.println("Balance for Assets: " + journal.getBalanceAsString("Assets"));
        var start = "2023/03/05";
        var end = "2023/03/07";
        System.out.println("Balance for Cash between " + start + " and " + end + ": " +
                journal.getBalanceAsString("Cash", start, end));

        System.out.println("\n--- Balance Report for Assets ---");
        System.out.println(journal.getBalanceReport("Assets"));

        System.out.println("\n--- Balance Report for Cash between " + start + " and " + end + " ---");
        System.out.println(journal.getBalanceReport("Cash", start, end));

        System.out.println("--- Transaction report for account Assets:Cash ---");
        System.out.println(journal.getTransactionReport(new Account("Assets:Cash")));

        System.out.println("--- Transaction report for accounts Assets:Debit Card and Equity:Opening Balances ---");
        System.out.println(journal.getTransactionReport(List.of(new Account("Assets:Debit Card"), new Account("Equity:Opening Balances"))));

        System.out.println("--- Transaction report for payee Moe's restaurant ---");
        System.out.println(journal.getTransactionReport(new Payee("Moe's restaurant")));

        System.out.println("--- Transaction report for all transactions between 2023/03/01 and 2023/03/06 ---");
        System.out.println(journal.getTransactionReport("2023/03/01", "2023/03/06"));

        System.out.println("--- Transaction report for all transactions between 2023/03/01 and 2023/03/31 ---");
        System.out.println(journal.getTransactionReport("2023/03/01", "2023/03/31"));
    }
}
