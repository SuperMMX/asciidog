package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.PluginRegistry

class QuoteParserSpec extends AsciidogSpec {
    def parser = new QuoteParser()

    def 'quote: with delimiter'() {
        given:
        def firstPara = '''first line
second line'''
        def secondPara = 'second paragraph'
        def thirdPara = 'third paragraph'
        def content = """
____
${firstPara}

${secondPara}
____

${thirdPara}"""

        def eDoc = builder.document {
            quote(hasDelimiter: true, isOpenBlock: false) {
                para {
                    text firstPara
                }
                para {
                    text secondPara
                }
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

    def 'quote: with delimiter and style'() {
        given:
        def firstPara = '''first line
second line'''
        def secondPara = 'second paragraph'
        def thirdPara = 'third paragraph'
        def content = """[quote]
____
${firstPara}

${secondPara}
____

${thirdPara}"""

        def eDoc = builder.document {
            quote(hasDelimiter: false, isOpenBlock: false, attributes: [quote:null]) {
                para {
                    text firstPara
                }
                para {
                    text secondPara
                }
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

    def 'quote: with style and open block delimiter'() {
        given:
        def firstPara = '''first line
second line'''
        def secondPara = 'second paragraph'
        def thirdPara = 'third paragraph'
        def content = """[quote]
--
${firstPara}

${secondPara}

--

${thirdPara}"""

        def eDoc = builder.document {
            quote(hasDelimiter: false, isOpenBlock: true, attributes: [quote:null]) {
                para {
                    text firstPara
                }
                para {
                    text secondPara
                }
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

    def 'quote: with style and single paragraph'() {
        given:
        def firstPara = '''first line
second line'''
        def content = """[quote]
${firstPara}
"""

        def eDoc = builder.document {
            quote(hasDelimiter: false, isOpenBlock: false, attributes: [quote:null]) {
                para {
                    text firstPara
                }
            }
        }

        when:
        def doc = parse(content)

        then:
        doc == eDoc
    }
}
