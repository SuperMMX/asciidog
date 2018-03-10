package org.supermmx.asciidog.lexer

import spock.lang.*

import org.supermmx.asciidog.Reader

class LexerSpec extends Specification {
    def 'peek blank file'() {
        given:
        def reader = Reader.createFromString('')
        def lexer = new Lexer(reader: reader)

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
        def lexer = new Lexer(reader: reader)

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
        def lexer = new Lexer(reader: reader)

        when:
        def token = lexer.peek()

        then:
        token.type == Token.Type.TEXT
        token.value == 'line'
        token.row == 0
        token.col == 0
    }

    def 'next tokens'() {
        given:
        def reader = Reader.createFromString('''line''')
        def lexer = new Lexer(reader: reader)

        expect:
        lexer.tokens() == [
            new Token(Token.Type.TEXT, 'line', '', 0, 0),
            new Token(Token.Type.EOL, null, '', 0, 4),
            new Token(Token.Type.EOF, null, '', 1, 0),
        ]

        when: 'null'
        token = lexer.next()

        then:
        token == null
    }

    def 'simple digits'() {
        given:
        def reader = Reader.createFromString('''12345''')
        def lexer = new Lexer(reader: reader)

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
        def lexer = new Lexer(reader: reader)

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
        def lexer = new Lexer(reader: reader)

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
}
