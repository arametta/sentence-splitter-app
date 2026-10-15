Sentence Splitter
=================

A small Java command-line tool. It reads text from stdin, splits it into
sentences and words, sorts the words of each sentence, and writes the
result to stdout as XML or CSV.

Words are sorted case-insensitively; when two words differ only by case,
the lowercase one comes first ("cat" before "Cat"). Contractions such as
"didn't" stay one word, a curly apostrophe is changed to a straight one,
and a decimal number such as "2.5" stays one word.

Input is streamed one sentence at a time, so large inputs run with a small
heap (CI checks this with -Xmx32m).

Build
-----
Requires JDK 17 or newer.

  ./mvnw package

This runs the tests and produces target/sentence-splitter.jar.

Run
---
XML output:
  java -jar target/sentence-splitter.jar xml < input.txt > output.xml

CSV output:
  java -jar target/sentence-splitter.jar csv < input.txt > output.csv

Log messages go to stderr, so they never mix with the output.

Try it with the sample input:
  java -jar target/sentence-splitter.jar xml < sample-files/small.in

Project layout
--------------
src/main/java/io/github/arametta/sentencesplitter/
  Main            reads the format argument and connects the parts
  SentenceReader  splits the input into sentences on ".", "!" and "?"
  WordTokenizer   extracts words and defines the sort order
  Sentence        one sentence's words (a record)
  XmlWriter       writes sentences as XML
  CsvWriter       writes sentences as CSV
src/test/java/...  unit tests and a golden-file test
sample-files/      small.in and its expected output, small.xml and small.csv
.github/workflows/ci.yml  runs the tests and a large-input, small-heap check

The golden-file test compares against output produced by this program, so
it protects against regressions and does not prove the rules by itself;
the unit tests do that.

Limitations
-----------
- A "." does not end a sentence only after a small, fixed list of
  abbreviations: Mr, Mrs, Ms, Dr, Prof, St, Sr, Jr. Any other
  abbreviation ends the sentence.
- Abbreviations with several periods, such as a.m. and p.m., are not
  recognized; each "." in them ends a sentence.
- Full-width punctuation, such as the Chinese full stop "。", does not
  end a sentence.
- Text with no ".", "!" or "?" at all is held in memory until the end of
  the input, so the small-heap guarantee does not apply to it.
