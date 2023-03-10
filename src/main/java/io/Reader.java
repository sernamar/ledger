package io;

import core.Ledger;

import java.nio.file.Path;

public interface Reader {
    Ledger readJournal(Path filePath);
}
