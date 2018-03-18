package org.supermmx.asciidog.lexer

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.reader.Cursor
import org.supermmx.asciidog.parser.TokenMatcher

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

    /**
     * The saved tokens when marked
     */
    private List<Token> markTokens = [] as LinkedList<Token>
    /**
     * The saved last token when marked
     */
    private Token markLastToken

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

            // in mark state
            if (markLastToken != null) {
                markTokens << lastToken
            }

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

    /**
     * Skip consecutive WHITE_SPACES and EOLs
     */
    void skipBlanks() {
        while (hasNext()) {
            def token = next()

            if (token.type != Token.Type.WHITE_SPACES
                && token.type != Token.Type.EOL) {
                back(token)
                break
            }
        }
    }

    /**
     * Combine the value of the remaining tokens till the matcher matches.
     * NOTE: This method will re-mark the lexer, the previous mark is cleared
     *
     * @consume whether to consume the matched tokens. The default value is true.
     * @ignore whether to ignore or include the consumed tokens. Only valid
     *         when consume is true. The default value is true.
     *
     * @return the combined result
     */
    String combineTo(TokenMatcher matcher, boolean consume = true, boolean ignore = true) {
        def buf = new StringBuilder()

        while (hasNext()) {
            // mark for every matching
            mark()

            def matched = matcher.matches(this)
            if (matched) {
                // consume the tokens
                if (consume) {
                    if (!ignore) {
                        // add to the buffer
                        tokensFromMark.each { buf.append(it.value) }
                    }
                    clearMark()
                } else {
                    reset()
                }
                break
            } else {
                // doesn't match, add the token
                reset()
                buf.append(next().value)
            }
        }

        return buf.toString()
    }

    /**
     * Put the token back in front
     */
    void back(Token token) {
        if (token != null) {
            tokens.add(0, token)
        }
    }

    /**
     * Put tokens back in front
     */
    void back(List<Token> backTokens) {
        if (backTokens == null) {
            return
        }

        tokens.addAll(0, backTokens)
    }

    /**
     * Mark current token position. A subsequent call to the reset method
     * repositions the lexer at the last marked position
     */
    void mark() {
        // remark current position
        if (markLastToken != null) {
            markTokens.clear()
        }

        markLastToken = lastToken
    }

    /**
     * Get tokens from mark
     *
     * @return list of tokens from the marked position to current position
     */
    List<Token> getTokensFromMark() {
        return markTokens
    }

    /**
     * Repositions the lexer to the position that the mark method
     * was last called
     */
    void reset() {
        // not marked
        if (markLastToken == null) {
            return
        }

        // push back the saved tokens from marked position
        back(markTokens)
        // restore last token
        lastToken = markLastToken

        // reset state
        clearMark()
    }

    /**
     * Clear the mark
     */
    void clearMark() {
        markTokens.clear()
        markLastToken = null
    }

    /**
     * Read more tokens
     *
     * @return true if there are more tokens, otherwise false
     */
    protected boolean more() {
        // tokenize the next line

        def cursor = reader.getCursor()
        def uri = cursor.uri
        def row = cursor.lineno
        def col = cursor.column

        // the first token is always the BOF, but not returned to the caller
        if (lastToken == null) {
            lastToken = new Token(Token.Type.BOF, null, uri, -1, 0)
        }
        def line = reader.nextLine()

        // EOF
        if (line == null) {
            if (lastToken.type == Token.Type.BOF
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
        tokens << new Token(Token.Type.EOL, '\n', uri, row, col)

        log.debug "Line tokens: ${tokens}"

        return true
    }

}
