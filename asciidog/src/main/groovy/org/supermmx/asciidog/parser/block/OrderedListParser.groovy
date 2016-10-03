package org.supermmx.asciidog.parser.block

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.OrderedList
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class OrderedListParser extends ListParserPlugin {
    static final String ID = 'plugin:parser:block:list:ordered'

    OrderedListParser() {
        nodeType = Node.Type.ORDERED_LIST
        id = ID
    }

    @Override
    protected AdocList createList() {
        return new OrderedList()
    }
}
