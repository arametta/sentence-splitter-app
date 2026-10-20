# Sentence Splitter

A small Java command-line tool. It reads text from stdin, splits it into
sentences and words, sorts the words of each sentence, and writes the
result to stdout as XML or CSV.

- Words are sorted case-insensitively; when two words differ only by case,
  the lowercase one comes first (`cat` before `Cat`).
- Contractions such as `didn't` stay one word, and a curly apostrophe is
  changed to a straight one.
- A decimal number such as `2.5` stays one word.
- Input is streamed one sentence at a time, so large inputs run with a
  small heap (CI checks this with `-Xmx32m`).

## Build

Requires JDK 17 or newer.

```sh
./mvnw package
```

This runs the tests and produces `target/sentence-splitter.jar`.

## Run

XML output:

```sh
java -jar target/sentence-splitter.jar xml < input.txt > output.xml
```

CSV output:

```sh
java -jar target/sentence-splitter.jar csv < input.txt > output.csv
```

Log messages go to stderr, so they never mix with the output.

### Example

Input:

```text
Dr. Rossi met us at the station.
```

XML output:

```xml
<?xml version="1.0" encoding="UTF-8" standalone="yes"?>
<text>
<sentence><word>at</word><word>Dr.</word><word>met</word><word>Rossi</word><word>station</word><word>the</word><word>us</word></sentence>
</text>
```

CSV output:

```text
, Word 1, Word 2, Word 3, Word 4, Word 5, Word 6, Word 7
Sentence 1, at, Dr., met, Rossi, station, the, us
```

A longer sample is in `sample-files/small.in`, with its output in
`small.xml` and `small.csv`.

## Project layout

```text
src/main/java/io/github/arametta/sentencesplitter/
  Main               reads the format argument and connects the parts
  OutputFormat       the supported formats (an enum); creates the writer for each
  SentenceReader     splits the input into sentences on ".", "!" and "?"
  WordTokenizer      extracts the words from a piece of text
  Sentence           one sentence's words (a record) and the sort order
  SentenceProcessor  reads each sentence, sorts it and passes it to the writer
  SentenceWriter     the interface both writers implement
  XmlWriter          writes sentences as XML, using JAXB
  SentenceElement    the JAXB model of one sentence element
  CsvWriter          writes sentences as CSV
src/test/java/...    unit tests and a golden-file test
sample-files/        small.in and its expected output, small.xml and small.csv
.github/workflows/ci.yml   runs the tests and a large-input, small-heap check
```

The golden-file test compares against output produced by this program, so
it protects against regressions and does not prove the rules by itself;
the unit tests do that.

## Design notes

- `XmlWriter` and `CsvWriter` implement one interface, `SentenceWriter`, so
  `SentenceProcessor` and `Main` never depend on a specific format.
- `OutputFormat` parses the argument and creates the matching writer, so the
  list of formats is kept in one place.
- `Sentence` is an immutable record: `sorted()` returns a new sentence.
- XML is written with JAXB, one sentence fragment at a time, so the document
  is never held in memory. A small escape handler writes `'` as `&apos;`.
- CSV is joined by hand: no word can contain a comma, quote or newline, so a
  CSV library would have no quoting to do.
- `WordTokenizer` stays a static method: it has no state and one rule, so an
  interface would add wiring with no second implementation to use it.
- `SentenceReader` does not implement `Iterator`, because reading can throw
  `IOException` and `Iterator` methods cannot.

## Limitations

- A `.` does not end a sentence only after a small, fixed list of
  abbreviations: Mr, Mrs, Ms, Dr, Prof, St, Sr, Jr. Any other abbreviation
  ends the sentence.
- Abbreviations with several periods, such as `a.m.` and `p.m.`, are not
  recognized; each `.` in them ends a sentence.
- Full-width punctuation, such as the Chinese full stop `。`, does not end
  a sentence.
- Text with no `.`, `!` or `?` at all is held in memory until the end of
  the input, so the small-heap guarantee does not apply to it.
