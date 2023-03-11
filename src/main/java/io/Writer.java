package io;

import core.Journal;

import java.io.IOException;
import java.nio.file.Path;

public interface Writer {
    void writeJournal(Journal journal);
    void writeJournal(Journal journal, Path path) throws IOException;
}
