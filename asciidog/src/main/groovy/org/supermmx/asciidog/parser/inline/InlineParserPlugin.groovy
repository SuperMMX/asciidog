package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.plugin.ParserPlugin

import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * Inline parser plugin that parses the inline nodes
 * it is interested in
 */
abstract class InlineParserPlugin extends ParserPlugin {
    boolean constrained
    Pattern pattern

    boolean checkStart(ParserContext context) {
        return doCheckStart(context)
    }

    protected boolean doCheckStart(ParserContext context) {
        return false
    }

    boolean checkEnd(ParserContext context) {
        return doCheckEnd(context)
    }

    protected boolean doCheckEnd(ParserContext context) {
        return true
    }

    public Inline parse(ParserContext context, InlineContainer parent) {
        return doParse(context, parent)
    }

    protected Inline doParse(ParserContext context, InlineContainer parent) {
        return null
    }
}
