import io.LedgerReader;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;


import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Main {
    public static void main(String[] args) {
        var filename = "src/main/resources/example.ledger";
        var file = Path.of(filename);
        var reader = new LedgerReader();
        var ledger = reader.readJournal(file);

        var journal = ledger.getJournal();

        var payee = "Moe's restaurant";
        var date = LocalDate.parse("2023/03/07", DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        var accountName = "Assets:Cash";
        var amount = Money.of(CurrencyUnit.of(Locale.getDefault()), 500);

        /* ============= */
        /* Entry methods */
        /* ============= */

        // Methods that only filter transaction information
        // ------------------------------------------------

        // entries by payee = Moe's restaurant
        var entries = journal.getEntriesByPayee(payee);
        System.out.println("\n--- Entries by payee = Moe's restaurant ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by date = 2023/03/07
        entries = journal.getEntriesBy(date);
        System.out.println("\n--- Entries by date = 2023/03/07 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, and date = 2023/03/07
        entries = journal.getEntriesByPayee(payee, date);
        System.out.println("\n--- Entries by payee = Moe's restaurant, and date = 2023/03/07 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // Methods that only filter entry information
        // ------------------------------------------

        // entries by accountName = Assets:Cash
        entries = journal.getEntriesByAccount(accountName);
        System.out.println("\n--- Entries by accountName = Assets:Cash ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by amount = 500
        entries = journal.getEntriesBy(amount);
        System.out.println("\n--- Entries by amount = 500 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by accountName = Assets:Cash, and amount = 500
        entries = journal.getEntriesByAccount(accountName, amount);
        System.out.println("\n--- Entries by accountName = Assets:Cash, and amount = 500 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // Methods that filter both transaction and entry information
        // ----------------------------------------------------------

        // entries by payee = Moe's restaurant, and accountName = Assets:Cash
        entries = journal.getEntriesByPayee(payee, accountName);
        System.out.println("\n--- Entries by payee = Moe's restaurant, and accountName = Assets:Cash ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, and accountName = Assets:Cash
        entries = journal.getEntriesByAccount(accountName, payee);
        System.out.println("\n--- Entries by accountName = Assets:Cash, and payee = Moe's restaurant ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, and amount = 500
        entries = journal.getEntriesByPayee(payee, amount);
        System.out.println("\n--- Entries by payee = Moe's restaurant, and amount = 500 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, and date = 2023/03/07
        entries = journal.getEntriesByAccount(accountName, date);
        System.out.println("\n--- Entries by accountName = Assets:Cash, and and date = 2023/03/07 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by amount = 500, and date = 2023/03/07
        entries = journal.getEntriesBy(amount, date);
        System.out.println("\n--- Entries by amount = 500, and date = 2023/03/07 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, accountName = Assets:Cash, and amount = -12
        amount = Money.of(CurrencyUnit.of(Locale.getDefault()), -12);
        entries = journal.getEntriesByPayee(payee, accountName, amount);
        System.out.println("\n--- Entries by payee = Moe's restaurant, accountName = Assets:Cash, and amount = -12 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, accountName = Assets:Cash, and amount = -12
        entries = journal.getEntriesByAccount(accountName, payee, amount);
        System.out.println("\n--- Entries by payee = Moe's restaurant, accountName = Assets:Cash, and amount = -12 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, accountName = Assets:Cash, and date = 2023/03/07
        entries = journal.getEntriesByPayee(payee, accountName, date);
        System.out.println("\n--- Entries by payee = Moe's restaurant, accountName = Assets:Cash, and date = 2023/03/07 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, accountName = Assets:Cash, and date = 2023/03/07
        entries = journal.getEntriesByAccount(accountName, payee, date);
        System.out.println("\n--- Entries by payee = Moe's restaurant, accountName = Assets:Cash, and date = 2023/03/07 ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by payee = Moe's restaurant, date = 2023/03/07, and amount = -12
        entries = journal.getEntriesByPayee(payee, date, amount);
        System.out.println("\n--- Entries by payee = Moe's restaurant, and accountName = Assets:Cash ---");
        for (var e : entries) {
            System.out.println(e);
        }

        // entries by accountName = Assets:Cash, date = 2023/03/07, and amount = -12
        entries = journal.getEntriesByAccount(accountName, date, amount);
        System.out.println("\n--- Entries by payee = Moe's restaurant, accountName = Assets:Cash, , and amount = -12 ---");
        for (var e : entries) {
            System.out.println(e);
        }
    }
}
