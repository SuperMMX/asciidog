package org.supermmx.asciidog

import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Header

import spock.lang.*

class ParserDocumentSpec extends AsciidogSpec {
    def 'parse: document: doctype'() {
        given:
        def content = '''
= Document Title
:doctype: book

'''
        when:
        def doc = parser(content).parseDocument()

        then:
        doc == builder.document(docType: Document.DocType.book) {
            header(title: 'Document Title') {
                current.blocks = [
                    attributeEntry(name: 'doctype', value: 'book')
                ]
            }
        }
    }

    def 'parse: document: preamble'() {
        given:
        def text1 = '''this is the paragraph
in the preamble
of the document'''
        def text2 = '''and a new paragraph'''
        def content = """
= Document Title

$text1

$text2

"""
        when:
        def doc = parser(content).parseDocument()

        then:
        doc == builder.document(docType: Document.DocType.article) {
            header(title: 'Document Title')
            current.blocks = [
                para(text1),
                para(text2)
            ]
        }
    }

    def 'wrong section level in article'() {
        given:
        def content = '''
= Document Title

= Section
'''
        when:
        def doc = parser(content).parseDocument()

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
        when:
        def doc = parser(content).parseDocument()

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
