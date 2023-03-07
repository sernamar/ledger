package core;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a journal (a group of transactions).
 */

public class Journal {
    private final List<Transaction> transactions;

    public Journal() {
        transactions = new ArrayList<>();
    }

    public void addTransaction (Transaction transaction) {
        transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }
}
