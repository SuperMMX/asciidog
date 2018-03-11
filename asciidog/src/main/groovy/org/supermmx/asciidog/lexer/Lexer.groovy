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
     * The tokens of the current line or more
     */
    private List<Token> tokens = [] as LinkedList<Token>
    /**
     * The last token
     */
    private Token lastToken

    Lexer(Reader reader) {
        this.reader = reader
    }

    /**
     * whether there are more tokens
     */
    boolean hasNext() {
        return peek() != null
    }

    /**
     * Peek the next token
     */
    Token peek() {
        def list = peek(1)

        if (list.size() == 0) {
            return null
        }

        return list.get(0)
    }

    /**
     * Peek the next-n tokens
     */
    List<Token> peek(int count) {
        while (tokens.size() < count && more()) {
        }

        return tokens.take(count)
    }

    /**
     * Take the next token and remove it from the queue
     */
    Token next() {
        def list = next(1)

        if (list.size() == 0) {
            return null
        }

        return list.get(0)
    }

    /**
     * Take the next n tokens and remove them from the queue.
     *
     * @param count the count of the tokens to take, -1 means all
     *
     * @return a list of the tokens
     */
    List<Token> next(int count) {
        def list = []
        while (hasNext()) {
            lastToken = tokens.remove(0)
            list << lastToken

            if (count > 0) {
                count --
                if (count == 0) {
                    break
                }
            }
        }

        return list
    }

    /**
     * Take all the remaining tokens, same as next(-1)
     *
     * @return a list of the tokens
     */
    List<Token> tokens() {
        return next(-1)
    }

    protected boolean more() {
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
            }

            return false
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
                lastCh = ch
                index ++

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

        return true
    }

}
