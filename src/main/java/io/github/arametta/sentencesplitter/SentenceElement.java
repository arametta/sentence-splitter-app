package io.github.arametta.sentencesplitter;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.List;

/**
 * JAXB model of one sentence element: {@code <sentence><word>...</word></sentence>}.
 */
@XmlRootElement(name = "sentence")
@XmlAccessorType(XmlAccessType.FIELD)
class SentenceElement {

    @XmlElement(name = "word")
    private List<String> words;

    // JAXB needs a no-argument constructor.
    SentenceElement() {
    }

    SentenceElement(Sentence sentence) {
        this.words = sentence.words();
    }
}
