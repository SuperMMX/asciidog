package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.StrongFormattingNode

class ConstrainedStrongFormattingParser extends TextFormattingParser {
    static final def STRONG_CONSTRAINED_PATTERN = ~'''(?Usxm)
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
\\*
(                   # 3, text
  [\\S&&[^*]]
  |
  [\\S&&[^*]] .*? [\\S&&[^*]]
)
\\*
(?!
  [\\w\\*]
)
'''
    static final String ID = 'plugin:parser:inline:formatting:strong:constrained'

    ConstrainedStrongFormattingParser() {
        id = ID
        nodeType = Node.Type.STRONG
    }

    FormattingNode createFormattingNode() {
        return new StrongFormattingNode(constrained: true)
    }
}
