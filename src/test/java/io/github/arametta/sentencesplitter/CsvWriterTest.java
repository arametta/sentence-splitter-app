package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CsvWriterTest {

    @Test
    void headerReflectsMaxWordCountEvenWhenLongestSentenceIsWrittenLast() throws IOException {
        Sentence shortSentence = sentenceOf("alpha", "beta");
        Sentence longSentence = sentenceOf("one", "two", "three", "four", "five");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.write(shortSentence);
            writer.write(longSentence);
        }
        String[] lines = out.toString(StandardCharsets.UTF_8).split("\n", -1);

        assertThat(lines[0]).isEqualTo(", Word 1, Word 2, Word 3, Word 4, Word 5");
    }

    @Test
    void shortSentenceRowIsNotPaddedToHeaderWidth() throws IOException {
        Sentence longSentence = sentenceOf("one", "two", "three", "four", "five");
        Sentence shortSentence = sentenceOf("alpha", "beta");

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.write(longSentence);
            writer.write(shortSentence);
        }
        String[] lines = out.toString(StandardCharsets.UTF_8).split("\n", -1);

        assertThat(lines[2]).isEqualTo("Sentence 2, alpha, beta");
    }

    @Test
    void rowsAreJoinedWithCommaSpaceIncludingLeadingEmptyHeaderField() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (CsvWriter writer = new CsvWriter(out)) {
            writer.write(sentenceOf("alpha", "beta"));
        }

        assertThat(out.toString(StandardCharsets.UTF_8))
                .isEqualTo(", Word 1, Word 2\nSentence 1, alpha, beta\n");
    }

    @Test
    void tempFileIsDeletedAfterSuccessfulClose() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CsvWriter writer = new CsvWriter(out);
        writer.write(sentenceOf("alpha"));
        Path tempFile = writer.tempFilePath();
        assertThat(tempFile).exists();

        writer.close();

        assertThat(tempFile).doesNotExist();
    }

    @Test
    void tempFileIsDeletedEvenWhenWritingRealOutputFails() throws IOException {
        CsvWriter writer = new CsvWriter(new AlwaysFailingOutputStream());
        writer.write(sentenceOf("alpha"));
        Path tempFile = writer.tempFilePath();
        assertThat(tempFile).exists();

        assertThatThrownBy(writer::close).isInstanceOf(IOException.class);

        assertThat(tempFile).doesNotExist();
    }

    private Sentence sentenceOf(String... words) {
        return new Sentence(List.of(words));
    }

    private static class AlwaysFailingOutputStream extends OutputStream {
        @Override
        public void write(int b) throws IOException {
            throw new IOException("simulated broken pipe");
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            throw new IOException("simulated broken pipe");
        }
    }
}
