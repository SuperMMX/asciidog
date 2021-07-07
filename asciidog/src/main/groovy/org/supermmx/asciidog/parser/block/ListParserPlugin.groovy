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

    static final Closure CHECK_ACTION = { String name, ParserContext context, Map<String, Object> props, boolean matched ->
        if (!matched) {
            return
        }

        def header = props.header
        def tokens = context.lexer.tokensFromMark

        if (name == 'lead') {
            // lead
            def lead = ''

            if (tokens.size() > 0) {
                lead = tokens[0].value
            }

            header.properties[LIST_LEAD] = lead
        } else if (name == 'mark') {
            def marker = context.lexer.joinTokensFromMark()
            def markerToken = context.lexer.tokensFromMark.last()

            // FIXME: handle start number for ordered list
            header.properties[LIST_MARKER] = markerToken.value[-1]
            header.properties[LIST_MARKER_LEVEL] = markerToken.value.length()
        }
    }

    private TokenMatcher checkMatcher
    protected TokenMatcher markerMatcher

    @Override
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        log.debug '==== list parser check start: header = {}', header
        if (header?.type != null
            && header?.type != nodeType) {
            return false
        }

        if (header?.type == nodeType) {
            return true
        }

        if (checkMatcher == null) {
            checkMatcher = sequence([
                optional('lead', type(Token.Type.WHITE_SPACES)),
                match('mark', { closureContext, props, valueObj ->
                    return markerMatcher.matches(closureContext, props, false)
                }),
                type(Token.Type.WHITE_SPACES),
                not(type(Token.Type.EOL), true)
            ])
        }
        def isStart = checkMatcher.matches(context, ["header": header], false, CHECK_ACTION)

        if (!isStart) {
            return false
        }

        header?.type = nodeType

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

    static LIST_CONTINUATION_LEAD_MATCHER_NAME = 'listContinuationLead'
    static TokenMatcher LIST_CONTINUATION_MATCHER = sequence([
        optional(LIST_CONTINUATION_LEAD_MATCHER_NAME, type(Token.Type.WHITE_SPACES)),
        literal('+'),
        type(Token.Type.EOL)
    ])

    @Override
    protected boolean doToEndParagraph(ParserContext context) {
        def end = false

        log.trace '==== list parer plugin: end paragraph, next token = {}', context.lexer.peek()

        def matched = LIST_CONTINUATION_MATCHER.matches(context, [:], false, { name, actionContext, props, matched ->
            if (!matched) {
                // no list continuation
                context.permProperties.listContinuationLead = null
                return
            }
            if (name == LIST_CONTINUATION_LEAD_MATCHER_NAME) {
                context.permProperties.listContinuationLead = actionContext.lexer.joinTokensFromMark()
            }
        })

        log.trace  '==== continuation match result = {}', matched

        if (matched) {
            // is list continuation
            /**
             * first list paragraph
             * +
             * next list paragraph
             */
            end = true
        } else {
            // check block header for every line
            def header = nextBlockHeader(context)

            log.trace '==== list parser plugin: check header = {}', header
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
}
