package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SentenceProcessorTest {

    @Test
    void writesEachSentenceSortedAndReturnsTheCount() throws IOException {
        CollectingWriter writer = new CollectingWriter();

        int count = process("b a. d c.", writer);

        assertThat(count).isEqualTo(2);
        assertThat(writer.sentences).containsExactly(
                new Sentence(List.of("a", "b")),
                new Sentence(List.of("c", "d")));
    }

    @Test
    void emptyInputWritesNothingAndReturnsZero() throws IOException {
        CollectingWriter writer = new CollectingWriter();

        int count = process("", writer);

        assertThat(count).isZero();
        assertThat(writer.sentences).isEmpty();
    }

    private int process(String text, SentenceWriter writer) throws IOException {
        SentenceReader reader = new SentenceReader(new StringReader(text));
        return new SentenceProcessor(reader, writer).process();
    }

    // Keeps every sentence it receives, so the test can inspect them.
    private static class CollectingWriter implements SentenceWriter {
        final List<Sentence> sentences = new ArrayList<>();

        @Override
        public void write(Sentence sentence) {
            sentences.add(sentence);
        }

        @Override
        public void close() {
        }
    }
}
