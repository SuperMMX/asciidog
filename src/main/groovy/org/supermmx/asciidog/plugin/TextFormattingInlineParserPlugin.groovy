package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node

import java.util.regex.Matcher

class TextFormattingInlineParserPlugin extends InlineParserPlugin {
    FormattingNode.Type formattingType

    TextFormattingInlineParserPlugin() {
        nodeType = Node.Type.INLINE_FORMATTED_TEXT
    }

    Inline parse(Matcher m, List<String> groups) {
        FormattingNode inline = new FormattingNode()

        inline.formattingType = formattingType

        inline.info.with {
            escaped = (groups[1] != '')
            constrained = this.constrained

            contentStart = m.start(3)
            contentEnd = m.end(3)
        }

        return inline
    }
    
}

