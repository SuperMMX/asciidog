package org.supermmx.asciidog.parser

import org.supermmx.asciidog.lexer.Lexer
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import java.util.regex.Pattern

/**
 * Match tokens
 */
abstract class TokenMatcher {
    protected abstract boolean doMatch(ParserContext context, BlockHeader header = null)

    /**
     * Closure called when the matching is finished { context, header, matched -> code }
     */
    Closure action

    /**
     * Match against this matcher starting from next token.
     * The tokens are consumed if matched
     */
    boolean matches(ParserContext context, BlockHeader header = null) {
        context.lexer.mark()

        def matched = doMatch(context, header)

        action?.call(context, header, matched)

        if (matched) {
            context.lexer.clearMark()
        } else {
            context.lexer.reset()
        }

        return matched
    }

    /**
     * Match the token value
     */
    static TokenMatcher literal(String value, Closure action = null) {
        return new ClosureMatcher(value: value, condition: { context, header, valueObj ->
            def token = context.lexer.next()
            token?.value == valueObj
        }, action: action)
    }

    static TokenMatcher regex(String regex, Closure action = null) {
        return regexPattern(~regex, action)
    }

    static TokenMatcher regexPattern(Pattern pattern, Closure action = null) {
        return new ClosureMatcher(value: pattern, condition: { context, header, valueObj ->
            def token = context.lexer.next()
            pattern.matcher(token?.value).matches()
        }, action: action)
    }

    /**
     * Match the token type
     */
    static TokenMatcher type(Token.Type type, Closure action = null) {
        return new ClosureMatcher(value: type, condition: { context, header, typeObj ->
            def token = context.lexer.next()
            token?.type == typeObj
        }, action: action)
    }

    /**
     * Match the token with custom closure
     */
    static TokenMatcher match(Closure closure, Closure action = null) {
        return new ClosureMatcher(condition: closure, action: action)
    }

    /**
     * Match a sequence of matchers
     */
    static TokenMatcher sequence(List<TokenMatcher> matchers, Closure action = null) {
        return new SequenceMatcher(matchers: matchers, action: action)
    }

    static TokenMatcher optional(TokenMatcher matcher, Closure action = null) {
        return new OptionalMatcher(matcher: matcher, action: action)
    }

    static TokenMatcher not(TokenMatcher matcher, Closure action = null) {
        return new NotMatcher(matcher: matcher, action: action)
    }

    static TokenMatcher zeroOrMore(TokenMatcher matcher, Closure action = null) {
        return new ZeroOrMoreMatcher(matcher: matcher, action: action)
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
        protected boolean doMatch(ParserContext context, BlockHeader header = null) {
            return condition(context, header, value)
        }
    }

    static class NotMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        protected boolean doMatch(ParserContext context, BlockHeader header = null) {
            return !matcher.matches(context, header)
        }
    }

    static class SequenceMatcher extends TokenMatcher {
        List<TokenMatcher> matchers = []

        @Override
        protected boolean doMatch(ParserContext context, BlockHeader header) {
            def result = true
            for (def matcher: matchers) {
                if (!matcher.matches(context, header)) {
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
        protected boolean doMatch(ParserContext context, BlockHeader header = null) {
            matcher.matches(context, header)

            return true
        }
    }

    static class ZeroOrMoreMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        protected boolean doMatch(ParserContext context, BlockHeader header = null) {
            while (matcher.matches(context, header)) {
            }

            return true
        }
    }

    static class OneOrMoreMatcher extends TokenMatcher {
        TokenMatcher matcher

        @Override
        protected boolean doMatch(ParserContext context, BlockHeader header = null) {
            def count = 0
            while (matcher.matches(context, header)) {
                count ++
            }

            return count > 0
        }
    }
}
