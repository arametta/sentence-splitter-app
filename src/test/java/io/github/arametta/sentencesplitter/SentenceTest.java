package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SentenceTest {

    @Test
    void sentencesWithSameWordsInSameOrderAreEqualAndHashTheSame() {
        Sentence first = new Sentence(List.of("Anna", "fed", "the", "cat"));
        Sentence second = new Sentence(List.of("Anna", "fed", "the", "cat"));

        assertThat(first).isEqualTo(second);
        assertThat(first.hashCode()).isEqualTo(second.hashCode());
    }

    @Test
    void sentencesWithDifferentWordsAreNotEqual() {
        Sentence first = new Sentence(List.of("Anna", "fed", "the", "cat"));
        Sentence second = new Sentence(List.of("Anna", "fed", "the", "dog"));

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void sentencesWithSameWordsInDifferentOrderAreNotEqual() {
        Sentence first = new Sentence(List.of("cat", "fed", "little", "the", "Anna"));
        Sentence second = new Sentence(List.of("Anna", "fed", "the", "little", "cat"));

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void sentencesDifferingOnlyByWordCaseAreNotEqual() {
        Sentence first = new Sentence(List.of("Cat"));
        Sentence second = new Sentence(List.of("cat"));

        assertThat(first).isNotEqualTo(second);
    }

    @Test
    void mutatingTheListPassedToTheConstructorDoesNotAffectTheSentence() {
        List<String> words = new ArrayList<>(List.of("Anna", "fed", "the", "cat"));
        Sentence sentence = new Sentence(words);

        words.add("today");

        assertThat(sentence.words()).containsExactly("Anna", "fed", "the", "cat");
    }

    @Test
    void wordsListIsImmutable() {
        Sentence sentence = new Sentence(List.of("Anna", "fed", "the", "cat"));

        assertThatThrownBy(() -> sentence.words().add("today"))
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void wordOrderSortsLowercaseBeforeSameWordDifferentCase() {
        assertThat(Sentence.WORD_ORDER.compare("cat", "Cat")).isNegative();
    }

    @Test
    void wordOrderSortsInBeforeCapitalIn() {
        assertThat(Sentence.WORD_ORDER.compare("in", "In")).isNegative();
    }

    @Test
    void sortedReturnsWordsCaseInsensitivelyWithLowercaseFirst() {
        Sentence sentence = new Sentence(List.of("the", "Cat", "Anna", "cat", "fed"));

        assertThat(sentence.sorted().words()).containsExactly("Anna", "cat", "Cat", "fed", "the");
    }

    @Test
    void sortedDoesNotChangeTheOriginalSentence() {
        Sentence sentence = new Sentence(List.of("the", "cat", "Anna"));

        sentence.sorted();

        assertThat(sentence.words()).containsExactly("the", "cat", "Anna");
    }

    @Test
    void sortedEmptySentenceIsEmpty() {
        assertThat(new Sentence(List.of()).sorted().words()).isEmpty();
    }
}
