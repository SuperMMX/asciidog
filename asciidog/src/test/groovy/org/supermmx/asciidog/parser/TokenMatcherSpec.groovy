package org.supermmx.asciidog.parser

import static org.supermmx.asciidog.parser.TokenMatcher.*

import spock.lang.*

import org.supermmx.asciidog.AsciidogSpec
import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token

class TokenMatcherSpec extends AsciidogSpec {
    def 'literal matcher'() {
        given:
        def context = parserContext('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = context.lexer

        expect:
        TokenMatcher.literal('==').matches(context)

        when:
        lexer.next()

        then:
        !TokenMatcher.literal('abc').matches(context)
    }

    def 'type matcher'() {
        given:
        def context = parserContext('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = context.lexer

        when:
        lexer.next(5)

        then:
        TokenMatcher.type(Token.Type.PUNCTS).matches(context)
        !TokenMatcher.type(Token.Type.PUNCTS).matches(context)
    }

    def 'closure matcher'() {
        given:
        def context = parserContext('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = context.lexer

        when:
        lexer.next(4)

        then:
        TokenMatcher.match({ token, value ->
            token?.type == Token.Type.TEXT && token?.value == 'image'
        }).matches(context)

        !TokenMatcher.match({ token, value ->
            token?.type == Token.Type.PUNCTS && token?.value != '::'
        }).matches(context)
    }

    def 'sequence matcher'() {
        given:
        def context = parserContext('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = context.lexer

        expect:
        sequence([
            literal('=='), type(Token.Type.WHITE_SPACES),
            literal('Section'), type(Token.Type.EOL)]
        ).matches(context)
    }

}
