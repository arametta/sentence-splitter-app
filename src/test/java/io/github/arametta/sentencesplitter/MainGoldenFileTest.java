package io.github.arametta.sentencesplitter;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Runs Main end to end against small.in and checks the output matches
 * small.xml / small.csv byte for byte, in both output formats.
 */
class MainGoldenFileTest {

    @Test
    void producesXmlMatchingSmallXmlByteForByte() throws Exception {
        byte[] actual = runMain("xml");
        byte[] expected = readResource("/small.xml");
        assertThat(actual).containsExactly(expected);
    }

    @Test
    void producesCsvMatchingSmallCsvByteForByte() throws Exception {
        byte[] actual = runMain("csv");
        byte[] expected = readResource("/small.csv");
        assertThat(actual).containsExactly(expected);
    }

    private byte[] runMain(String format) throws Exception {
        InputStream originalIn = System.in;
        PrintStream originalOut = System.out;
        ByteArrayOutputStream capturedOut = new ByteArrayOutputStream();
        try {
            System.setIn(new ByteArrayInputStream(readResource("/small.in")));
            System.setOut(new PrintStream(capturedOut, false, "UTF-8"));
            Main.main(new String[] {format});
            System.out.flush();
        } finally {
            System.setIn(originalIn);
            System.setOut(originalOut);
        }
        return capturedOut.toByteArray();
    }

    private byte[] readResource(String path) throws Exception {
        try (InputStream in = MainGoldenFileTest.class.getResourceAsStream(path)) {
            return in.readAllBytes();
        }
    }
}
