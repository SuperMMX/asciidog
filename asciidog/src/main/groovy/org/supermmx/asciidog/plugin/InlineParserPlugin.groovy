package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
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
    InlineInfo parse(Matcher m, List<String> groups) {
        InlineInfo info = new InlineInfo()

        Inline inline = createNode(m, groups, info)

        if (inline != null) {
            def success = fillNode(inline, m, groups, info)
            if (!success) {
                inline = null
                info = null
            }
        } else {
            info = null
        }

        // create a text node
        if (inline == null) {
            inline = new TextNode(groups[0])
            info.with {
                start = groups[0].start
                end = groups[0].end
                contentStart = start
                contentEnd = end
            }
        }

        if (info != null) {
            info.inlineNode = inline
        }

        return info
    }

    /**
     * Create an empty node with necessary data
     *
     * @return the newly created inline node
     */
    protected abstract Inline createNode(Matcher m, List<String> groups, InlineInfo info)

    /**
     * Fill data from regex to the inline
     *
     * @return successfully fill the data or not
     */
    protected abstract boolean fillNode(Inline inline, Matcher m, List<String> groups, InlineInfo info)
}
