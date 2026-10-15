package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class WordTokenizerTest {

    @Test
    void extractsPlainWords() {
        assertThat(WordTokenizer.extractWords("hello world")).containsExactly("hello", "world");
    }

    @Test
    void keepsContractionWithStraightApostropheAsOneWord() {
        assertThat(WordTokenizer.extractWords("couldn't")).containsExactly("couldn't");
    }

    @Test
    void keepsContractionWithCurlyApostropheAsOneWordAndNormalizesToStraightApostrophe() {
        assertThat(WordTokenizer.extractWords("you’d")).containsExactly("you'd");
    }

    @Test
    void keepsRunOfChineseCharactersAsOneWord() {
        assertThat(WordTokenizer.extractWords("北京火车站")).containsExactly("北京火车站");
    }

    @Test
    void splitsWordsGluedToPunctuationWithNoSpace() {
        assertThat(WordTokenizer.extractWords("tea,coffee")).containsExactly("tea", "coffee");
    }

    @Test
    void keepsDecimalNumberAsOneWord() {
        assertThat(WordTokenizer.extractWords("temperature is 3.14 degrees"))
                .containsExactly("temperature", "is", "3.14", "degrees");
    }

    @Test
    void doesNotMergeADotNotFollowedByADigit() {
        assertThat(WordTokenizer.extractWords("5. Next")).containsExactly("5", "Next");
    }

    @Test
    void wordOrderSortsLowercaseBeforeSameWordDifferentCase() {
        assertThat(WordTokenizer.WORD_ORDER.compare("cat", "Cat")).isNegative();
    }

    @Test
    void wordOrderSortsInBeforeCapitalIn() {
        assertThat(WordTokenizer.WORD_ORDER.compare("in", "In")).isNegative();
    }
}
