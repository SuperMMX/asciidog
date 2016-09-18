package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
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

        Section section = new Section(parent: parent,
                                      document: parent.document,
                                      level: level)
        fillBlockFromHeader(section, header)

        section.title = title

        return section
    }

    @Override
    protected List<Node> doParseChildren(ParserContext context, Block parent) {
        return null
    }

    @Override
    protected boolean doIsValidChild(ParserContext context, Block parent, BlockHeader header) {
        return true
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
