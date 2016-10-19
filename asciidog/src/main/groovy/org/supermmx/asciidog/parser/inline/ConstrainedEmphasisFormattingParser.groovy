package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.EmphasisFormattingNode
import org.supermmx.asciidog.plugin.TextFormattingInlineParserPlugin

class ConstrainedEmphasisFormattingParser extends TextFormattingInlineParserPlugin {
    static final def EMPHASIS_CONSTRAINED_PATTERN = ~'''(?Usxm)
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
_
(                   # 3, text
  [\\S&&[^*]]
  |
  [\\S&&[^*]] .*? [\\S&&[^*]]
)
_
(?!
  [\\w\\*]
)
'''
    static final String ID = 'plugin:parser:inline:formatting:emphasis:constrained'

    ConstrainedEmphasisFormattingParser() {
        id = ID
        nodeType = Node.Type.EMPHASIS
        pattern = EMPHASIS_CONSTRAINED_PATTERN
    }

    FormattingNode createFormattingNode() {
        return new EmphasisFormattingNode(constrained: true)
    }
}
