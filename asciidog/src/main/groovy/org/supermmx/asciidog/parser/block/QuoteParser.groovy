package org.supermmx.asciidog.parser.block

import static org.supermmx.asciidog.parser.TokenMatcher.*

import org.supermmx.asciidog.ast.Block
import org.supermmx.asciidog.ast.Node
import org.supermmx.asciidog.ast.Quote
import org.supermmx.asciidog.parser.ParserContext
import org.supermmx.asciidog.parser.block.BlockParserPlugin.BlockHeader

import groovy.util.logging.Slf4j

import org.slf4j.Logger

@Slf4j
@Slf4j(value='userLog', category="AsciiDog")
class QuoteParser extends StyledBlockParser {
    static final String ID = 'plugin:parser:block:styled:quote'
    static final String QUOTE_STYLE = 'quote'
    static final String QUOTE_DELIMITER = '____'

    QuoteParser() {
        nodeType = Node.Type.QUOTE_BLOCK
        id = ID

        style = QUOTE_STYLE
        delimiter = QUOTE_DELIMITER
    }

    @Override
    protected Block doCreateBlock(ParserContext context, Block parent, BlockHeader header) {
        return new Quote()
    }
}
