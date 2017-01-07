package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.EmphasisFormattingNode

class EmphasisFormattingParser extends TextFormattingParser {
    static final def EMPHASIS_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escaped
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
__
(.+?)               # 3, content
__
'''
    static final String ID = 'plugin:parser:inline:formatting:emphasis'

    EmphasisFormattingParser() {
        id = ID
        nodeType = Node.Type.EMPHASIS
        pattern = EMPHASIS_PATTERN
    }

    FormattingNode createFormattingNode() {
        return new EmphasisFormattingNode()
    }
}
