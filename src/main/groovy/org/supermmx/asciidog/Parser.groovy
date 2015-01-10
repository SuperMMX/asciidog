package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

class Parser {
    static final def SECTION_PATTERN = ~'''(?x)
(={1,6})         # 1, section identifier
\\p{Blank}+
(                # 2, whole title
  \\S+
  (?:\\p{Blank}+\\S+)*
)
\\s*
'''

    Reader reader

    Document parseString(String content) {
        reader = Reader.createFromString(content)

        return parseDocument()
    }

    Document parseFile(String filename) {
        null
    }

    protected Document parseDocument() {
        Document doc = new Document()

        if (reader.nextLine() == null) {
            return doc
        }

        doc.header = parseHeader()

        return doc
    }

    protected Header parseHeader() {
        Header header = null

        def line = reader.line

        if (line == null) {
            return null
        }

        def m = SECTION_PATTERN.matcher(line)

        if (m.matches()) {
            println("m[0] = ${m[0]}")
        }

        if (!m.matches()
            || m[0][1].length() != 1) {
            // doesn't have a header
            return null
        }

        reader.nextLine()

        header = new Header()
        header.title = m[0][2]

        // parse author
        // parse revision
        // parse attributes

        return header
    }
}
