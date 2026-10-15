package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class XmlWriterTest {

    @Test
    void writesWordContainingApostropheAsAposEntity() throws Exception {
        Sentence sentence = new Sentence(List.of("couldn't"));

        String xml = writeToString(sentence);

        assertThat(xml).contains("<word>couldn&apos;t</word>");
    }

    @Test
    void writesSentenceWithNoWordsAsEmptyElement() throws Exception {
        Sentence sentence = new Sentence(List.of());

        String xml = writeToString(sentence);

        assertThat(xml).contains("<sentence></sentence>");
    }

    @Test
    void writesXmlDeclarationWithStandaloneYes() throws Exception {
        Sentence sentence = new Sentence(List.of("hello"));

        String xml = writeToString(sentence);

        assertThat(xml).startsWith("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n");
    }

    @Test
    void writesMultipleSentencesAsSeparateElements() throws Exception {
        Sentence first = new Sentence(List.of("hello"));
        Sentence second = new Sentence(List.of("world"));

        String xml = writeToString(first, second);

        assertThat(xml).contains("<sentence><word>hello</word></sentence>",
                "<sentence><word>world</word></sentence>");
    }

    private String writeToString(Sentence... sentences) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (XmlWriter writer = new XmlWriter(out)) {
            for (Sentence sentence : sentences) {
                writer.write(sentence);
            }
        }
        return out.toString(StandardCharsets.UTF_8);
    }
}
