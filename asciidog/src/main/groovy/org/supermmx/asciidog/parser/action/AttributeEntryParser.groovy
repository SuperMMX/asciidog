package org.supermmx.asciidog.parser.action

import org.supermmx.asciidog.ast.AttributeEntry
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext
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

    AttributeEntryParser() {
        nodeType = Node.Type.DEFINE_ATTRIBUTE
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        log.debug('doCheckStart: line = {}', line)
        def (name, value) = isAttribute(line)

        if (name == null) {
            return false
        }

        if (header != null) {
            header.type = Node.Type.DEFINE_ATTRIBUTE
            header.properties[HEADER_PROPERTY_ATTRIBUTE_NAME] = name
            header.properties[HEADER_PROPERTY_ATTRIBUTE_FIRST_LINE] = value
        }

        return true
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def name = header?.properties[HEADER_PROPERTY_ATTRIBUTE_NAME]
        def value = header?.properties[HEADER_PROPERTY_ATTRIBUTE_FIRST_LINE]

        // FIXME: value of multiple lines
        AttributeEntry attr = new AttributeEntry(name: name, value: value)

        context.attributes << attr

        context.reader.nextLine()

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
