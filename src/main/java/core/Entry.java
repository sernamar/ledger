package core;

import org.joda.money.Money;

/**
 * Represents a transaction entry.
 */

public record Entry(Account account, Money amount) { }
