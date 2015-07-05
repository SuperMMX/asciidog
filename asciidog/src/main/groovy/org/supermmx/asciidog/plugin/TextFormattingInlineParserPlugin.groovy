package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.Node

import java.util.regex.Matcher

class TextFormattingInlineParserPlugin extends InlineParserPlugin {
    FormattingNode.FormattingType formattingType

    TextFormattingInlineParserPlugin() {
        nodeType = Node.Type.FORMATTING
    }

    @Override
    protected List<Inline> createNodes(Matcher m, List<String> groups) {
        FormattingNode inline = new FormattingNode()

        inline.formattingType = formattingType

        return [ inline ]
    }

    @Override
    protected boolean fillNodes(List<InlineInfo> infoList, Matcher m, List<String> groups) {
        infoList[0].with {
            contentStart = m.start(3)
            contentEnd = m.end(3)

            inlineNode.with {
                escaped = (groups[1] != '')

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

