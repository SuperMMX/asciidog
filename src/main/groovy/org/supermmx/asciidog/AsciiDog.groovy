package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.converter.Converter

class AsciiDog {
    static void main(String[] args) {
        String file = args[0]
        String output = args[1]
        Parser parser = new Parser()
        Document doc = parser.parseFile(file)

        Converter converter = new Converter()
        converter.convertToHtmlFile(doc, output)
    }
}
