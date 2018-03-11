package org.supermmx.asciidog.lexer

import spock.lang.*

import org.supermmx.asciidog.Reader

class LexerSpec extends Specification {
    def 'peek blank file'() {
        given:
        def reader = Reader.createFromString('')
        def lexer = new Lexer(reader)

        when:
        def token = lexer.peek()

        then:
        token.type == Token.Type.EOF
        token.row == 0
        token.col == 0
    }

    def 'peek blank line'() {
        given:
        def reader = Reader.createFromString('''
''')
        def lexer = new Lexer(reader)

        when:
        def token = lexer.peek()

        then:
        token.type == Token.Type.EOL
        token.row == 0
        token.col == 0
    }

    def 'peek token'() {
        given:
        def reader = Reader.createFromString('''line''')
        def lexer = new Lexer(reader)

        when:
        def token = lexer.peek()

        then:
        token.type == Token.Type.TEXT
        token.value == 'line'
        token.row == 0
        token.col == 0
    }

    def 'simple digits'() {
        given:
        def reader = Reader.createFromString('''12345''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(Token.Type.DIGITS, '12345', '', 0, 0),
            new Token(Token.Type.EOL, null, '', 0, 5),
            new Token(Token.Type.EOF, null, '', 1, 0)
        ]
    }

    def 'simple white spaces'() {
        given:
        def reader = Reader.createFromString(''' \t\t ''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(Token.Type.WHITE_SPACES, ' \t\t ', '', 0, 0),
            new Token(Token.Type.EOL, null, '', 0, 4),
            new Token(Token.Type.EOF, null, '', 1, 0)
        ]
    }

    def 'simple puncts'() {
        given:
        def reader = Reader.createFromString('''#%^.''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(Token.Type.PUNCTS, '#', '', 0, 0),
            new Token(Token.Type.PUNCTS, '%', '', 0, 1),
            new Token(Token.Type.PUNCTS, '^', '', 0, 2),
            new Token(Token.Type.PUNCTS, '.', '', 0, 3),
            new Token(Token.Type.EOL, null, '', 0, 4),
            new Token(Token.Type.EOF, null, '', 1, 0)
        ]
    }

    def 'peek next n tokens'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        lexer.peek(5) == [
            new Token(Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(Token.Type.EOL, null, '', 0, 10),
            new Token(Token.Type.TEXT, 'image', '', 1, 0),
        ]

        and:
        lexer.next() == new Token(Token.Type.PUNCTS, '==', '', 0, 0)

    }

    def 'next n tokens'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        lexer.next(5) == [
            new Token(Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(Token.Type.EOL, null, '', 0, 10),
            new Token(Token.Type.TEXT, 'image', '', 1, 0),
        ]

        and:
        lexer.next() == new Token(Token.Type.PUNCTS, '::', '', 1, 5)

    }

    def 'all tokens'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(Token.Type.EOL, null, '', 0, 10),
            new Token(Token.Type.TEXT, 'image', '', 1, 0),
            new Token(Token.Type.PUNCTS, '::', '', 1, 5),
            new Token(Token.Type.TEXT, 'test', '', 1, 7),
            new Token(Token.Type.PUNCTS, '.', '', 1, 11),
            new Token(Token.Type.TEXT, 'jpeg', '', 1, 12),
            new Token(Token.Type.PUNCTS, '[', '', 1, 16),
            new Token(Token.Type.TEXT, 'Test', '', 1, 17),
            new Token(Token.Type.PUNCTS, ',', '', 1, 21),
            new Token(Token.Type.DIGITS, '300', '', 1, 22),
            new Token(Token.Type.PUNCTS, ',', '', 1, 25),
            new Token(Token.Type.DIGITS, '200', '', 1, 26),
            new Token(Token.Type.PUNCTS, ']', '', 1, 29),
            new Token(Token.Type.EOL, null, '', 1, 30),
            new Token(Token.Type.EOF, null, '', 2, 0)
        ]
    }

}
