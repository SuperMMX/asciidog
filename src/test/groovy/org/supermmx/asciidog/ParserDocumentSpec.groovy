package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

import spock.lang.*

class ParserDocumentSpec extends Specification {
    @Shared
    def builder = new ObjectGraphBuilder()

    def setupSpec() {
        builder.classNameResolver = "org.supermmx.asciidog.ast"
        builder.identifierResolver = "uid"
    }

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

    def 'wrong section level in article'() {
        given:
        def content = '''
= Document Title

= Section
'''
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def doc = parser.parseDocument()

        then:
        doc == builder.document(docType: Document.DocType.article) {
            header(title: 'Document Title')
        }
    }

    def 'wrong section level in book'() {
        given:
        def content = '''
= Document Title
:doctype: book

== Section
'''
        def parser = new Parser()
        def reader = Reader.createFromString(content)
        parser.reader = reader

        when:
        def doc = parser.parseDocument()

        then:
        doc == builder.document(docType: Document.DocType.book) {
            header(title: 'Document Title') {
                current.blocks = [
                    attributeEntry(name: 'doctype', value: 'book')
                ]
            }
        }
    }
}
