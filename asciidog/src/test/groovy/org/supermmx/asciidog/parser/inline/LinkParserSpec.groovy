package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.parser.block.ParagraphParser

class LinkParserSpec extends AsciidogSpec {
    def parser = new ParagraphParser()

    def 'link: simple link without text'() {
        given:
        def content = '''
http://test.com
'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            link('http://test.com') {
                text 'http://test.com'
            }
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'link: simple link with text'() {
        given:
        def content = '''
http://test.com[Test Site]
'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            link('http://test.com') {
                text 'Test Site'
            }
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'link: with blank text'() {
        given:
        def content = '''
visit http://test.com[] please
'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text 'visit '
            link('http://test.com') {
                text 'http://test.com'
            }
            text ' please'
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'link: without text'() {
        given:
        def content = '''
visit http://test.com please
'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text 'visit '
            link('http://test.com') {
                text 'http://test.com'
            }
            text ' please'
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'link: with text'() {
        given:
        def content = '''
visit http://test.com[Test Site] please
'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text 'visit '
            link('http://test.com') {
                text 'Test Site'
            }
            text ' please'
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'link: with formatted text'() {
        given:
        def content = '''
visit http://test.com[Test *Site*] please
'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text 'visit '
            link('http://test.com') {
                text 'Test '
                strong {
                    text 'Site'
                }
            }
            text ' please'
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }

    def 'link: unicode tokens'() {
        given:
        def content = '''
网站：https://test.com[测试]'''
        def context = parserContext(content)
        context.parserId = parser.id

        def ePara = builder.para {
            text '网站：'
            link('https://test.com') {
                text '测试'
            }
        }

        when:
        def para = parser.parse(context)

        then:
        para == ePara
    }
}
