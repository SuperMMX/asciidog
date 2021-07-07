package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.MarkFormattingNode

class ConstrainedMarkFormattingParser extends TextFormattingParser {
    static final def MARK_CONSTRAINED_PATTERN = ~'''(?Usxm)
(?<=
  ^ | [^\\w;:}]
)
(\\\\?)             # 1, escape
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
(?<!
  [\\w*]
)
#
(                   # 3, text
  [\\S&&[^*]]
  |
  [\\S&&[^*]] .*? [\\S&&[^*]]
)
#
(?!
  [\\w\\*]
)
'''
    static final String ID = 'plugin:parser:inline:formatting:mark:constrained'

    ConstrainedMarkFormattingParser() {
        id = ID
        nodeType = Node.Type.MARK
        tag = '#'
    }

    FormattingNode createFormattingNode() {
        return new MarkFormattingNode(constrained: true)
    }
}
