package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node

import java.util.regex.Pattern
import java.util.regex.Matcher

/**
 * Inline parser plugin that parses the inline nodes
 * it is interested in
 */
abstract class InlineParserPlugin extends ParserPlugin {
    boolean constrained
    Pattern pattern

    /**
     * Parse the matched part.
     *
     * @param m the matcher which should only be used to
     *          retrieve data
     * @param groups
     *
     * @return the parsed inline node
     */
    abstract Inline parse(Matcher m, List<String> groups)
}
