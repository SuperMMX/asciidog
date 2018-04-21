package org.supermmx.asciidog.parser

import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import java.util.regex.Pattern

/**
 * Match tokens
 */
abstract class TokenMatcher {
    static final TokenMatcher EOL_MATCHER = type(Token.Type.EOL)

    protected abstract boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null)

    /**
     * The matcher name, could be used in the action to check the result
     */
    String name
    /**
     * Reset to the mark always for this matcher.
     * Normally set to true for the last matcher in order not to consume the tokens
     */
    boolean selfReset = false
    /**
     * Match against this matcher starting from next token.
     * The tokens are consumed if matched and not reset.
     *
     * @param context the parser context
     * @param reset whether to reset the lexer even matched
     * @param props properties passed to the matcher and action
     * @param action the action to call after the matching, the parameters are name, context, props, matched
     *
     * @return true if matched, false otherwise
     */
    boolean matches(ParserContext context, boolean reset = false, Map<String, Object> props = [:], Closure action = null) {
        context.lexer.mark()

        def matched = doMatch(context, props, action)

        action?.call(name, context, props, matched)

        if (matched && !reset && !selfReset) {
            context.lexer.clearMark()
        } else {
            context.lexer.reset()
        }

        return matched
    }

    /**
     * Match the token value
     */
    static TokenMatcher literal(String value, boolean selfReset = false) {
        return literal(null, value, selfReset)
    }

    static TokenMatcher literal(String name, String value, boolean selfReset = false) {
        return new ClosureMatcher(name: name, value: value, selfReset: selfReset,
                                  condition: { context, props, valueObj ->
                def token = context.lexer.next()
                token?.value == valueObj
            })
    }

    static TokenMatcher regex(String regexStr) {
        return regex(null, regexStr)
    }

    static TokenMatcher regex(String name, String regexStr) {
        return regexPattern(name, ~regexStr)
    }

    static TokenMatcher regexPattern(Pattern pattern) {
        return regexPattern(null, pattern)
    }

    static TokenMatcher regexPattern(String name, Pattern pattern) {
        return new ClosureMatcher(value: pattern, condition: { context, props, valueObj ->
            def token = context.lexer.next()
            pattern.matcher(token?.value).matches()
        })
    }

    /**
     * Match the token type
     */
    static TokenMatcher type(Token.Type type) {
        return TokenMatcher.type(null, type)
    }

    static TokenMatcher type(String name, Token.Type type) {
        return new ClosureMatcher(name: name, value: type, condition: { context, props, typeObj ->
            def token = context.lexer.next()
            token?.type == typeObj
        })
    }

    /**
     * Match the token with custom closure
     */
    static TokenMatcher match(Closure closure) {
        return match(null, closure)
    }

    static TokenMatcher match(String name, Closure closure) {
        return new ClosureMatcher(name: name, condition: closure)
    }

    /**
     * Match a sequence of matchers
     */
    static TokenMatcher sequence(List<TokenMatcher> matchers, boolean selfReset = false) {
        return sequence(null, matchers, selfReset)
    }

    static TokenMatcher sequence(String name, List<TokenMatcher> matchers, boolean selfReset = false) {
        return new SequenceMatcher(name: name, matchers: matchers, selfReset: selfReset)
    }

    static TokenMatcher optional(TokenMatcher matcher) {
        return optional(null, matcher)
    }

    static TokenMatcher optional(String name, TokenMatcher matcher) {
        return new OptionalMatcher(name: name, matcher: matcher)
    }

    static TokenMatcher not(TokenMatcher matcher) {
        return not(null, matcher)
    }

    static TokenMatcher not(String name, TokenMatcher matcher) {
        return new NotMatcher(name: name, matcher: matcher)
    }

    static TokenMatcher zeroOrMore(TokenMatcher matcher) {
        return zeroOrMore(null, matcher)
    }

    static TokenMatcher zeroOrMore(String name, TokenMatcher matcher) {
        return new ZeroOrMoreMatcher(name: name, matcher: matcher)
    }

    static TokenMatcher oneOrMore(TokenMatcher matcher) {
        return oneOrMore(null, matcher)
    }

    static TokenMatcher oneOrMore(String name, TokenMatcher matcher) {
        return new OneOrMoreMatcher(name: name, matcher: matcher)
    }

    static TokenMatcher firstOf(List<TokenMatcher> matchers) {
        return firstOf(null, matchers)
    }

    static TokenMatcher firstOf(String name, List<TokenMatcher> matchers) {
        return new FirstOfMatcher(name: name, matchers: matchers)
    }

    /**
     * Only matches one token
     */
    static abstract class SimpleMatcher extends TokenMatcher {
    }

    static class ClosureMatcher extends SimpleMatcher {
        Object value
        Closure condition

        @Override
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            return condition(context, props, value)
        }
    }

    static class NotMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            return !matcher.matches(context, false, props, action)
        }
    }

    static class SequenceMatcher extends TokenMatcher {
        List<TokenMatcher> matchers = []

        @Override
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            def result = true
            for (def matcher: matchers) {
                if (!matcher.matches(context, false, props, action)) {
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
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            matcher.matches(context, false, props, action)

            return true
        }
    }

    static class ZeroOrMoreMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            while (matcher.matches(context, false, props, action)) {
            }

            return true
        }
    }

    static class OneOrMoreMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            def count = 0
            while (matcher.matches(context, false, props, action)) {
                count ++
            }

            return count > 0
        }
    }

    static class FirstOfMatcher extends TokenMatcher {
        List<TokenMatcher> matchers = []

        @Override
        protected boolean doMatch(ParserContext context, Map<String, Object> props = [:], Closure action = null) {
            def result = false
            for (def matcher: matchers) {
                if (matcher.matches(context, false, props, action)) {
                    result = true
                    break
                }
            }

            return result
        }
    }

}
