package org.supermmx.asciidog.plugin

import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.FormattingNode

import java.util.regex.Matcher

class TextFormattingInlineParserPlugin extends InlineParserPlugin {
    FormattingNode.Type formattingType

    Inline parse(Matcher m, List<String> groups) {
        FormattingNode inline = new FormattingNode()

        inline.escaped = (groups[1] != '')

        inline.constrained = constrained
        inline.formattingType = formattingType

        inline.contentStart = m.start(2)
        inline.contentEnd = m.end(2)

        return inline
    }
    
}

