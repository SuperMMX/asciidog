package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

class Parser {
    static final def SECTION_PATTERN = /^(={1,6})\s+(\S+(\s+\S+)*)\s*$/

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
        println "line = $line"
        def m = (line =~ SECTION_PATTERN)

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
