package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.Utils
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Paragraph
import org.supermmx.asciidog.ast.Section
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher

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
    static final String HEADER_PROPERTY_MARK_TOKEN = 'secMarkToken'

    static final String ID = 'plugin:parser:block:section'

    static final TokenMatcher CHECK_MATCHER = sequence([
        match('mark', { context, props, valueObj ->
            def token = context.lexer.next()
            if (token.value == null) {
                return false
            }
            def value = token.value
            def size = value.length()
            value.charAt(0) == '=' && size >= 1 && size <= 5
        }),
        type(Token.Type.WHITE_SPACES),
        not(type(Token.Type.EOL), true)
    ])

    static final Closure CHECK_ACTION = { String name, ParserContext context, Map<String, Object> props, boolean matched ->
        if (!matched) {
            return
        }

        def header = props.header

        if (name == 'mark') {
            def tokens = context.lexer.tokensFromMark
            header.properties[HEADER_PROPERTY_SECTION_LEVEL] = tokens[0].value.length() - 1
            header.properties[HEADER_PROPERTY_MARK_TOKEN] = tokens[0]
        }
    }

    SectionParser() {
        nodeType = Node.Type.SECTION
        id = ID
    }

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        if (header != null && header.type != null) {
            return header.type == nodeType
        }

        def lexer = context.lexer
        def isStart = false

        isStart = CHECK_MATCHER.matches(context, ["header": header], false, CHECK_ACTION)

        return isStart
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lexer = context.lexer

        log.trace '=== next token = {}', lexer.peek()

        def level = header.properties[HEADER_PROPERTY_SECTION_LEVEL]

        // TODO: parse the title as inlines
        def title = lexer.combineTo(type(Token.Type.EOL))
        //def inlines = parseInlines(context, type(Token.Type.EOL))

        // check the parsed level and the expected level
        def expectedLevel = context.expectedSectionLevel
        if (expectedLevel != null && level != expectedLevel) {
            def markToken = header.properties[HEADER_PROPERTY_MARK_TOKEN]
            log.error('{},{}: Wrong section level {}, expected level is {}',
                      markToken.row, markToken.col, level, expectedLevel)

            userLog.error('{},{}: Wrong section level {}, expected level is {}',
                          markToken.row, markToken.col, level, expectedLevel)

            context.stop = true

            return null
        }

        Section section = new Section(level: level)
        fillBlockFromHeader(section, header)

        section.title = title
        Utils.generateId(section)

        context.document.references[section.id] = section

        return section
    }

    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return [
            // find blocks except sibling and higher level sections
            ChildParserInfo.find().doBeforeParsing { newContext, block ->
                def result = true

                def section = block
                def header = newContext.blockHeader
                if (header != null && header.type != null) {
                    if (header.type == Node.Type.SECTION) {
                        // check level
                        def level = header.properties[HEADER_PROPERTY_SECTION_LEVEL]
                        if (level <= section.level) {
                            result = false
                        } else if (level >= section.level + 1) {
                            newContext.childParserProps.expectedSectionLevel = section.level + 1
                        }
                    }
                } else {
                    result = false
                }

                return result
            }
        ]
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
