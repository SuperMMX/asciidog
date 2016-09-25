package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.Utils
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section
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

    static final String HEADER_PROPERTY_SECTION_TITLE = 'secTitle'
    static final String HEADER_PROPERTY_SECTION_LEVEL = 'secLevel'

    static final String ID = 'plugin:parser:block:section'

    SectionParser() {
        nodeType = Node.Type.SECTION
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        def (level, title) = isSection(line)

        if (level == -1) {
            return false
        }

        if (header != null) {
            header.type = Node.Type.SECTION
            header.properties[(HEADER_PROPERTY_SECTION_LEVEL)] = level
            header.properties[(HEADER_PROPERTY_SECTION_TITLE)] = title
        }

        return true
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def level = header.properties[(HEADER_PROPERTY_SECTION_LEVEL)]
        def title = header.properties[(HEADER_PROPERTY_SECTION_TITLE)]

        def reader = context.reader

        // check the parsed level and the expected level
        def expectedLevel = context.expectedSectionLevel
        if (level != expectedLevel) {
            log.error('{}: Wrong section level {}, expected level is {}',
                      reader.cursor, level, expectedLevel)

            userLog.error('{}: Wrong section level {}, expected level is {}',
                          reader.cursor, level, expectedLevel)

            return null
        }

        Section section = new Section(level: level)
        fillBlockFromHeader(section, header)

        section.title = title
        Utils.generateId(section)

        reader.nextLine()

        return section
    }

    protected String doGetNextChildParser(ParserContext context, Block block) {
        Section section = block

        BlockHeader header = context.blockHeader
        if (header == null) {
            header = nextBlockHeader(context, true)
        }

        String childParser = null

        if (header != null && header.type != null) {
            childParser = header.parserId

            if (header.type == Node.Type.SECTION) {
                // check level
                def level = header.properties[HEADER_PROPERTY_SECTION_LEVEL]
                if (level <= section.level) {
                    childParser = null
                } else if (level >= section.level + 1) {
                    context.childParserProps.expectedSectionLevel = section.level + 1
                }
            }
        }

        return childParser
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
