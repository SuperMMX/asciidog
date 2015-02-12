package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

import spock.lang.*

class ParserDocumentSpec extends Specification {
    def 'parse: document: doctype'() {
        given:
        def content = '''
= Document Title
:doctype: book

'''
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def doc = parser.parseDocument()

        then:
        doc.docType == Document.DocType.book
    }

    def 'parse: document: preamble'() {
        given:
        def content = '''
= Document Title

this is the paragraph
in the preamble
of the document

and a new paragraph

'''
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def doc = parser.parseDocument()

        then:
        doc.blocks.size() == 2
        doc.blocks[0].lines == [ 'this is the paragraph', 'in the preamble', 'of the document' ]
        doc.blocks[1].lines == [ 'and a new paragraph' ]
    }
}
