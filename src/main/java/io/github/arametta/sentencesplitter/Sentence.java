package io.github.arametta.sentencesplitter;

import java.util.List;

/**
 * A single sentence's words, in whatever order they were added.
 *
 * A record so that two sentences with the same words in the same order
 * (case-sensitive) are equal and hash the same, making Sentence usable as
 * a Map key that treats identical sentences as duplicates of one another.
 */
public record Sentence(List<String> words) {

    public Sentence {
        words = List.copyOf(words);
    }
}
