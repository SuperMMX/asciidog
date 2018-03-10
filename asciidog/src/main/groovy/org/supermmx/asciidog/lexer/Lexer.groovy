package org.supermmx.asciidog.lexer

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.reader.Cursor

import groovy.util.logging.Slf4j

import org.slf4j.Logger

/**
 * Lexer to convert characters to tokens
 */
@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class Lexer {
    /**
     * The underlying reader
     */
    private Reader reader

    /**
     * The tokens of the current line
     */
    private List<Token> tokens = []
    /**
     * The last token
     */
    private Token lastToken

    /**
     * whether there are more tokens
     */
    boolean hasNext() {
        return peek() != null
    }

    /**
     * Peek the next tokens
     */
    Token peek() {
        def result = null

        if (tokens.size() > 0) {
            return tokens.head()
        }

        // tokenize the next line

        def cursor = reader.getCursor()
        def uri = cursor.uri
        def row = cursor.lineno
        def col = cursor.column

        def line = reader.nextLine()

        // EOF
        if (line == null) {
            if (lastToken == null
                // duplicate call
                || lastToken.type != Token.Type.EOF) {
                tokens << new Token(Token.Type.EOF, null,
                                    uri, row, col)
                result = tokens.head()
            }

            return result
        }

        def len = line.length()
        def index = 0

        def type = null
        def lastCh = null

        log.debug 'Line {}: "{}"', row, line

        while (index < len) {
            def ch = line.charAt(index)

            log.debug 'index = {}, ch = {}, last char = {}, last type = {}', index, ch, lastCh, type

            // same character as last one, combine them
            if (ch == lastCh) {
                continue
            }

            def chType = Token.Type.TEXT

            // get token type for current char
            for (Token.Type matchingType: Token.Type.MATCHING_TYPES) {
                if (matchingType.matches(ch)) {
                    chType = matchingType
                    break
                }
            }

            log.debug 'ch type is {}', chType

            if (type == null) {
                // first token in the line
                type = chType
            } else {
                // compare to the current type
                if (chType != type
                    // the type is not combining
                    || !chType.combining) {
                    def value = line.substring(col, index)
                    def token = new Token(type, value, uri, row, col)
                    tokens << token

                    log.debug 'Token created: {}', token

                    col = index
                    type = chType
                }
            }

            lastCh = ch

            index++
        }

        log.debug "col = ${col}, index = ${index}"

        // last one
        if (col < index) {
            // last token
            def value = line.substring(col, index)
            def token = new Token(type, value, uri, row, col)
            tokens << token

            log.debug 'Last token in line created: {}', token

            col = index
        }

        // eol
        tokens << new Token(Token.Type.EOL, null, uri, row, col)

        log.debug "Line tokens: ${tokens}"

        return tokens.head()
    }

    /**
     * Get the next token
     */
    Token next() {
        def token = peek()

        if (token != null) {
            tokens.remove(0)
            lastToken = token
        }

        return token
    }

    List<Token> tokens() {
        def tokens = []

        while (hasNext()) {
            tokens << next()
        }

        return tokens
    }
}
