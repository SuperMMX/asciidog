package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.Node

import java.util.regex.Matcher

/**
 * Plugin for Cross Reference
 */
class CrossReferenceInlineParserPlugin extends InlineParserPlugin {
    CrossReferenceInlineParserPlugin() {
        nodeType = Node.Type.CROSS_REFERENCE
        id = 'inline_parser_xref'
        pattern = Parser.CROSS_REFERENCE_PATTERN
    }

    Inline createNode(Matcher m, List<String> groups, InlineInfo info) {
        CrossReferenceNode xrNode = new CrossReferenceNode()

        return xrNode
    }

    protected boolean fillNode(Inline inline, Matcher m, List<String> groups, InlineInfo info) {
        inline.escaped = (groups[1] != '')
        inline.xrefId = groups[3]

        info.with {
            contentStart = m.start(3)
            contentEnd = m.end(3)

            def attrsStr = groups[2]
            if (attrsStr != null) {
                def attrs = Parser.parseAttributes(attrsStr)
                inline.attributes.putAll(attrs)
            }
        }

        return true
    }
}

