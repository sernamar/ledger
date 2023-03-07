package core;

import java.time.Instant;
import java.util.List;

/**
 * Represents a transaction (a group of entries).
 */

public record Transaction (Instant date, String note, List<Entry> entries){
}
