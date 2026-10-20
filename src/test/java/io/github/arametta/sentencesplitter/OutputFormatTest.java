package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

class OutputFormatTest {

    @Test
    void parsesXmlArgument() {
        assertThat(OutputFormat.parse("xml")).contains(OutputFormat.XML);
    }

    @Test
    void parsesCsvArgument() {
        assertThat(OutputFormat.parse("csv")).contains(OutputFormat.CSV);
    }

    @ParameterizedTest
    @ValueSource(strings = {"pdf", "XML", "", " xml"})
    void rejectsUnknownArgument(String argument) {
        assertThat(OutputFormat.parse(argument)).isEmpty();
    }

    @Test
    void xmlFormatCreatesXmlWriter() throws IOException {
        try (SentenceWriter writer = OutputFormat.XML.createWriter(new ByteArrayOutputStream())) {
            assertThat(writer).isInstanceOf(XmlWriter.class);
        }
    }

    @Test
    void csvFormatCreatesCsvWriter() throws IOException {
        try (SentenceWriter writer = OutputFormat.CSV.createWriter(new ByteArrayOutputStream())) {
            assertThat(writer).isInstanceOf(CsvWriter.class);
        }
    }
}
