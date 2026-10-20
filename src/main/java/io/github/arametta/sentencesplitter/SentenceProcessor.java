package io.github.arametta.sentencesplitter;

import java.io.IOException;

/**
 * Reads every sentence, sorts its words and passes it to the writer.
 *
 * Does not close the writer: whoever created it is responsible for that.
 */
public class SentenceProcessor {

    private final SentenceReader reader;
    private final SentenceWriter writer;

    public SentenceProcessor(SentenceReader reader, SentenceWriter writer) {
        this.reader = reader;
        this.writer = writer;
    }

    /** Processes all remaining sentences and returns how many there were. */
    public int process() throws IOException {
        int sentenceCount = 0;
        Sentence sentence;
        while ((sentence = reader.readNext()) != null) {
            writer.write(sentence.sorted());
            sentenceCount++;
        }
        return sentenceCount;
    }
}
