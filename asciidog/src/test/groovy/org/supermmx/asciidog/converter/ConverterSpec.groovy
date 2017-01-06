package org.supermmx.asciidog.converter

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Author
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Header
import org.supermmx.asciidog.ast.Paragraph

class ConverterSpec extends AsciidogSpec {
    def 'test'() {
        given:
        def content = '''= Document Title

this is a preamble paragraph
with multiple line

another preamble paragraph

== Section 1

中**文段**落 this is a ch**ine**se paragraph
*多行段落*

没有啥事

== Section 2

paragraph in section 2
'''
        def document = parse(content)

        when:
        def converter = new Converter()
        converter.convert(document)

        then:
        document != null
    }
}
