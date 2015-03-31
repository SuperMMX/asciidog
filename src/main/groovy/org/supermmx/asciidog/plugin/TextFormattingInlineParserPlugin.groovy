package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node

import java.util.regex.Matcher

class TextFormattingInlineParserPlugin extends InlineParserPlugin {
    FormattingNode.Type formattingType

    TextFormattingInlineParserPlugin() {
        nodeType = Node.Type.INLINE_FORMATTED_TEXT
    }

    Inline createNode() {
        FormattingNode inline = new FormattingNode()

        inline.info.constrained = this.constrained
        inline.formattingType = formattingType

        return inline
    }

    protected boolean fillNode(Inline inline, Matcher m, List<String> groups) {
        inline.info.with {
            escaped = (groups[1] != '')

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

