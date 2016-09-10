package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.parser.ParserContext

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class SectionParser extends BlockParserPlugin {
    static final def SECTION_PATTERN = ~'''(?x)
(                # 1, section identifier
  ={1,6}
)
\\p{Blank}+
(                # 2, whole title
  \\S+
  (?:\\p{Blank}+\\S+)*
)
\\s*
'''
    static final String ID = 'plugin:parser:block:section'

    SectionParser() {
        nodeType = Node.Type.SECTION
        id = ID
    }

    @Override
    boolean isStart(String line, BlockHeader header) {
        def (level, title) = isSection(line)

        if (level == -1) {
            return false
        }

        if (header != null) {
            header.type = Node.Type.SECTION
            header.properties[BlockHeader.SECTION_LEVEL] = secLevel
            header.properties[BlockHeader.SECTION_TITLE] = secTitle
        }

        return true
    }

    @Override
    Block parse(ParserContext context) {
        def reader = context.reader
        def parent = context.parent
        def parentParser = context.parentParser
        def blockHeader = context.blockHeader

        log.debug('Start parsing section, parent type: {}', parent.type)

        return doc
    }

    /**
     * Whether the line represents a section, like
     *
     * == Section Title
     *
     * @param line the line to check
     *
     * @return the section level, -1 if not a section,
     *         the section title, null if not a section,
     */
    public static List<Object> isSection(String line) {
        if (line == null) {
            return [ -1, null ]
        }

        def m = SECTION_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ -1, null ]
        }

        int level = m[0][1].length() - 1
        String title = m[0][2]

        return [ level, title ]
    }
}
