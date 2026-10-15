package io.github.arametta.sentencesplitter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Streams Sentence objects out as CSV.
 *
 * The header row needs the maximum word count across the whole document,
 * which is not known until the input ends. Each sentence's row is written
 * to a temporary file as it arrives, tracking that maximum along the way;
 * once the input is exhausted, the header is written first, followed by
 * the rows spooled back in from the temporary file.
 *
 * No word ever contains a comma, quote or newline (WordTokenizer's regex
 * cannot produce one), so rows are joined with ", " directly, no CSV
 * quoting is needed.
 */
public class CsvWriter implements AutoCloseable {

    private final OutputStream out;
    private final Path tempFile;
    private final Writer tempWriter;
    private int sentenceNumber = 0;
    private int maxWords = 0;

    public CsvWriter(OutputStream out) throws IOException {
        this.out = out;
        this.tempFile = Files.createTempFile("sentence-splitter", ".csv");
        this.tempWriter = new BufferedWriter(
                new OutputStreamWriter(Files.newOutputStream(tempFile), StandardCharsets.UTF_8));
    }

    // Package-private on purpose: exists only so tests can verify the temp
    // file is cleaned up, not part of the public API.
    Path tempFilePath() {
        return tempFile;
    }

    public void write(Sentence sentence) throws IOException {
        sentenceNumber++;
        List<String> words = sentence.words();
        maxWords = Math.max(maxWords, words.size());

        List<String> fields = new ArrayList<>();
        fields.add("Sentence " + sentenceNumber);
        fields.addAll(words);

        tempWriter.write(buildRow(fields));
        tempWriter.write("\n");
    }

    private String buildRow(List<String> fields) {
        return String.join(", ", fields);
    }

    @Override
    public void close() throws IOException {
        tempWriter.flush();
        tempWriter.close();

        try {
            writeHeader();
            streamSpooledRows();
            out.flush();
        } catch (IOException writeFailure) {
            deleteTempFileSuppressingFailure(writeFailure);
            throw writeFailure;
        }

        Files.delete(tempFile);
    }

    // If writing the real output fails partway (e.g. a broken pipe), the
    // temp file must still be cleaned up. If the delete itself then also
    // fails, the original write failure is what the caller needs to see,
    // so the delete failure is attached to it rather than replacing it.
    private void deleteTempFileSuppressingFailure(IOException originalFailure) {
        try {
            Files.delete(tempFile);
        } catch (IOException deleteFailure) {
            originalFailure.addSuppressed(deleteFailure);
        }
    }

    private void writeHeader() throws IOException {
        List<String> fields = new ArrayList<>();
        fields.add("");
        for (int wordIndex = 1; wordIndex <= maxWords; wordIndex++) {
            fields.add("Word " + wordIndex);
        }
        writeLine(buildRow(fields));
    }

    private void streamSpooledRows() throws IOException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Files.newInputStream(tempFile), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                writeLine(line);
            }
        }
    }

    private void writeLine(String line) throws IOException {
        out.write((line + "\n").getBytes(StandardCharsets.UTF_8));
    }
}
