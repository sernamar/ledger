package core;

import java.math.BigDecimal;

/**
 * Represents a transaction entry.
 */

public record Entry(Account account, BigDecimal amount) {

    public Entry(Account account, double amount) {
        this(account, BigDecimal.valueOf(amount)); // calls the (implicit) canonical constructor
    }
}
