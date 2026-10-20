package io.github.arametta.sentencesplitter;

import java.io.IOException;

/**
 * Writes Sentence objects to an output in one format. close() finishes the
 * document, so nothing is complete until it has been called.
 */
public interface SentenceWriter extends AutoCloseable {

    void write(Sentence sentence) throws IOException;

    @Override
    void close() throws IOException;
}
