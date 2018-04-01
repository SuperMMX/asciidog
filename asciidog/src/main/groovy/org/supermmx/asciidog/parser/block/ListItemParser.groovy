package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.block.ListParserPlugin.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.ListItem
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.lexer.Token
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
    protected boolean doCheckStart(ParserContext context, BlockHeader header, boolean expected) {
        log.debug('Expected: {}, Token: {}, Header: {}', expected, context.lexer.peek(), header)
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

        def lexer = context.lexer
        def token = lexer.next()
        if (token.type == Token.Type.WHITE_SPACES) {
            lexer.next()
        }

        lexer.next()

        log.trace '==== list item next token = {}', lexer.peek()
        return listItem
    }

    @Override
    protected List<ChildParserInfo> doGetChildParserInfos(ParserContext context) {
        return [
            ChildParserInfo.one(ParagraphParser.ID),
            ChildParserInfo.find().doBeforeParsing { newContext, parent ->
                def result = false
                def header = newContext.blockHeader

                if (header?.type?.isList()) {
                    // whether the list is a child list or a list item of the same level
                    def lead = header.properties[LIST_LEAD]
                    def marker = header.properties[LIST_MARKER]
                    def markerLevel = header.properties[LIST_MARKER_LEVEL]

                    if (!isListItem(parent, lead, marker, markerLevel)) {
                        // the list doesn't belong to one of the parent list
                        result = true
                    }
                } else if (header?.type != Node.Type.SECTION) {
                    // other blocks than a section
                    def continuationLead = newContext.permProperties.listContinuationLead
                    if (continuationLead != null
                        && continuationLead == parent.parent.lead) {
                        result = true
                        newContext.permProperties.listContinuationLead = null
                    }
                }

                return result
            }
        ]
    }

    /**
     * Whether the marker and marker level represent a new list
     * or an item of one of the ancestor lists, by checking the
     * marker and the marker level
     *
     * @return true if the list represented by the marker and markerLevel
     *         is a list item of one of the parent list, which should
     *         have the same marker and markerLevel
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
