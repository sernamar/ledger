package core;

import java.time.LocalDate;
import java.util.List;

/**
 * Represents a transaction (a group of entries).
 */

public record Transaction (LocalDate date, TransactionStatus status, String payee, List<Entry> entries){
}
