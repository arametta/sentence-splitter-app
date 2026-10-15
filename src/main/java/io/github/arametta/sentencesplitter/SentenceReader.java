package io.github.arametta.sentencesplitter;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Reads characters from a Reader and splits them into Sentence objects.
 *
 * Reads one character at a time and only ever buffers the text seen since
 * the last recognized sentence boundary, so the full input is never held
 * in memory at once.
 */
public class SentenceReader {

    // Heuristic, not full sentence-boundary detection: a trailing "." after
    // one of these (case-insensitive) does not end the sentence.
    private static final Set<String> ABBREVIATIONS =
            Set.of("mr", "mrs", "ms", "dr", "prof", "st", "sr", "jr");

    private final PushbackReader input;
    private final StringBuilder pendingText = new StringBuilder();
    private final List<String> currentWords = new ArrayList<>();
    private boolean finished = false;

    public SentenceReader(Reader input) {
        this.input = new PushbackReader(input, 1);
    }

    /**
     * Returns the next Sentence, or null once the input is exhausted.
     */
    public Sentence readNext() throws IOException {
        if (finished) {
            return null;
        }

        int nextChar;
        while ((nextChar = input.read()) != -1) {
            char character = (char) nextChar;

            if (isSentenceTerminator(character)) {
                if (character == '.' && isDecimalPoint()) {
                    pendingText.append(character);
                    continue;
                }

                Sentence sentence = handleTerminator(character);
                if (sentence != null) {
                    return sentence;
                }
                continue;
            }

            pendingText.append(character);
        }

        finished = true;
        return buildRemainingSentence();
    }

    private boolean isSentenceTerminator(char character) {
        return character == '.' || character == '!' || character == '?';
    }

    // A "." between two digits (e.g. "3.14") is a decimal point, not a
    // sentence boundary. Peeks one character ahead and pushes it back
    // immediately, so the lookahead never consumes input it doesn't use.
    private boolean isDecimalPoint() throws IOException {
        if (pendingText.length() == 0 || !Character.isDigit(pendingText.charAt(pendingText.length() - 1))) {
            return false;
        }

        int next = input.read();
        if (next != -1) {
            input.unread(next);
        }
        return next != -1 && Character.isDigit((char) next);
    }

    /**
     * Handles one ".", "!" or "?" character. Returns the finished Sentence
     * if this character genuinely ends one, or null if the sentence
     * continues (an abbreviation exception, or nothing accumulated yet).
     */
    private Sentence handleTerminator(char terminator) {
        flushPendingWords();

        if (terminator == '.' && lastWordIsAbbreviation()) {
            appendDotToLastWord();
            return null;
        }

        if (currentWords.isEmpty()) {
            return null;
        }

        return finalizeSentence();
    }

    private void flushPendingWords() {
        if (pendingText.length() == 0) {
            return;
        }
        currentWords.addAll(WordTokenizer.extractWords(pendingText.toString()));
        pendingText.setLength(0);
    }

    private boolean lastWordIsAbbreviation() {
        if (currentWords.isEmpty()) {
            return false;
        }
        String lastWord = currentWords.get(currentWords.size() - 1);
        return ABBREVIATIONS.contains(lastWord.toLowerCase(Locale.ROOT));
    }

    private void appendDotToLastWord() {
        int lastIndex = currentWords.size() - 1;
        currentWords.set(lastIndex, currentWords.get(lastIndex) + ".");
    }

    private Sentence finalizeSentence() {
        Sentence sentence = new Sentence(currentWords);
        currentWords.clear();
        return sentence;
    }

    private Sentence buildRemainingSentence() {
        flushPendingWords();
        if (currentWords.isEmpty()) {
            return null;
        }
        return finalizeSentence();
    }
}
