package core;

import java.util.HashSet;
import java.util.Set;

public class ChartOfAccounts {
    private final Set<Account> accounts;

    public ChartOfAccounts() {
        accounts = new HashSet<>();
    }

    public int size() {
        return accounts.size();
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public Set<Account> getAccounts() {
        return accounts;
    }

    public boolean addAccount(Account account) {
        return accounts.add(account);
    }

    public Account addAccount(String accountName) {
        var account = new Account(accountName);
        addAccount(account);
        return account;
    }

    public boolean contains(Account account) {
        return accounts.contains(account);
    }
}
