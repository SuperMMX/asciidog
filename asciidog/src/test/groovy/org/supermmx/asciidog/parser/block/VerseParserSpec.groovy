package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
class VerseParserSpec extends AsciidogSpec {
    def parser = new VerseParser()

    def 'verse: with delimiter and style'() {
        given:
        def firstPara = '''first line
second line'''
        def secondPara = 'second paragraph'
        def thirdPara = 'third paragraph'
        def content = """[verse]
____
${firstPara}

${secondPara}
____

${thirdPara}"""

        def eDoc = builder.document {
            verse(hasDelimiter: false, isOpenBlock: false, attributes: [verse:null]) {
                text """${firstPara}

${secondPara}
"""
            }
            para {
                text thirdPara
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'verse: with style and open block delimiter'() {
        given:
        def firstPara = '''first line
second line'''
        def secondPara = 'second paragraph'
        def thirdPara = 'third paragraph'
        def content = """[verse]
--
${firstPara}

${secondPara}
--

${thirdPara}"""

        def eDoc = builder.document {
            verse(hasDelimiter: false, isOpenBlock: true, attributes: [verse:null]) {
                text """${firstPara}

${secondPara}
"""
            }
            para {
                text thirdPara
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

    def 'verse: with style and single paragraph'() {
        given:
        def firstPara = '''first line
second line
'''
        def content = """[verse]
${firstPara}"""

        def eDoc = builder.document {
            verse(hasDelimiter: false, isOpenBlock: false, attributes: [verse:null]) {
                text firstPara
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }
}
