package core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ChartOfAccountsTest {

    @Test
    void addAccount() {
        // add new account
        var chartOfAccounts = new ChartOfAccounts();
        var result1 = chartOfAccounts.addAccount(new Account("Assets"));
        assertTrue(result1);

        // add existing account
        var result2 = chartOfAccounts.addAccount(new Account("Assets"));
        assertFalse(result2);
    }

    @Test
    void hasAccount() {
        var chartOfAccounts = new ChartOfAccounts();
        chartOfAccounts.addAccount(new Account("Assets"));

        // existing account
        var existingAccount = new Account("Assets");
        var result1 = chartOfAccounts.hasAccount(existingAccount);
        assertTrue(result1);

        // new account
        var newAccount = new Account("Liabilities");
        var result2 = chartOfAccounts.hasAccount(newAccount);
        assertFalse(result2);
    }
}