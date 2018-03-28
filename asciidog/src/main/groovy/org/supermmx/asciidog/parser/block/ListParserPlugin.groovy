package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.lexer.Token
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.ParserContext

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
abstract class ListParserPlugin extends BlockParserPlugin {
    static final def LIST_PATTERN = ~'''(?x)
^
(
  \\p{Blank}*    # 1, leading
)
(                # 2, list character
  -
  |
  [*.]{1,5}
)
\\p{Blank}+
(                # 3, content
  .*
)
$
'''
    static final def LIST_CONTINUATION_PATTERN = ~'''(?x)
^
(
  \\p{Blank}*    # 1, leading
)
\\+
$
'''
    static final String LIST_LEAD = 'listLead'
    static final String LIST_MARKER = 'listMarker'
    static final String LIST_MARKER_LEVEL = 'listMarkerLevel'

    static final TokenMatcher CHECK_MATCHER = sequence([
        optional(type(Token.Type.WHITE_SPACES,
                      { ParserContext context, BlockHeader header, boolean matched ->
                def lead = ''
                if (matched) {
                    def tokens = context.lexer.tokensFromMark
                    lead = tokens[0].value
                }
                header.properties[LIST_LEAD] = lead
            })),
        match({ token, valueObj ->
            log.info '==== matcher token = {}', token
            def value = token.value
            def marker = value.charAt(0)
            def length = value.length()
            if (value == '-'
                || ((marker == (char)'*'
                     || marker == (char)'.')
                    && length >= 1 && length <= 5)) {
                return true
            }

            return false
        },
              { ParserContext context, BlockHeader header, boolean matched ->
                if (matched) {
                    def marker = context.lexer.tokensFromMark.collect { it.value }.join()
                    header.properties[LIST_MARKER_LEVEL] = marker.length()
                    header.properties[LIST_MARKER] = marker[0]
                }
            }),
        type(Token.Type.WHITE_SPACES),
        not(type(Token.Type.EOL))
    ])

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        log.info '==== list parser check start: header = {}', header
        if (header?.type != null
            && header?.type != nodeType) {
            return false
        }

        if (header?.type == nodeType) {
            return true
        }

        def isStart = CHECK_MATCHER.matches(context, header)
        if (!isStart) {
            return false
        }

        def marker = header.properties[LIST_MARKER]
        def listType = null
        switch (marker) {
        case '*':
        case '-':
            listType = Node.Type.UNORDERED_LIST
            break
        case '.':
            listType = Node.Type.ORDERED_LIST
            break
        default:
            // should not happen
            break
        }

        if (listType != nodeType) {
            return false
        }

        header?.type = listType

        return true
    }

    abstract protected AdocList createList()

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def lead = header?.properties[LIST_LEAD]
        def marker = header?.properties[LIST_MARKER]
        def markerLevel = header?.properties[LIST_MARKER_LEVEL]

        def level = 1
        if (parent.type == Node.Type.LIST_ITEM) {
            level = parent.parent.level + 1
        }

        def list = createList()

        list.lead = lead
        list.level = level
        list.marker = marker
        list.markerLevel = markerLevel

        fillBlockFromHeader(list, header)

        def newHeader = new BlockHeader(type: header.type,
                                        properties: header.properties)
        header = newHeader
        context.blockHeader = header

        context.keepHeader = true

        // add the paragraph ending checker
        context.paragraphEndingCheckers << this

        return list
    }

    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return [
            ChildParserInfo.zeroOrMore(ListItemParser.ID).findHeader().doBeforeParsing { latestContext, parent ->
                def result = false

                def header = latestContext.blockHeader
                if (header?.type?.isList()) {
                    result = true
                }

                if (!result) {
                    latestContext.paragraphEndingCheckers.pop()
                }

                return result
            }
        ]
    }

    public static TokenMatcher LIST_CONTINUATION_MATCHER =
        sequence([ optional(type(Token.Type.WHITE_SPACES)), literal('+')])

    @Override
    protected boolean doToEndParagraph(ParserContext context) {
        def end = false

        log.info '==== list parer plugin: end paragraph, next token = {}', context.lexer.peek()
        context.lexer.mark()
        def matched = LIST_CONTINUATION_MATCHER.matches(context, null)
        context.lexer.reset()

        if (matched) {
            // is list continuation
            /**
             * first list paragraph
             * +
             * next list paragraph
             */
            end = true

            def token = context.lexer.peek()
            if (token.type == Token.Type.WHITE_SPACES) {
                context.permProperties.listContinuationLead = token.value
            }
        } else {
            // check block header for every line
            def header = nextBlockHeader(context)

            log.info '==== list parser plugin: check header = {}', header
            /**
             * . first list paragraph
             * * next list paragraph
             */
            // or is next list item
            if (header?.type?.isList()) {
                end = true
            }
        }

        return end
    }

    /**
     * Whether the line is the start of a list, like
     *
     * *** abc
     * - abc
     * .. abc
     *
     * @return the type of list
     *         the list marker, *, - or .
     *         the level of the list
     *         the start index of the first line of the list item content
     */
    protected static List isListLine(String line) {
        if (line == null) {
            return [ null, null, null, -1, null ]
        }

        def m = LIST_PATTERN.matcher(line)
        if (!m.matches()) {
            return [ null, null, null, -1, null ]
        }

        Node.Type type = null

        def lead = m[0][1]
        def markers = m[0][2]
        def contentStart = m.start(3);
        int markerLevel = markers.length()

        def marker = markers[0]
        switch (marker) {
        case '*':
        case '-':
            type = Node.Type.UNORDERED_LIST
            break
        case '.':
            type = Node.Type.ORDERED_LIST
            break
        default:
            // should not happen
            break
        }

        return [ type, lead, marker, markerLevel, contentStart ]
    }

    /**
     * Whether a line is the list continuation, like
     *
     * +
     *
     * or
     *
     *    +
     *
     * @return leading spaces if is a list continuation, or null
     */
}
