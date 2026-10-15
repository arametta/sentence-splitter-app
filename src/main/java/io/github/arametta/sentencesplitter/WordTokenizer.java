package io.github.arametta.sentencesplitter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Extracts word tokens out of a block of text.
 */
public final class WordTokenizer {

    // One or more letters/digits, optionally continuing through an apostrophe
    // (e.g. "you'd") or a decimal point immediately followed by more digits
    // (e.g. "3.14"), so a contraction or decimal number stays a single word.
    private static final Pattern WORD_PATTERN =
            Pattern.compile("[\\p{L}\\p{Nd}]+(?:['’][\\p{L}\\p{Nd}]+|\\.\\p{Nd}+)*");

    // Sort case-insensitively, but when two words only differ by case
    // (e.g. "cat" vs "Cat"), the lowercase spelling sorts first.
    public static final Comparator<String> WORD_ORDER =
            Comparator.comparing((String word) -> word.toLowerCase(Locale.ROOT))
                    .thenComparing(Comparator.reverseOrder());

    private WordTokenizer() {
        // Not instantiated; extractWords() is called directly on the class.
    }

    public static List<String> extractWords(String text) {
        List<String> words = new ArrayList<>();
        Matcher matcher = WORD_PATTERN.matcher(text);
        while (matcher.find()) {
            words.add(normalizeApostrophe(matcher.group()));
        }
        return words;
    }

    // The regex accepts a curly apostrophe so a contraction like "you’d"
    // stays one token, but the word is then normalized to a straight
    // apostrophe so callers only ever have to handle one quote character.
    private static String normalizeApostrophe(String word) {
        return word.replace('’', '\'');
    }
}
