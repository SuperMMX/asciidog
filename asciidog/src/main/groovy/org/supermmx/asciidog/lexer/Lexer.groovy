package org.supermmx.asciidog.lexer

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.reader.Cursor
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.ParserContext

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

    private int index = 0

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

    private List<Integer> marks = []

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
            if (marks.size() > 0) {
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
     * Skip consecutive WHITE_SPACES or/and EOLs
     *
     * @param includeEOLs including EOLs, the default value is true
     * @param includeWhiteSpaces including white spaces, the default value is false
     */
    void skipBlanks(boolean includeEOLs = true, boolean includeWhiteSpaces = false) {
        while (hasNext()) {
            def token = peek()

            if (includeEOLs && token.type == Token.Type.EOL
                || includeWhiteSpaces && token.type == Token.Type.WHITE_SPACES) {
                next()
            } else {
                break
            }
        }
    }

    /**
     * Combine the value of the remaining tokens till the matcher matches.
     *
     * @consume whether to consume the matched tokens. The default value is true.
     * @ignore whether to ignore or include the consumed tokens. Only valid
     *         when consume is true. The default value is true.
     *
     * @return the combined result
     */
    String combineTo(TokenMatcher matcher, boolean consume = true, boolean ignore = true) {
        return joinTokensTo(matcher, consume, ignore)
    }

    /**
     * Join the value of the remaining tokens till the matcher matches.
     *
     * @consume whether to consume the matched tokens. The default value is true.
     * @ignore whether to ignore or include the consumed tokens. Only valid
     *         when consume is true. The default value is true.
     *
     * @return the combined result
     */
    String joinTokensTo(TokenMatcher matcher, boolean consume = true, boolean ignore = true) {
        def buf = new StringBuilder()

        while (hasNext()) {
            if (peek().type == Token.Type.EOF) {
                break
            }
            // mark for every matching
            mark()

            def matched = matcher.matches(new ParserContext(lexer: this))
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
     * re-positions the lexer at the last marked position, and clearMark method
     * to keep the current position but clear the mark
     */
    void mark() {
        marks << markTokens.size()
    }

    /**
     * Get tokens from the last mark
     *
     * @return list of tokens from the last marked position to current position. Return null if there are no marks
     */
    List<Token> getTokensFromMark() {
        // no marks
        if (marks.size() == 0) {
            return null
        }

        // take the tokens from the marked position to the end
        return markTokens.takeRight(markTokens.size() - marks.last())
    }

    /**
     * Join tokens from the latest mark to current position as string
     *
     * @return the joined string result
     */
    String joinTokensFromMark() {
        return tokensFromMark?.collect { it.value }.join()
    }

    /**
     * Re-positions the lexer to the position that the mark method
     * was last called
     */
    void reset() {
        def backTokens = this.tokensFromMark
        if (backTokens == null) {
            return
        }

        // push back the tokens from the mark position
        back(backTokens)

        // remove last count tokens from mark tokens
        for (def count = backTokens.size(); count > 0; count --) {
            markTokens.removeLast()
        }

        // clear the mark
        clearMark()
    }

    /**
     * Clear the last mark, and will NOT re-position the current token position
     */
    void clearMark() {
        // no marks
        if (marks.size() == 0) {
            return
        }

        // remove the last mark
        marks.removeLast()

        // clear mark tokens if there are no more marks
        if (marks.size() == 0) {
            markTokens.clear()
        }
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
            lastToken = new Token(-1, Token.Type.BOF, null, uri, -1, 0)
        }
        def line = reader.nextLine()

        // EOF
        if (line == null) {
            if (lastToken.type == Token.Type.BOF
                // duplicate call
                || lastToken.type != Token.Type.EOF) {
                tokens << new Token(index++, Token.Type.EOF, null,
                                    uri, row, col)
            }

            return false
        }

        def len = line.length()
        def chIndex = 0

        def type = null
        def lastCh = null

        log.debug 'Line {}: "{}"', row, line

        while (chIndex < len) {
            def ch = line.charAt(chIndex)

            log.debug 'chIndex = {}, ch = {}, last char = {}, last type = {}', chIndex, ch, lastCh, type

            // same character as last one, combine them
            if (ch == lastCh) {
                lastCh = ch
                chIndex ++

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
                    def value = line.substring(col, chIndex)
                    def token = new Token(index++, type, value, uri, row, col)
                    tokens << token

                    log.debug 'Token created: {}', token

                    col = chIndex
                    type = chType
                }
            }

            lastCh = ch

            chIndex++
        }

        log.debug "col = ${col}, chIndex = ${chIndex}"

        // last one
        if (col < chIndex) {
            // last token
            def value = line.substring(col, chIndex)
            def token = new Token(index++, type, value, uri, row, col)
            tokens << token

            log.debug 'Last token in line created: {}', token

            col = chIndex
        }

        // eol
        tokens << new Token(index++, Token.Type.EOL, '\n', uri, row, col)

        log.debug "Line tokens: ${tokens}"

        return true
    }

    @Override
    String toString() {
        StringBuilder buf = new StringBuilder()
        buf.append('Tokens: ').append(tokens).append('\n')
        buf.append('Index: ').append(index).append('\n')
        buf.append('Last Token: ').append(lastToken).append('\n')
        buf.append('Marks: ').append(marks).append('\n')
        buf.append('Mark Tokens: ').append(markTokens).append('\n')

        return buf.toString()
    }
}
