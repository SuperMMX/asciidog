package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.Reader
import org.supermmx.asciidog.ast.AdocList
import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Document
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.UnOrderedList
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.TokenMatcher
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class UnOrderedListParser extends ListParserPlugin {
    static final String ID = 'plugin:parser:block:list:unordered'

    static final TokenMatcher MARKER_MATCHER = firstOf([
        literal('-'),
        regex('\\*{1,5}')
    ])

    UnOrderedListParser() {
        nodeType = Node.Type.UNORDERED_LIST
        id = ID
        markerMatcher = MARKER_MATCHER
    }

    @Override
    protected AdocList createList() {
        return new UnOrderedList()
    }
}
