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

        when: 'first token'
        def token = lexer.next()

        then:
        token.type == Token.Type.TEXT
        token.value == 'line'
        token.row == 0
        token.col == 0

        when: 'eol'
        token = lexer.next()

        then:
        token.type == Token.Type.EOL
        token.value == null
        token.row == 0
        token.col == 4

        when: 'eof'
        token = lexer.next()

        then:
        token.type == Token.Type.EOF
        token.value == null
        token.row == 1
        token.col == 0

        when: 'null'
        token = lexer.next()

        then:
        token == null
    }
}
