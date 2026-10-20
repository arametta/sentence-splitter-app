package io.github.arametta.sentencesplitter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs each SentenceWriter implementation through the interface only.
 * Format details are covered by XmlWriterTest and CsvWriterTest.
 */
class SentenceWriterTest {

    @ParameterizedTest
    @ValueSource(strings = {"xml", "csv"})
    void writesEveryWordOfTheSentence(String format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (SentenceWriter writer = createWriter(format, out)) {
            writer.write(new Sentence(List.of("hello", "world")));
        }

        assertThat(out.toString(StandardCharsets.UTF_8)).contains("hello", "world");
    }

    private SentenceWriter createWriter(String format, OutputStream out) throws IOException {
        if (format.equals("xml")) {
            return new XmlWriter(out);
        }
        return new CsvWriter(out);
    }
}
