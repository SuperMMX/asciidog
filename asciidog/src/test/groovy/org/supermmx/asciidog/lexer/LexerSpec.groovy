package org.supermmx.asciidog.lexer

import spock.lang.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.parser.TokenMatcher

class LexerSpec extends Specification {
    def 'peek blank file'() {
        given:
        def reader = Reader.createFromString('')
        def lexer = new Lexer(reader)

        when:
        def token = lexer.peek()

        then:
        token.index == 0
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
        token.index == 0
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
        token.index == 0
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
            new Token(0, Token.Type.DIGITS, '12345', '', 0, 0),
            new Token(1, Token.Type.EOL, '\n', '', 0, 5),
            new Token(2, Token.Type.EOF, null, '', 1, 0)
        ]
    }

    def 'simple white spaces'() {
        given:
        def reader = Reader.createFromString(''' \t\t ''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(0, Token.Type.WHITE_SPACES, ' \t\t ', '', 0, 0),
            new Token(1, Token.Type.EOL, '\n', '', 0, 4),
            new Token(2, Token.Type.EOF, null, '', 1, 0)
        ]
    }

    def 'simple puncts'() {
        given:
        def reader = Reader.createFromString('''#%^.''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(0, Token.Type.PUNCTS, '#', '', 0, 0),
            new Token(1, Token.Type.PUNCTS, '%', '', 0, 1),
            new Token(2, Token.Type.PUNCTS, '^', '', 0, 2),
            new Token(3, Token.Type.PUNCTS, '.', '', 0, 3),
            new Token(4, Token.Type.EOL, '\n', '', 0, 4),
            new Token(5, Token.Type.EOF, null, '', 1, 0)
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
            new Token(0, Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(1, Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(2, Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(3, Token.Type.EOL, '\n', '', 0, 10),
            new Token(4, Token.Type.TEXT, 'image', '', 1, 0),
        ]

        and:
        lexer.next() == new Token(0, Token.Type.PUNCTS, '==', '', 0, 0)

    }

    def 'next n tokens'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        lexer.next(5) == [
            new Token(0, Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(1, Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(2, Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(3, Token.Type.EOL, '\n', '', 0, 10),
            new Token(4, Token.Type.TEXT, 'image', '', 1, 0),
        ]

        and:
        lexer.next() == new Token(5, Token.Type.PUNCTS, '::', '', 1, 5)

    }

    def 'back one token'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        when:
        def tokens = lexer.next(5)
        lexer.back(tokens[4])

        then:
        lexer.peek() == tokens[4]
    }

    def 'back tokens'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        when:
        def tokens = lexer.next(8)
        lexer.back(tokens.takeRight(4))

        then:
        lexer.peek(4) == tokens.takeRight(4)
    }

    def 'combine to eol'() {
        given:
        def reader = Reader.createFromString('''== Section  
  image::test.jpeg[Test,300,200]  
next 100 lines
''')
        def lexer = new Lexer(reader)
        def matcher = TokenMatcher.type(Token.Type.EOL)

        expect: 'not consuming'
        lexer.combineTo(matcher, false) == '== Section  '
        lexer.next().type == Token.Type.EOL

        and: 'consume and ignore'
        lexer.combineTo(matcher) == '  image::test.jpeg[Test,300,200]  '
        lexer.peek().value == 'next'

        and:
        lexer.combineTo(matcher, true, false) == '''next 100 lines
'''
        lexer.peek().type == Token.Type.EOF
        lexer.tokensFromMark == null
    }

    def 'combine to for blank'() {
        given:
        def reader = Reader.createFromString('')
        def lexer = new Lexer(reader)
        def matcher = TokenMatcher.type(Token.Type.EOL)

        expect:
        lexer.combineTo(matcher) == ''
        lexer.next().type == Token.Type.EOF

    }

    def 'skip blanks'() {
        def reader = Reader.createFromString('''== Section  

\t  \t
  \t\t  next line
''')
        def lexer = new Lexer(reader)

        when:
        lexer.next(3)
        lexer.skipBlanks()

        then:
        lexer.next().value == 'next'
    }

    def 'all tokens'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokens() == [
            new Token(0, Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(1, Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(2, Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(3, Token.Type.EOL, '\n', '', 0, 10),
            new Token(4, Token.Type.TEXT, 'image', '', 1, 0),
            new Token(5, Token.Type.PUNCTS, '::', '', 1, 5),
            new Token(6, Token.Type.TEXT, 'test', '', 1, 7),
            new Token(7, Token.Type.PUNCTS, '.', '', 1, 11),
            new Token(8, Token.Type.TEXT, 'jpeg', '', 1, 12),
            new Token(9, Token.Type.PUNCTS, '[', '', 1, 16),
            new Token(10, Token.Type.TEXT, 'Test', '', 1, 17),
            new Token(11, Token.Type.PUNCTS, ',', '', 1, 21),
            new Token(12, Token.Type.DIGITS, '300', '', 1, 22),
            new Token(13, Token.Type.PUNCTS, ',', '', 1, 25),
            new Token(14, Token.Type.DIGITS, '200', '', 1, 26),
            new Token(15, Token.Type.PUNCTS, ']', '', 1, 29),
            new Token(16, Token.Type.EOL, '\n', '', 1, 30),
            new Token(17, Token.Type.EOF, null, '', 2, 0)
        ]
    }

    def 'mark, clearMark and reset'() {
        given:
        def reader = Reader.createFromString('''== Section
image::test.jpeg[Test,300,200]
''')
        def lexer = new Lexer(reader)

        expect:
        lexer.tokensFromMark == null

        when: 'first mark'
        lexer.mark()

        then:
        lexer.tokensFromMark == []

        when:
        lexer.next(2)

        then:
        lexer.tokensFromMark == [
            new Token(0, Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(1, Token.Type.WHITE_SPACES, ' ', '', 0, 2),
        ]

        when: 'second mark'
        lexer.mark()

        then:
        lexer.tokensFromMark == []

        when:
        lexer.next(3)

        then:
        lexer.tokensFromMark == [
            new Token(2, Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(3, Token.Type.EOL, '\n', '', 0, 10),
            new Token(4, Token.Type.TEXT, 'image', '', 1, 0),
        ]

        when: 'third mark'
        lexer.mark()
        lexer.next(2)

        then:
        lexer.tokensFromMark == [
            new Token(5, Token.Type.PUNCTS, '::', '', 1, 5),
            new Token(6, Token.Type.TEXT, 'test', '', 1, 7),
        ]

        when: 'third reset'
        lexer.reset()

        then:
        lexer.peek() == new Token(5, Token.Type.PUNCTS, '::', '', 1, 5)
        lexer.tokensFromMark == [
            new Token(2, Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(3, Token.Type.EOL, '\n', '', 0, 10),
            new Token(4, Token.Type.TEXT, 'image', '', 1, 0),
        ]

        when: 'second clear'
        lexer.clearMark()

        then:
        lexer.peek() == new Token(5, Token.Type.PUNCTS, '::', '', 1, 5)
        lexer.tokensFromMark == [
            new Token(0, Token.Type.PUNCTS, '==', '', 0, 0),
            new Token(1, Token.Type.WHITE_SPACES, ' ', '', 0, 2),
            new Token(2, Token.Type.TEXT, 'Section', '', 0, 3),
            new Token(3, Token.Type.EOL, '\n', '', 0, 10),
            new Token(4, Token.Type.TEXT, 'image', '', 1, 0),
        ]

        when:
        lexer.reset()

        then:
        lexer.peek() == new Token(0, Token.Type.PUNCTS, '==', '', 0, 0)
        lexer.tokensFromMark == null

    }
}
