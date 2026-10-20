package io.github.arametta.sentencesplitter;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import org.glassfish.jaxb.core.marshaller.CharacterEscapeHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

/**
 * Streams Sentence objects out as XML, one sentence element at a time.
 *
 * Each sentence is marshalled by JAXB as a separate fragment, so the whole
 * document is never held in memory. The XML declaration and the enclosing
 * text element are written directly, because a fragment has neither.
 *
 * JAXBException is wrapped in IOException, so callers of SentenceWriter
 * only have to handle one exception type.
 */
public class XmlWriter implements SentenceWriter {

    private static final String XML_DECLARATION =
            "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n";

    // Specific to the GlassFish JAXB runtime; the JAXB standard has no way
    // to change how text is escaped.
    private static final String ESCAPE_HANDLER_PROPERTY = "org.glassfish.jaxb.characterEscapeHandler";

    private final OutputStream out;
    private final Marshaller marshaller;

    public XmlWriter(OutputStream out) throws IOException {
        this.out = out;
        try {
            marshaller = JAXBContext.newInstance(SentenceElement.class).createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
            marshaller.setProperty(ESCAPE_HANDLER_PROPERTY, (CharacterEscapeHandler) XmlWriter::escape);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
        writeText(XML_DECLARATION + "<text>\n");
    }

    @Override
    public void write(Sentence sentence) throws IOException {
        try {
            marshaller.marshal(new SentenceElement(sentence), out);
        } catch (JAXBException e) {
            throw new IOException(e);
        }
        writeText("\n");
    }

    @Override
    public void close() throws IOException {
        writeText("</text>");
        out.flush();
    }

    private void writeText(String text) throws IOException {
        out.write(text.getBytes(StandardCharsets.UTF_8));
    }

    // JAXB's default escaping leaves a plain apostrophe as it is, but the
    // output writes it as the &apos; entity. The other three characters are
    // escaped as usual.
    private static void escape(char[] chars, int start, int length, boolean isAttribute, Writer out)
            throws IOException {
        for (int i = start; i < start + length; i++) {
            switch (chars[i]) {
                case '&' -> out.write("&amp;");
                case '<' -> out.write("&lt;");
                case '>' -> out.write("&gt;");
                case '\'' -> out.write("&apos;");
                default -> out.write(chars[i]);
            }
        }
    }
}
