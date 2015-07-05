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

    @Override
    protected List<Inline> createNodes(Matcher m, List<String> groups) {
        CrossReferenceNode xrNode = new CrossReferenceNode()

        return [ xrNode ]
    }

    @Override
    protected boolean fillNodes(List<Inline> infoList, Matcher m, List<String> groups) {
        infoList[0].with {
            contentStart = m.start(3)
            contentEnd = m.end(3)

            inlineNode.with {
                escaped = (groups[1] != '')

                xrefId = groups[3]

                def attrsStr = groups[2]
                if (attrsStr != null) {
                    def attrs = Parser.parseAttributes(attrsStr)
                    attributes.putAll(attrs)
                }
            }
        }

        return true
    }
}

