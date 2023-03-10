package core;

import java.util.List;
import java.util.Set;

public class Ledger {
    private final ChartOfAccounts accounts;
    private final Journal journal;

    public Ledger(ChartOfAccounts accounts, Journal journal) {
        this.accounts = accounts;
        this.journal = journal;
    }

    public Set<Account> getAccounts() {
        return accounts.getAccounts();
    }

    public Journal getJournal() {
        return journal;
    }

    public List<Transaction> getTransactions() {
        return journal.getTransactions();
    }
}
