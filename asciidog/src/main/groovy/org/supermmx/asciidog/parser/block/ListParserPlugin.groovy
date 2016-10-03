package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
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
    static final String LIST_CONTENT_START = 'listContentStart'

    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        if (header?.type != null
            && header?.type != nodeType) {
            return false
        }

        // check list
        def (listType, listLead, listMarker, markerLevel, listContentStart) = isListLine(line)
        if (listType == null) {
            return false
        }

        header?.with {
            type = listType
            properties[LIST_LEAD] = listLead
            properties[LIST_MARKER] = listMarker
            properties[LIST_MARKER_LEVEL] = markerLevel
            properties[LIST_CONTENT_START] = listContentStart
        }

        return (listType == nodeType)
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

        def newHeader = new BlockHeader()
        newHeader.with {
            type = header.type
            properties = header.properties
        }
        header = newHeader
        context.blockHeader = header

        context.keepHeader = true

        // add the paragraph ending checker
        context.paragraphEndingCheckers << this

        return list
    }

    @Override
    protected String doGetNextChildParser(ParserContext context, Block block) {
        def childParser = null

        def header = nextBlockHeader(context)

        // the list item is detected as list
        if (header?.type?.isList()) {
            context.expected = true
            childParser = ListItemParser.ID
        }

        // remove the paragraph ending checker
        if (childParser == null) {
            context.paragraphEndingCheckers.pop()
        }

        return childParser
    }

    @Override
    boolean toEndParagraph(ParserContext context, String line) {
        def end = false

        // is list continuation
        if (isListContinuation(line) != null) {
            end = true
        } else {
            // check block header for every line
            header = nextBlockHeader(context)

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
    public static String isListContinuation(String line) {
        if (line == null) {
            return null
        }

        def m = LIST_CONTINUATION_PATTERN.matcher(line)
        if (!m.matches()) {
            return null
        }

        String lead = m[0][1]

        return lead
    }

}
