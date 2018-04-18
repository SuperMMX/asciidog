package org.supermmx.asciidog.parser.inline

import static org.supermmx.asciidog.parser.TokenMatcher.*;

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.CrossReferenceNode
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.inline.InlineParserPlugin

import java.util.regex.Matcher

/**
 * Plugin for Cross Reference
 */
class CrossReferenceParser extends InlineParserPlugin {
    static final def CROSS_REFERENCE_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escape
(?:
  \\[
     ([^\\]]+?)     # 2, Attributes
  \\]
)?
<<
(.*?)          # 3, id, allow any characters
>>
'''
    static final String ID = 'plugin:parser:inline:cross_reference'

    static final START_MATCHER = literal('<<')
    static final END_MATCHER = literal('>>')

    CrossReferenceParser() {
        id = ID
        nodeType = Node.Type.CROSS_REFERENCE

        pattern = CROSS_REFERENCE_PATTERN
    }

    @Override
    protected boolean doCheckStart(ParserContext context) {
        return START_MATCHER.matches(context, false, [:], null)
    }

    @Override
    protected boolean doCheckEnd(ParserContext context) {
        return END_MATCHER.matches(context, false, [:], null)

        return true
    }

    @Override
    protected Inline doParse(ParserContext context, InlineContainer parent) {
        def lexer = context.lexer

        // get the reference id
        def id = context.lexer.combineTo(firstOf([
            literal(','),
            literal('>>'),
        ]), false)

        if (lexer.peek().value == ',') {
            lexter.next()
        }

        def inline = new CrossReferenceNode(xrefId: id)

        // the reference text will be parsed in the main inline parsing loop

        return inline
    }

    @Override
    protected List<Inline> createNodes(Matcher m, List<String> groups) {
        CrossReferenceNode xrNode = new CrossReferenceNode()

        return [ xrNode ]
    }

    @Override
    protected boolean fillNodes(List<Inline> infoList, Matcher m, List<String> groups) {
        infoList[0].with {
            contentStart = m.start(3)
            contentEnd = m.end(3)

            inlineNode.with {
                escaped = (groups[1] != '')

                xrefId = groups[3]

                def attrsStr = groups[2]
                if (attrsStr != null) {
                    def attrs = Parser.parseAttributes(attrsStr)
                    attributes.putAll(attrs)
                }
            }
        }

        return true
    }
}
