package org.supermmx.asciidog.parser

import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token

import groovy.transform.TupleConstructor

/**
 * Match tokens
 */
abstract class TokenMatcher {
    abstract boolean matches(Lexer lexer)

    /**
     * Match the token value
     */
    static TokenMatcher literal(String value) {
        return new ClosureMatcher(value, { token, valueObj ->
            token?.value == valueObj
        })
    }

    /**
     * Match the token type
     */
    static TokenMatcher type(Token.Type type) {
        return new ClosureMatcher(type, { token, typeObj ->
            token?.type == typeObj
        })
    }

    /**
     * Match the token with custom closure
     */
    static TokenMatcher match(Closure closure) {
        return new ClosureMatcher(null, closure)
    }

    /**
     * Match a sequence of matchers
     */
    static TokenMatcher sequence(TokenMatcher... matchers) {
        return new SequenceMatcher(matchers)
    }

    @TupleConstructor
    static class ClosureMatcher extends TokenMatcher {
        Object value
        Closure condition

        @Override
        boolean matches(Lexer lexer) {
            def token = lexer.next()

            return condition(token, value)
        }
    }

    @TupleConstructor
    static class SequenceMatcher extends TokenMatcher {
        TokenMatcher[] matchers = []

        @Override
        boolean matches(Lexer lexer) {
            def result = true
            for (def matcher: matchers) {
                if (!matcher.matches(lexer)) {
                    result = false
                    break
                }
            }

            return result
        }
    }

    static class OptionalMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        boolean matches(Lexer lexer) {
            matcher.matches(lexer)

            return true
        }
    }

    static class ZeroOrMoreMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        boolean matches(Lexer lexer) {
            while (matcher.matches(lexer)) {
            }

            return true
        }
    }

    static class OneOrMoreMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        boolean matches(Lexer lexer) {
            def count = 0
            while (matcher.matches(lexer)) {
                count ++
            }

            return count > 0
        }
    }
}
