package io;

import core.Journal;

import java.nio.file.Path;

public interface Writer {
    void writeJournal(Journal journal);
    void writeJournal(Journal journal, Path path);
}
