package io.github.arametta.sentencesplitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * A single sentence's words, in whatever order they were added.
 *
 * A record so that two sentences with the same words in the same order
 * (case-sensitive) are equal and hash the same, making Sentence usable as
 * a Map key that treats identical sentences as duplicates of one another.
 */
public record Sentence(List<String> words) {

    // Sort case-insensitively, but when two words only differ by case
    // (e.g. "cat" vs "Cat"), the lowercase spelling sorts first.
    public static final Comparator<String> WORD_ORDER =
            Comparator.comparing((String word) -> word.toLowerCase(Locale.ROOT))
                    .thenComparing(Comparator.reverseOrder());

    public Sentence {
        words = List.copyOf(words);
    }

    /**
     * Returns a new Sentence with the same words sorted by WORD_ORDER.
     * This sentence is not changed.
     */
    public Sentence sorted() {
        List<String> sortedWords = new ArrayList<>(words);
        sortedWords.sort(WORD_ORDER);
        return new Sentence(sortedWords);
    }
}
