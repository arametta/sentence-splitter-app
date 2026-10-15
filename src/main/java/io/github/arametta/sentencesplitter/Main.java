package io.github.arametta.sentencesplitter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.stream.XMLStreamException;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws IOException, XMLStreamException {
        LOGGER.info("Started");

        String format = readFormatArgument(args);
        LOGGER.info("Format selected: {}", format);

        BufferedReader input = new BufferedReader(new InputStreamReader(System.in, StandardCharsets.UTF_8));
        OutputStream output = new BufferedOutputStream(System.out);

        int sentenceCount = format.equals("xml")
                ? runWithXmlWriter(input, output)
                : runWithCsvWriter(input, output);

        output.flush();
        LOGGER.info("Sentences processed: {}", sentenceCount);
        LOGGER.info("Done");
    }

    private static String readFormatArgument(String[] args) {
        if (args.length == 1 && (args[0].equals("xml") || args[0].equals("csv"))) {
            return args[0];
        }
        System.err.println("Usage: java -jar sentence-splitter.jar <xml|csv> < input > output");
        System.exit(1);
        throw new IllegalStateException("unreachable");
    }

    private static int runWithXmlWriter(BufferedReader input, OutputStream output)
            throws IOException, XMLStreamException {
        SentenceReader sentenceReader = new SentenceReader(input);
        int sentenceCount = 0;
        try (XmlWriter writer = new XmlWriter(output)) {
            Sentence sentence;
            while ((sentence = sentenceReader.readNext()) != null) {
                writer.write(sortWords(sentence));
                sentenceCount++;
            }
        }
        return sentenceCount;
    }

    private static int runWithCsvWriter(BufferedReader input, OutputStream output) throws IOException {
        SentenceReader sentenceReader = new SentenceReader(input);
        int sentenceCount = 0;
        try (CsvWriter writer = new CsvWriter(output)) {
            Sentence sentence;
            while ((sentence = sentenceReader.readNext()) != null) {
                writer.write(sortWords(sentence));
                sentenceCount++;
            }
        }
        return sentenceCount;
    }

    private static Sentence sortWords(Sentence sentence) {
        List<String> words = new ArrayList<>(sentence.words());
        words.sort(WordTokenizer.WORD_ORDER);
        return new Sentence(words);
    }
}
