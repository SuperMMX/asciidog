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

    /**
     * Parse the matched part.
     *
     * @param m the matcher which should only be used to
     *          retrieve data
     * @param groups the matching groups
     *
     * @return a list of parsed inline info
     */
    List<InlineInfo> parse(Matcher m, List<String> groups) {
        List<Inline> inlines = createNodes(m, groups)

        List<InlineInfo> infoList = []
        inlines.eachWithIndex { inline, index ->
            InlineInfo info = new InlineInfo()
            info.inlineNode = inline

            // fill the start and end for the top level node
            if (index == 0) {
                info.start = m.start(0)
                info.end = m.end(0)
            }

            infoList << info
        }

        if (inlines.size() > 0) {
            def success = fillNodes(infoList, m, groups)
            if (!success) {
                inlines = []
                infoList = []
            }
        }

        // create a text node
        if (inlines == null || inlines.size() == 0) {
            Inline inline = new TextNode(groups[0])
            InlineInfo info = new InlineInfo()

            info.with {
                inlineNode = inline

                start = m.start(0)
                end = m.end(0)
                contentStart = start
                contentEnd = end
            }
        }

        return infoList
    }

    /**
     * Create a list of empty nodes with necessary data
     *
     * @return the newly created inline nodes
     */
    protected abstract List<Inline> createNodes(Matcher m, List<String> groups)

    /**
     * Fill data from regex to the inline info list
     *
     * @return successfully fill the data or not
     */
    protected abstract boolean fillNodes(List<Inline> infoList, Matcher m, List<String> groups)
}
