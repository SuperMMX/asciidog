package org.supermmx.asciidog.parser.inline

import static org.supermmx.asciidog.parser.TokenMatcher.*;

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.Inline
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.InlineContainer
import org.supermmx.asciidog.ast.InlineInfo
import org.supermmx.asciidog.ast.AttributeReferenceNode
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.action.AttributeEntryParser
import org.supermmx.asciidog.parser.inline.InlineContext
import org.supermmx.asciidog.parser.inline.InlineParserPlugin

/*
 import org.supermmx.asciidog.ast.AttributeSettingNode
 import org.supermmx.asciidog.ast.CounterSettingNode
 */

import java.util.regex.Matcher

/**
 * Attribute Refrence parser plugin
 */
class AttributeReferenceParser extends InlineParserPlugin {
    static final def ATTRIBUTE_REFERENCE_PATTERN = ~'''(?Usxm)
(\\\\?)             # 1, escape
\\{
(                   # 2
  (set|counter2?)   # 3, set or counter
  :
  ([^:]+?)          # 4, attribute name
  (?:
    :
    (.*?)           # 5, attribute value
  )?
  |
  (                 # 6, attribute name
    [\\w-.]+?
  )
)
\\}
'''

    static final String ID = 'plugin:parser:inline:attribute_reference'

    static final String INLINE_PROPERTY_ATTRIBUTE_NAME = 'attrName'

    // TODO: set, counter and counter2
    static final CHECK_MATCHER = sequence([
        literal('{'),
        AttributeEntryParser.ATTRIBUTE_NAME_MATCHER,
        literal('}')
    ])

    static final Closure CHECK_ACTION = { String name, ParserContext context, Map<String, Object> props, boolean matched ->
        if (!matched) {
            return
        }

        if (name == 'name') {
            context.inlineContext[INLINE_PROPERTY_ATTRIBUTE_NAME] = context.lexer.joinTokensFromMark()
        }
    }

    AttributeReferenceParser() {
        id = ID
        nodeType = Node.Type.ATTRIBUTE_REFERENCE

        pattern = ATTRIBUTE_REFERENCE_PATTERN
    }

    @Override
    protected boolean doCheckStart(ParserContext context) {
        if (context.lexer.peek().value != '{') {
            return false
        }

        context.inlineContext = new InlineContext()

        return CHECK_MATCHER.matches(context, false, [:], CHECK_ACTION)
    }

    @Override
    protected boolean doCheckEnd(ParserContext context) {
        context.inlineContext = null

        return true
    }

    @Override
    protected Inline doParse(ParserContext context, InlineContainer parent) {
        // TODO: handle set, counter and counter2

        // check header attributes
        def inlineContext = context.inlineContext
        def attrName = inlineContext[INLINE_PROPERTY_ATTRIBUTE_NAME]

        def inline = new AttributeReferenceNode(name: attrName)

        return inline
    }

    @Override
    protected List<Inline> createNodes(Matcher m, List<String> groups) {
        Inline inline = null;

        def action = groups[3]
        if (action == 'set') {
            //inline = new AttributeSettingNode()
        } else if (action == 'counter'
                   || action == 'counter2') {
            //inline = new CounterSettingNode()
        } else {
            // normal reference
            inline = new AttributeReferenceNode()
            inline.name = groups[6]
        }

        return [ inline ]
    }

    @Override
    protected boolean fillNodes(List<InlineInfo> infoList, Matcher m, List<String> groups) {
        infoList[0].with {
            inlineNode.escaped = (groups[0] == '')

            contentStart = m.start(2)
            contentEnd = m.end(2)
        }

        return true
    }
}
