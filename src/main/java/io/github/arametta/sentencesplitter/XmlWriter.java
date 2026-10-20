package io.github.arametta.sentencesplitter;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 * Streams Sentence objects out as XML, one sentence element at a time.
 *
 * XMLStreamException is wrapped in IOException, so callers of
 * SentenceWriter only have to handle one exception type.
 */
public class XmlWriter implements SentenceWriter {

    // XMLStreamWriter.writeStartDocument() has no overload that adds
    // standalone="yes", so the declaration is written by hand instead.
    private static final String XML_DECLARATION =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";

    private final OutputStream out;
    private final XMLStreamWriter writer;

    public XmlWriter(OutputStream out) throws IOException {
        this.out = out;
        out.write(XML_DECLARATION.getBytes(StandardCharsets.UTF_8));
        try {
            writer = XMLOutputFactory.newInstance().createXMLStreamWriter(out, "UTF-8");
            writer.writeStartElement("text");
            writer.writeCharacters("\n");
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    @Override
    public void write(Sentence sentence) throws IOException {
        try {
            writer.writeStartElement("sentence");
            for (String word : sentence.words()) {
                writeWord(word);
            }
            writer.writeEndElement();
            writer.writeCharacters("\n");
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    // writeCharacters() does not escape a plain apostrophe, but it is written
    // here as the &apos; entity instead, so the word is split on it and the
    // entity is inserted by hand between the pieces.
    private void writeWord(String word) throws XMLStreamException {
        writer.writeStartElement("word");
        String[] parts = word.split("'", -1);
        for (int i = 0; i < parts.length; i++) {
            if (i > 0) {
                writer.writeEntityRef("apos");
            }
            writer.writeCharacters(parts[i]);
        }
        writer.writeEndElement();
    }

    @Override
    public void close() throws IOException {
        try {
            writer.writeEndElement();
            writer.writeEndDocument();
            writer.flush();
            writer.close();
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
        out.flush();
    }
}
