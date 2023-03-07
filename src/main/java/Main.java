import core.Account;
import core.Entry;
import core.Journal;
import core.Transaction;

import java.time.Instant;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        // First transaction
        var date = Instant.now();
        var note1 = "Moe's restaurant";

        var food = new Account("Expenses:Restaurant:Food");
        var tips = new Account("Expenses:Restaurant:Tips");
        var cash = new Account("Assets:Cash");
        var debitCard = new Account("Assets:Debit Card");

        var entry1 = new Entry(food, 20);
        var entry2 = new Entry(tips, 2);
        var entry3 = new Entry(cash, -12);
        var entry4 = new Entry(debitCard, -10);

        var entries1 = new ArrayList<Entry>();
        entries1.add(entry1);
        entries1.add(entry2);
        entries1.add(entry3);
        entries1.add(entry4);

        var transaction1 = new Transaction(date, note1, entries1);

        // second transaction
        var note2 = "Mike's convenience store";
        var groceries = new Account("Expenses:Groceries");
        var entry5 = new Entry(groceries, 35.95);
        var entry6 = new Entry(cash, -35.95);

        var entries2 = new ArrayList<Entry>();
        entries2.add(entry5);
        entries2.add(entry6);

        var transaction2 = new Transaction(date, note2, entries2);

        // journal
        var journal = new Journal();
        journal.addTransaction(transaction1);
        journal.addTransaction(transaction2);

        for (var t: journal.getTransactions()) {
            System.out.println(t);
        }

    }
}
