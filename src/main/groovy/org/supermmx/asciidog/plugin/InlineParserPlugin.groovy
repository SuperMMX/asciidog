package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.TextNode

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
    Inline parse(Matcher m, List<String> groups) {
        Inline inline = createNode();

        if (inline != null) {
            def success = fillNode(inline, m, groups)
            if (!success) {
                inline = null
            }
        }

        // create a text node
        if (inline == null) {
            inline = new TextNode(groups[0], m.start())
        }

        return inline
    }

    /**
     * Create an empty node with necessary data
     *
     * @return the newly created inline node
     */
    protected abstract Inline createNode()

    /**
     * Fill data from regex to the inline
     *
     * @return successfully fill the data or not
     */
    protected abstract boolean fillNode(Inline inline, Matcher m, List<String> groups)
}
