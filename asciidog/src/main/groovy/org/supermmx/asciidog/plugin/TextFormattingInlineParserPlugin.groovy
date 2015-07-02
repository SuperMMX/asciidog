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

    protected Inline createNode(Matcher m, List<String> groups, InlineInfo info) {
        FormattingNode inline = new FormattingNode()

        inline.formattingType = formattingType

        return inline
    }

    protected boolean fillNode(Inline inline, Matcher m, List<String> groups, InlineInfo info) {
        inline.escaped = (groups[1] != '')

        def attrsStr = groups[2]
        if (attrsStr != null) {
            def attrs = Parser.parseAttributes(attrsStr)
            inline.attributes.putAll(attrs)
        }

        info.with {
            contentStart = m.start(3)
            contentEnd = m.end(3)
        }

        return true
    }
    
}

