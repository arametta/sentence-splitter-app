package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringReader;

import static org.assertj.core.api.Assertions.assertThat;

class SentenceReaderTest {

    @Test
    void splitsOnPeriodExclamationAndQuestionMark() throws IOException {
        SentenceReader reader = new SentenceReader(
                new StringReader("Hello world. Goodbye now! Are you there?"));

        assertThat(reader.readNext().words()).containsExactly("Hello", "world");
        assertThat(reader.readNext().words()).containsExactly("Goodbye", "now");
        assertThat(reader.readNext().words()).containsExactly("Are", "you", "there");
        assertThat(reader.readNext()).isNull();
    }

    @Test
    void abbreviationDotDoesNotEndSentenceAndKeepsTheDot() throws IOException {
        SentenceReader reader = new SentenceReader(new StringReader(
                "We waited while Dr. Novak checked the tickets - the train was late."));

        Sentence sentence = reader.readNext();

        assertThat(sentence.words()).containsExactly("We", "waited", "while", "Dr.", "Novak", "checked",
                "the", "tickets", "the", "train", "was", "late");
        assertThat(reader.readNext()).isNull();
    }

    @Test
    void decimalPointDoesNotEndSentenceAndStaysPartOfTheNumber() throws IOException {
        SentenceReader reader = new SentenceReader(new StringReader("The value is 3.14. Done."));

        assertThat(reader.readNext().words()).containsExactly("The", "value", "is", "3.14");
        assertThat(reader.readNext().words()).containsExactly("Done");
        assertThat(reader.readNext()).isNull();
    }
}
