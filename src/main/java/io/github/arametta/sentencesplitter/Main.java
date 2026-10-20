package io.github.arametta.sentencesplitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException {
        LOGGER.info("Started");

        OutputFormat format = readFormatArgument(args);
        LOGGER.info("Format selected: {}", format.argument());

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        OutputStream output = new BufferedOutputStream(System.out);

        int sentenceCount;
        try (SentenceWriter writer = format.createWriter(output)) {
            sentenceCount = new SentenceProcessor(new SentenceReader(input), writer).process();
        }

        output.flush();
        LOGGER.info("Sentences processed: {}", sentenceCount);
        LOGGER.info("Done");
    }

    private static OutputFormat readFormatArgument(String[] args) {
        if (args.length == 1) {
            Optional<OutputFormat> format = OutputFormat.parse(args[0]);
            if (format.isPresent()) {
                return format.get();
            }
        }
        System.err.println("Usage: java -jar sentence-splitter.jar <xml|csv> < input > output");
        System.exit(1);
        throw new IllegalStateException("unreachable");
    }
}
