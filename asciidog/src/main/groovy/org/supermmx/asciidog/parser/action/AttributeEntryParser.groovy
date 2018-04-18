package org.supermmx.asciidog.parser.action

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Parser
import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.block.BlockParserPlugin
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class AttributeEntryParser extends BlockParserPlugin {
    static final def ATTRIBUTE_PATTERN = ~"""(?x)
:
(
  !?\\w.*?      # 1, attribute name
)
:
(?:
  \\p{Blank}+
  (.*)          # 2, attribute value
)?
"""

    static final String HEADER_PROPERTY_ATTRIBUTE_NAME = 'attrName'
    static final String HEADER_PROPERTY_ATTRIBUTE_FIRST_LINE = 'attrFirstLine'

    static final String ID = 'plugin:parser:action:define_attribute'

    static final TokenMatcher ATTRIBUTE_NAME_MATCHER =
        // FIXME: tokens with consecutive same punctuation characters
        oneOrMore('name', firstOf([
        literal('_'),
        literal('-'),
        type(Token.Type.TEXT),
        type(Token.Type.DIGITS)
    ]))

    static final TokenMatcher CHECK_MATCHER = sequence([
        literal(':'),
        // name
        sequence('fullName', [
            optional(literal('!')),
            ATTRIBUTE_NAME_MATCHER,
        ]),
        literal(':'),
        optional(type(Token.Type.WHITE_SPACES))
    ])

    // TODO: multi-line value
    /**
     * Always match the next token, but will NOT consume it.
     * This will be called when EOL occurs when parsing the value as inlines
     */
    static final TokenMatcher VALUE_END_MATCHER = match { context, props, valueObj ->
        true
    }

    AttributeEntryParser() {
        nodeType = Node.Type.DEFINE_ATTRIBUTE
        id = ID
    }

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        def isStart = CHECK_MATCHER.matches(context, false, ['header': header],
                                            { name, matcherContext, props, matched ->
                if (!matched) {
                    return
                }

                if (name == 'fullName') {
                    def matcherHeader = props.header
                    matcherHeader.properties[HEADER_PROPERTY_ATTRIBUTE_NAME] = matcherContext.lexer.joinTokensFromMark()
                }
            })

        return isStart
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        def name = header?.properties[HEADER_PROPERTY_ATTRIBUTE_NAME]

        // TODO: value of multiple lines
        AttributeEntry attr = new AttributeEntry(name: name)

        // parse value
        Parser.parseInlines(context, attr, VALUE_END_MATCHER)

        context.attributes << attr

        return attr
    }

    /**
     * Whether the line represents an attribute definition, like
     *
     * :attr-name: attribute value
     *
     * @param line the line to check
     *
     * @return the attribute name, null if not an attribute
     *         the attribute value, null if not an attribute
     */
    protected static List<String> isAttribute(String line) {
        if (line == null) {
            return [ null, null ]
        }

        def m = ATTRIBUTE_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null ]
        }

        String key = m[0][1]
        String value = m[0][2]

        return [ key, value ]
    }
}
