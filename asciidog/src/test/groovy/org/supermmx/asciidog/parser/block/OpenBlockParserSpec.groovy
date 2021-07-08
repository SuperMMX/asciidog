package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

class OpenBlockParserSpec extends AsciidogSpec {
    def parser = new OpenBlockParser()

    def 'openBlock: with delimiter'() {
        given:
        def firstPara = '''first line
second line'''
        def secondPara = 'second paragraph'
        def thirdPara = 'third paragraph'
        def content = """
--
${firstPara}

${secondPara}
--

${thirdPara}"""

        def eDoc = builder.document {
            openBlock(hasDelimiter: true, isOpenBlock: true) {
                para {
                    text firstPara
                }
                para {
                    text secondPara
                }
                para {
                    text thirdPara
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }

}
