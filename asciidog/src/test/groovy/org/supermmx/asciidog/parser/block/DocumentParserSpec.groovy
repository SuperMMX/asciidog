package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node

class DocumentParserSpec extends AsciidogSpec {
    def parser = new DocumentParser()

    def 'createBlock: article'() {
        given:

        def content = '= Document Title'
        def edoc = builder.document(title: 'Document Title') {
        }

        def context = parserContext(content)
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def doc = parser.createBlock(context, null, header)

        then:
        edoc == doc
        context.attributes.getAttribute(Document.DOCTYPE).value ==  Document.DocType.article.toString()
    }

    def 'createBlock: inline as section'() {
        given:

        def content = '== Section Title'
        def edoc = builder.document {
        }

        def context = parserContext(content)
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def doc = parser.createBlock(context, null, header)

        then:
        edoc == doc
        context.attributes.getAttribute(Document.DOCTYPE).value ==  Document.DocType.inline.toString()
        context.lexer.peek().value == '=='
    }

    def 'createBlock: inline as other'() {
        given:

        def content = 'this is a paragraph'
        def edoc = builder.document {
        }

        def context = parserContext(content)
        def header = new BlockParserPlugin.BlockHeader()

        when:
        def doc = parser.createBlock(context, null, header)

        then:
        edoc == doc
        context.attributes.getAttribute(Document.DOCTYPE).value ==  Document.DocType.inline.toString()
        context.lexer.peek().value == 'this'
    }

    def 'document: with header and preamble'() {
        given:
        def content = '''= Document Title
First Last <first.last@email.com>
:name: value

this is the preamble paragraph

== Section Title
'''
        def eDoc = builder.document(title: 'Document Title') {
            header {
                authors {
                    author 'First Last <first.last@email.com>'
                }
                attribute 'name', 'value'
            }

            preamble {
                para {
                    text 'this is the preamble paragraph'
                }
            }

            section(level: 1, title: 'Section Title')
        }

        def context = parserContext(content)

        when:
        def doc = Parser.parse(context)

        then:
        doc == eDoc
    }

    def 'document: with sections'() {
        given:
        def content = '''= Document Title

== Section Title

== Another Section
'''
        def eDoc = builder.document(title: 'Document Title') {
            header {
            }

            section(level: 1, title: 'Section Title')
            section(level: 1, title: 'Another Section')
        }
        def context = parserContext(content)

        when:
        def doc = Parser.parse(context)

        then:
        doc == eDoc
    }
}
