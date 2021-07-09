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
abstract class StyledBlockParser extends BlockParserPlugin {
    static final String HEADER_PROPERTY_STYLED_DELIMITER = 'styledDelimiter'
    static final String HEADER_PROPERTY_HAS_DELIMITER = 'hasDelimiter'
    static final String HEADER_PROPERTY_IS_OPEN_BLOCK = 'isOpenBlock'

    static final Closure CHECK_ACTION = { String name, ParserContext context, Map<String, Object> props, boolean matched ->
        if (!matched) {
            return
        }

        def header = props.header

        if (name == 'delimiter') {
            def tokens = context.lexer.tokensFromMark
            header.properties[HEADER_PROPERTY_STYLED_DELIMITER] = tokens[0].value
        }
    }

    protected String style
    protected String delimiter

    protected TokenMatcher delimiterMatcher
    protected TokenMatcher endDelimiterMatcher

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        // get style from attribute
        def headerStyle = header?.attributes.find { true }?.key

        if (delimiterMatcher == null) {
            delimiterMatcher = sequence([
                firstOf('delimiter', [ literal(delimiter), literal(OpenBlockParser.OPEN_DELIMITER) ]),
                type(Token.Type.EOL)
            ])
        }

        def hasDelimiter = delimiterMatcher.matches(context, ["header": header], false, CHECK_ACTION)
        def headerDelimiter = header.properties[HEADER_PROPERTY_STYLED_DELIMITER]

        def isStart = false
        def isOpenBlock = false;

        if (headerStyle == null) {
            // determined by the delimiter
            if (headerDelimiter == delimiter) {
                isStart = true
                isOpenBlock = (headerDelimiter == OpenBlockParser.OPEN_DELIMITER)
            }
        } else {
            // determined by the header style attribute
            if (headerStyle == style) {
                if (headerDelimiter == null
                    || headerDelimiter == delimiter
                    || headerDelimiter == OpenBlockParser.OPEN_DELIMITER) {
                    // style attribute is consistent with the delimiter
                    isStart = true
                    isOpenBlock = (headerDelimiter == OpenBlockParser.OPEN_DELIMITER)
                } else {
                    // conflict
                    log.error "Conflict style and delimiter"
                    context.stop = true
                }
            }
        }

        // matched for this parser
        if (isStart) {
            header.properties[HEADER_PROPERTY_HAS_DELIMITER] = (headerStyle == null)
            header.properties[HEADER_PROPERTY_IS_OPEN_BLOCK] = isOpenBlock

            endDelimiterMatcher = sequence([
                literal(headerDelimiter),
                type(Token.Type.EOL)
            ])
        }

        return isStart
    }

    @Override
    protected Block createBlock(ParserContext context, Block parent, BlockHeader header) {
        def styledBlock = doCreateBlock(context, parent, header)

        styledBlock.hasDelimiter = header.properties[HEADER_PROPERTY_HAS_DELIMITER]
        styledBlock.isOpenBlock = header.properties[HEADER_PROPERTY_IS_OPEN_BLOCK]

        if (header.properties[HEADER_PROPERTY_STYLED_DELIMITER] != null) {
            context.paragraphEndingCheckers << this
        }

        return styledBlock
    }

    @Override
    protected boolean doToEndParagraph(ParserContext context) {
        return endDelimiterMatcher.matches(context, null, true)
    }

    @Override
    protected boolean doNeedToFindNextChildParser(ParserContext context) {
        return !endDelimiterMatcher.matches(context, null, false)
    }

    // TODO: resuse the following to check non-section blocks for parsers like Preamble
    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return [
            ChildParserInfo.find().doBeforeParsing { newContext, parent ->
                def result = false
                def header = newContext.blockHeader

                if (header?.type != Node.Type.SECTION) {
                    // other blocks than a section
                    result = true
                }

                return result
            }
        ]
    }
}
