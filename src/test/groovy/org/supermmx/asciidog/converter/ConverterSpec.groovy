package org.supermmx.asciidog.converter

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

import spock.lang.*

class ConverterSpec extends Specification {
    def 'test'() {
        given:
        def reader = Reader.createFromString(
'''= Document Title

this is a preamble paragraph
with multiple line

another preamble paragraph

== Section 1

中文段落 this is a chinese paragraph
多行段落

没有啥事

== Section 2

paragraph in section 2
'''
        )
        def parser = new Parser()
        parser.reader = reader
        def document = parser.parseDocument()

        when:
        def converter = new Converter()
        converter.convert(document)

        then:
        document != null
    }
}
