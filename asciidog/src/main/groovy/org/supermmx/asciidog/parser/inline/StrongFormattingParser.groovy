package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.StrongFormattingNode

class StrongFormattingParser extends TextFormattingParser {
    static final def STRONG_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escaped
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
\\*\\*
(.+?)               # 3, content
\\*\\*
'''
    static final String ID = 'plugin:parser:inline:formatting:strong'

    StrongFormattingParser() {
        id = ID
        nodeType = Node.Type.STRONG
        pattern = STRONG_PATTERN
    }

    FormattingNode createFormattingNode() {
        return new StrongFormattingNode()
    }
}
