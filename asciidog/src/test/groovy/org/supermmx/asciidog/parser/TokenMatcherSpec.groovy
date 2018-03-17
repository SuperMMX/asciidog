package org.supermmx.asciidog.parser

import static org.supermmx.asciidog.parser.TokenMatcher.*

import spock.lang.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token

class TokenMatcherSpec extends Specification {
    def 'literal matcher'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        TokenMatcher.literal('==').matches(lexer)

        when:
        lexer.next()

        then:
        !TokenMatcher.literal('abc').matches(lexer)
    }

    def 'type matcher'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        when:
        lexer.next(5)

        then:
        TokenMatcher.type(Token.Type.PUNCTS).matches(lexer)
        !TokenMatcher.type(Token.Type.PUNCTS).matches(lexer)
    }

    def 'closure matcher'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        when:
        lexer.next(4)

        then:
        TokenMatcher.match({ token, value ->
            token?.type == Token.Type.TEXT && token?.value == 'image'
        }).matches(lexer)

        !TokenMatcher.match({ token, value ->
            token?.type == Token.Type.PUNCTS && token?.value != '::'
        }).matches(lexer)
    }

    def 'sequence matcher'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        sequence(
            literal('=='), type(Token.Type.WHITE_SPACES),
            literal('Section'), type(Token.Type.EOL)
        ).matches(lexer)
    }

}
