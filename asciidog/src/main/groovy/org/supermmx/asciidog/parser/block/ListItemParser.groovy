package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.block.ListParserPlugin.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.parser.ParserContext

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class ListItemParser extends BlockParserPlugin {
    static final String ID = 'plugin:parser:block:list:list_item'

    ListItemParser() {
        nodeType = Node.Type.LIST_ITEM
        id = ID
    }

    @Override
    protected boolean doCheckStart(String line, BlockHeader header, boolean expected) {
        log.debug('Expected: {}, Line: {}, Header: {}', expected, line, header)
        return expected && header?.type?.isList()
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        def marker = header?.properties[LIST_MARKER]
        def markerLevel = header?.properties[LIST_MARKER_LEVEL]

        if (parent != null) {
            // not the same level
            if (marker != parent.marker
                || markerLevel != parent.markerLevel) {
                return null
            }
        }

        def listItem = new ListItem()
        fillBlockFromHeader(listItem, header)

        def reader = context.reader
        def line = reader.peekLine()
        def index = header?.properties[LIST_CONTENT_START]

        // skip the list markers and blanks before the real content
        reader.skipChars(index)

        return listItem
    }

    @Override
    protected String doGetNextChildParser(ParserContext context, Block block) {
        def childParser = null

        def lastParser = context.lastParserId
        log.debug('Last parser = {}', lastParser)
        if (lastParser == null) {
            // first one is considered as a simple paragraph
            childParser = ParagraphParser.ID
        } else {
            def header = nextBlockHeader(context)
            log.debug('Next header = {}', header)

            if (header?.type == Node.Type.SECTION) {
                // definitely need to stop
            } else if (header?.type?.isList()) {
                // whether the list is a child list or a list item of the same level
                def lead = header.properties[LIST_LEAD]
                def marker = header.properties[LIST_MARKER]
                def markerLevel = header.properties[LIST_MARKER_LEVEL]

                if (isListItem(block, lead, marker, markerLevel)) {
                    // the list belongs to one of the parent list
                } else {
                    // a new list as child
                    childParser = header?.parserId
                }
            } else {
                childParser = header?.parserId
            }
        }

        log.debug('Child parser = {}', childParser)

        context.lastParserId = childParser

        return childParser
    }

    /**
     * Whether the marker and marker level represent a new list
     * or an item of one of the ancestor lists, by checking the
     * marker and the marker level
     */
    protected boolean isListItem(Block parent, String lead, String marker, int markerLevel) {
        boolean result = false
        boolean found = false

        while (!found && parent != null) {
            switch (parent.type) {
            case Node.Type.LIST_ITEM:
                break
            case Node.Type.ORDERED_LIST:
            case Node.Type.UNORDERED_LIST:
                if (parent.marker == marker
                    && parent.markerLevel == markerLevel) {
                    found = true
                    result = true
                }
                break
            default:
                found = true
                break
            }

            parent = parent.parent
        }

        return result
    }
}
