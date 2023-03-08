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

    public boolean hasAccount(Account account) {
        return accounts.contains(account);
    }
}
