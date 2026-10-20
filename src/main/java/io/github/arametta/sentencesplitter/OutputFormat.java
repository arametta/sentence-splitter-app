package io.github.arametta.sentencesplitter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Optional;

/**
 * The output formats the program supports, and the writer for each.
 */
public enum OutputFormat {
    XML("xml"),
    CSV("csv");

    private final String argument;

    OutputFormat(String argument) {
        this.argument = argument;
    }

    /** The command-line argument that selects this format. */
    public String argument() {
        return argument;
    }

    /**
     * Returns the format whose argument matches exactly (case-sensitive),
     * or an empty Optional if there is none.
     */
    public static Optional<OutputFormat> parse(String argument) {
        for (OutputFormat format : values()) {
            if (format.argument.equals(argument)) {
                return Optional.of(format);
            }
        }
        return Optional.empty();
    }

    public SentenceWriter createWriter(OutputStream out) throws IOException {
        return switch (this) {
            case XML -> new XmlWriter(out);
            case CSV -> new CsvWriter(out);
        };
    }
}
