package io.github.arametta.sentencesplitter;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs each SentenceWriter implementation through the interface only.
 * Format details are covered by XmlWriterTest and CsvWriterTest.
 */
class SentenceWriterTest {

    @ParameterizedTest
    @EnumSource(OutputFormat.class)
    void writesEveryWordOfTheSentence(OutputFormat format) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (SentenceWriter writer = format.createWriter(out)) {
            writer.write(new Sentence(List.of("hello", "world")));
        }

        assertThat(out.toString(StandardCharsets.UTF_8)).contains("hello", "world");
    }
}
