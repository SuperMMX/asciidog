package org.supermmx.asciidog.parser.inline

import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.FormattingNode
import org.supermmx.asciidog.ast.MarkFormattingNode

class MarkFormattingParser extends TextFormattingParser {
    static final def MARK_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escaped
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
##
(.+?)               # 3, content
##
'''
    static final String ID = 'plugin:parser:inline:formatting:mark'

    MarkFormattingParser() {
        id = ID
        nodeType = Node.Type.MARK
        tag = '##'
    }

    FormattingNode createFormattingNode() {
        return new MarkFormattingNode()
    }
}
